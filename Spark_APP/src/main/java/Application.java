import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.not;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.LabeledPoint;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Application {

	public static void main(String[] args) {

		Logger.getLogger("org").setLevel(Level.ERROR);
		Logger.getLogger("akka").setLevel(Level.ERROR);
		
		SparkSession spSession = MySparkUtil.getSession();

		final String USER = "root";
		final String PASS = "your password";
		String jdbcUrl = "jdbc:mysql://localhost:3306/sakila";
		String table1 = "table1";
		String table2 = "table2";
		String table3 = "table3";
		
		Properties dbProperties = new Properties();
		try {
			dbProperties.put("user", USER);
			dbProperties.put("password", PASS);
			dbProperties.put("useSSL", "false");
		} catch (Exception ex) {
		}
		
		/*--------------------------------------------------------------------------
		Drop MySQL Tables
		--------------------------------------------------------------------------*/
		dropTable(jdbcUrl,USER,PASS,table1);
		dropTable(jdbcUrl,USER,PASS,table2);
		dropTable(jdbcUrl,USER,PASS,table3);
		
		/*--------------------------------------------------------------------------
		Load Data
		--------------------------------------------------------------------------*/
		Dataset<Row> ccRawDf = spSession.read().option("header", "true").csv("data/cc-default-data.csv");
		System.out.println("Raw Data : ");
		ccRawDf.show(5);
		ccRawDf.printSchema();

		/*--------------------------------------------------------------------------
		Cleanse and Transform Data
		--------------------------------------------------------------------------*/

		// Remove lines that start with aaaa*
		Dataset<Row> ccCleanedDf = ccRawDf.filter(not(col("CUSTID").startsWith("aaaaa")));

		// Create the schema for the data to be loaded into Dataset.
		StructType ccSchema = DataTypes.createStructType(
				new StructField[] { DataTypes.createStructField("CustId", DataTypes.DoubleType, false),
						DataTypes.createStructField("LimitBal", DataTypes.DoubleType, false),
						DataTypes.createStructField("Sex", DataTypes.DoubleType, false),
						DataTypes.createStructField("Education", DataTypes.DoubleType, false),
						DataTypes.createStructField("Marriage", DataTypes.DoubleType, false),
						DataTypes.createStructField("Age", DataTypes.DoubleType, false),
						DataTypes.createStructField("AvgPayDur", DataTypes.DoubleType, false),
						DataTypes.createStructField("AvgBillAmt", DataTypes.DoubleType, false),
						DataTypes.createStructField("AvgPayAmt", DataTypes.DoubleType, false),
						DataTypes.createStructField("PerPaid", DataTypes.DoubleType, false),
						DataTypes.createStructField("Defaulted", DataTypes.DoubleType, false) });

		// Change data frame back to RDD
		JavaRDD<Row> rdd1 = ccCleanedDf.toJavaRDD().repartition(2);

		// Function to map.
		JavaRDD<Row> rdd2 = rdd1.map(new Function<Row, Row>() {

			@Override
			public Row call(Row iRow) throws Exception {

				// PR#06 - Round of age to range of 10
				Double age = Math.round(Double.valueOf(iRow.getString(5)) / 10.0) * 10.0;

				// Normalize Sex to 1 or 2
				Double sex;
				switch (iRow.getString(2)) {
				case "M":
					sex = 1.0;
					break;
				case "F":
					sex = 2.0;
					break;
				default:
					sex = Double.valueOf(iRow.getString(2));
				}

				// Find average billed amount
				double avgBillAmt = Math.abs((Double.valueOf(iRow.getString(12)) + Double.valueOf(iRow.getString(13))
						+ Double.valueOf(iRow.getString(14)) + Double.valueOf(iRow.getString(15))
						+ Double.valueOf(iRow.getString(16)) + Double.valueOf(iRow.getString(17))) / 6.0);

				// Find average pay amount
				double avgPayAmt = Math.abs((Double.valueOf(iRow.getString(18)) + Double.valueOf(iRow.getString(19))
						+ Double.valueOf(iRow.getString(20)) + Double.valueOf(iRow.getString(21))
						+ Double.valueOf(iRow.getString(22)) + Double.valueOf(iRow.getString(23))) / 6.0);

				// Find average pay duration
				double avgPayDuration = Math.round((Math.abs(Double.valueOf(iRow.getString(6)))
						+ Math.abs(Double.valueOf(iRow.getString(7))) + Math.abs(Double.valueOf(iRow.getString(8)))
						+ Math.abs(Double.valueOf(iRow.getString(9))) + Math.abs(Double.valueOf(iRow.getString(10)))
						+ Math.abs(Double.valueOf(iRow.getString(11)))) / 6.0);

				// Average percentage paid. add this as an additional field to
				// see
				// if this field has any predictive capabilities. This is
				// additional creative work that you do to see possibilities.
				double perPay = Math.round((avgPayAmt / (avgBillAmt + 1) * 100) / 25.0) * 25.0;

				Row retRow = RowFactory.create(Double.valueOf(iRow.getString(0)), Double.valueOf(iRow.getString(1)),
						sex, Double.valueOf(iRow.getString(3)), Double.valueOf(iRow.getString(4)), age, avgPayDuration,
						avgBillAmt, avgPayAmt, perPay, Double.valueOf(iRow.getString(24)));

				return retRow;
			}

		});

		// Create Data Frame back.
		Dataset<Row> ccXformedDf1 = spSession.createDataFrame(rdd2, ccSchema);
		System.out.println("Transformed Data :");
		ccXformedDf1.show(5);

		// Add Sex Name for the data Required for PR#02
		List<Gender> gender = new ArrayList<Gender>();
		gender.add(new Gender(1.0, "Male"));
		gender.add(new Gender(2.0, "Female"));
		Dataset<Row> genderDf = spSession.createDataFrame(gender, Gender.class);
		Dataset<Row> ccXformedDf2 = ccXformedDf1.join(genderDf, col("Sex").equalTo(col("sexId"))).drop(col("sexId"));

		// Add Education Name for the data Required for PR#03
		List<Education> education = new ArrayList<Education>();
		education.add(new Education(1.0, "Graduate"));
		education.add(new Education(2.0, "University"));
		education.add(new Education(3.0, "High School"));
		education.add(new Education(4.0, "Others"));
		Dataset<Row> educationDf = spSession.createDataFrame(education, Education.class);
		Dataset<Row> ccXformedDf3 = ccXformedDf2.join(educationDf, col("Education").equalTo(col("eduId")))
				.drop(col("eduId"));

		// Add Marriage Name for the data Required for PR#03
		List<Marriage> marriage = new ArrayList<Marriage>();
		marriage.add(new Marriage(1.0, "Single"));
		marriage.add(new Marriage(2.0, "Married"));
		marriage.add(new Marriage(3.0, "Divorced"));
		Dataset<Row> marriageDf = spSession.createDataFrame(marriage, Marriage.class);
		Dataset<Row> ccXformedDf4 = ccXformedDf3.join(marriageDf, col("Marriage").equalTo(col("marriageId")))
				.drop(col("marriageId"));

		System.out.println("Transformed and Joined Data : ");
		ccXformedDf4.show(5);

		/*--------------------------------------------------------------------------
		Do analysis as required by the problem statement
		--------------------------------------------------------------------------*/
		// Create a temp view
		ccXformedDf4.createOrReplaceTempView("CCDATA");

		// PR#02 solution
		Dataset<Row> PR02 = spSession
				.sql("SELECT row_number() over (order by sexname) as id,sexname, count(*) as Total, "
						+ " SUM(Defaulted) as Defaults, " + " ROUND(SUM(Defaulted) * 100 / count(*)) as Percent "
						+ " FROM CCDATA GROUP BY sexName");
		System.out.println("Solution for PR#02 :");
		PR02.show();

		PR02.write().mode("error").jdbc(jdbcUrl, table1, dbProperties);

		// PR#03 solution
		Dataset<Row> PR03 = spSession.sql("SELECT row_number() over (order by marriagename) as id, marriagename, eduname, count(*) as total,"
				+ " SUM(defaulted) as defaults, " + " ROUND(SUM(defaulted) * 100 / count(*)) as perdefault "
				+ " FROM CCDATA GROUP BY marriagename, eduname " + " ORDER BY 1,2");
		System.out.println("Solution for PR#03 : ");
		PR03.show();
		
		PR03.write().mode("error").jdbc(jdbcUrl, table2, dbProperties);
		
		// PR#04 solution
		Dataset<Row> PR04 = spSession.sql("SELECT AvgPayDur, count(*) as Total, " + " SUM(Defaulted) as Defaults, "
				+ " ROUND(SUM(Defaulted) * 100 / count(*)) as PerDefault "
				+ " FROM CCDATA GROUP BY AvgPayDur ORDER BY 1");
		System.out.println("Solution for PR#04 : ");
		PR04.show();

		// Do correlation analysis
		for (StructField field : ccSchema.fields()) {
			if (!field.dataType().equals(DataTypes.StringType)) {
				System.out.println("Correlation between Defaulted and " + field.name() + " = "
						+ ccXformedDf4.stat().corr("Defaulted", field.name()));
			}
		}

		/*--------------------------------------------------------------------------
		Prepare for Machine Learning
		--------------------------------------------------------------------------*/

		JavaRDD<Row> rdd3 = ccXformedDf4.toJavaRDD().repartition(2);

		JavaRDD<LabeledPoint> rdd4 = rdd3.map(new Function<Row, LabeledPoint>() {

			@Override
			public LabeledPoint call(Row iRow) throws Exception {

				Vector features = Vectors.dense(iRow.getDouble(2), iRow.getDouble(3), iRow.getDouble(4),
						iRow.getDouble(5), iRow.getDouble(6), iRow.getDouble(7), iRow.getDouble(8), iRow.getDouble(9));

				// Using customerID as label - a trick to add customer ID.
				// Will add defaulted later.
				LabeledPoint lp = new LabeledPoint(iRow.getDouble(0), features);

				return lp;
			}

		});

		Dataset<Row> ccLp = spSession.createDataFrame(rdd4, LabeledPoint.class);
		System.out.println("Labeled Point Data : ");
		ccLp.show(5);

		Dataset<Row> ccMap = ccXformedDf4.select(col("Custid"), col("Defaulted"));
		Dataset<Row> ccDefaultLp = ccLp.join(ccMap, col("label").equalTo(col("CustId"))).drop("label");

		System.out.println("Labeled Point with Defaulted and Customer ID");
		Dataset<Row> ccFinalLp = ccDefaultLp.repartition(2);
		ccFinalLp.show(5);

		// Do indexing
		StringIndexer indexer = new StringIndexer().setInputCol("Defaulted").setOutputCol("indexed");

		StringIndexerModel siModel = indexer.fit(ccFinalLp);
		Dataset<Row> indexedCCLp = siModel.transform(ccFinalLp);

		// Split the data into training and test sets (30% held out for
		// testing).
		Dataset<Row>[] splits = indexedCCLp.randomSplit(new double[] { 0.99, 0.01 });
		Dataset<Row> trainingData = splits[0];
		Dataset<Row> testData = splits[1];

		/*--------------------------------------------------------------------------
		Machine Learning - Classification
		--------------------------------------------------------------------------*/
		// PR#05 Do Predictions - to predict defaults. Use multiple
		// classification
		// algorithms to see which ones provide the best results

		// Setup evaluator
		MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator().setLabelCol("indexed")
				.setPredictionCol("prediction").setMetricName("accuracy");

		// Convert indexed labels back to original labels.
		IndexToString labelConverter = new IndexToString().setInputCol("indexed").setOutputCol("labelStr")
				.setLabels(siModel.labels());

		IndexToString predConverter = new IndexToString().setInputCol("prediction").setOutputCol("predictionStr")
				.setLabels(siModel.labels());

		// Do Decision Trees **********
		DecisionTreeClassifier dt = new DecisionTreeClassifier().setLabelCol("indexed").setFeaturesCol("features");
		DecisionTreeClassificationModel dtModel = dt.fit(trainingData);
		// Predict on test data
		Dataset<Row> dtRaw = dtModel.transform(testData);
		Dataset<Row> dtPredictions = predConverter.transform(labelConverter.transform(dtRaw));
	
		
		
		System.out.println("========================Sunil=======================================");
		System.out.println("Decision Tree output : ");
		System.out.println("dtPredictions.count() = " + dtPredictions.count());
		dtPredictions.show();
		System.out.println("testData.count() = " + testData.count());
		testData.show();
		System.out.println("========================Sunil=======================================");
		double dtAccuracy = evaluator.evaluate(dtPredictions);
		System.out.println("Decision Trees Accuracy = " + Math.round(dtAccuracy * 100) + " %");
		
		dtPredictions.drop("rawPrediction","probability","features","labelStr","predictionStr").write().mode("error").jdbc(jdbcUrl, table3, dbProperties);
		
	}

	public static void dropTable(String db, String user, String pwd, String table) {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(db, user, pwd);
			System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			System.out.println("Deleting table in given database...");
			stmt = conn.createStatement();

			String sql = "DROP TABLE " + table;

			stmt.executeUpdate(sql);
			System.out.println("Table  deleted in given database... " + table);
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try

	}
}
