import org.apache.spark.sql.SparkSession

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


  nyZonesDF.show()
}
