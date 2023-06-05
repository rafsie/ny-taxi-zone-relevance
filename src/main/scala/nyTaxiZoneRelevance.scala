import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.date_format
import org.graphframes.GraphFrame

object nyTaxiZoneRelevance extends App {

  val spark = SparkSession.builder()
    .config("spark.master", "local[*]")
    .appName("nyTaxiZoneRelevance")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val nyZonesDF = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("/home/rs/Documents/prac_inz/spark/ny-taxi-zone-relevance/resources/taxi_zones_geo.csv")
    .select($"LocationID".as("id"),
      $"zone".as("zone"),
      $"borough".as("borough"),
      $"X".as("longitude"),
      $"Y".as("latitude"))

  val nyTaxiDF = spark.read.format("parquet")
    .option("header", "true")
    .option("mode", "FAILFAST")
    .option("inferSchema", "true")
    .load("/home/rs/Documents/prac_inz/spark/ny-taxi-zone-relevance/resources/part-r-00000-ec9cbb65-519d-4bdb-a918-72e2364c144c.snappy.parquet")
    .select($"pickup_taxizone_id".as("src"), $"dropoff_taxizone_id".as("dst"),
      date_format($"pickup_datetime", "yyyy-MM-dd").as("time"))
    .where($"src".isNotNull && $"dst".isNotNull)
    .orderBy("time")

  val distTime = nyTaxiDF.select("time").distinct()

//  nyTaxiDF.show()
//  distTime.show()

  val taxiGF = GraphFrame(nyZonesDF, nyTaxiDF)

}
