import org.apache.spark.sql.{SaveMode, SparkSession}
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

  val taxiGF = GraphFrame(nyZonesDF, nyTaxiDF)

  spark.time {
    // Run PageRank until convergence to tolerance "tol".
    val results = taxiGF.pageRank.resetProbability(0.15).tol(0.01).run()

    val resultsDF = results.vertices
      .select($"id", $"borough", $"zone", $"pagerank")
      .sort($"pagerank".desc)
      .toDF()

    resultsDF.join(nyZonesDF, resultsDF("id") === nyZonesDF("id"))
      .select(resultsDF("id"), resultsDF("borough"), resultsDF("zone"),
        nyZonesDF("latitude"), nyZonesDF("longitude"), resultsDF("pagerank"))
      .coalesce(1)
//      Uncomment to save to .csv file
//      .write.option("header", value = true)
//      .mode(SaveMode.Overwrite)
//      .option("sep", ",")
//      .csv("/home/rs/Desktop/pagerank")
      .show(300, truncate = false)
  }

}
