# ny-taxi-spark

This is an attempt to dynamically implement the PageRank algorithm in Apache Spark [GraphX](https://spark.apache.org/docs/latest/graphx-programming-guide.html) library. Static PageRank runs for a fixed number of iterations, while dynamic PageRank runs until the ranks converge stop changing by more than a specified tolerance (here it is set to default 0.01).

As a data set, routes of New York taxis from 2009-2016 were used, available for download in parquet format at [academictorrents.com](https://academictorrents.com/details/4f465810b86c6b793d1c7556fe3936441081992e)

It should be borne in mind that this is a large (above 35 GB compressed) data set, of which only one example file (5 days) has been placed in the resources directory, and the calculations, the results of which are presented as a map and table, concern only whole June 2016. Despite such a limited scope, the computation time on an old Dell i5 notebook with only 16 GB of RAM in the IntelliJ environment was over 24 minutes, and a noticeable problem was the sorting of results.

The complexity of the calculations can be demonstrated by the reduced DAG schedule of only one of the application tasks presented below:

![pageRank-DAG.jpg](img%2FpageRank-DAG.jpg)

The results of the calculations were displayed on the screen and saved in CSV files, one of which was then used to display map of the locations with PageRank >= 2.0 in the Zeppelin notebook - these are the 35 highest rated locations, which are also presented in the table. Practically the whole of Manhattan and the airports: Newark, LaGuardia and JFK attract attention here:

![pageRank-top35.png](img%2FpageRank-top35.png)
![pageRank-table.png](img%2FpageRank-table.png)

A set of files (taxi_zones.zip) from the [Taxi Zone Shapefile](https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page) was used as a source of additional data. It contains the boundaries of all zones in the shapefile (.shp) format, and was used to generate, using the attached Python script (nyTaxiGeo.py), a map of New York with boroughs marked in different colors, numbered taxi zones, longitude and latitude, for easier finding them from the table.

Thanks to the [MyGeodata Converter](https://mygeodata.cloud/converter/shp--to-latlong), the mentioned shapefile and additional .dbf, .shx and .prj files were also used to generate midpoint longitude and latitude for individual zones.

![borough-zones.png](img%2Fborough-zones.png)