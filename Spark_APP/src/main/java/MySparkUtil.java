

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession ;

import java.util.List;

import org.apache.spark.SparkConf;

public class MySparkUtil {

	//A name for the spark instance. Can be any string
	private static String appName = "My Sample App";
	//Pointer / URL to the Spark instance - embedded
	private static String sparkMaster = "local[*]";
	
	private static JavaSparkContext spContext = null;
	private static SparkSession sparkSession = null;
	private static String tempDir = "file:///c:/temp/spark-warehouse";
	
	private static void getConnection() {
		
		if ( spContext == null) {	
			//Setup Spark configuration
			SparkConf conf = new SparkConf()
					.setAppName(appName)
					.setMaster(sparkMaster);
			
			//Set the hadoop.home.dir if you intend to run on Win32 platform
			//Also download winutils binaries from https://github.com/srccodes/hadoop-common-2.2.0-bin/archive/master.zip
			//System.setProperty("hadoop.home.dir", "c:\\Spark21\\winutils\\");	
			
			//Create Spark Context from configuration
			spContext = new JavaSparkContext(conf);
			
			 sparkSession = SparkSession
					  .builder()
					  .appName(appName)
					  .master(sparkMaster)
					  .config("spark.sql.warehouse.dir", tempDir)
					  .getOrCreate();
			 
		}
		
	}
	
	public static JavaSparkContext getContext() {
		
		if ( spContext == null ) {
			getConnection();
		}
		return spContext;
	}
	
	public static SparkSession getSession() {
		if ( sparkSession == null) {
			getConnection();
		}
		return sparkSession;
	}

}
