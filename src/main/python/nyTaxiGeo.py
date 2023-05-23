import geopandas as gpd
import contextily as cx
import matplotlib.pyplot as plt

nyzones = gpd.read_file('/home/rs/Documents/prac_inz/spark/ny-taxi-zone-relevance/resources/taxi_zones.shp')
nyboroughs = gpd.read_file(gpd.datasets.get_path('nybb'))

df_nyzones = nyzones.to_crs(epsg=4326)
df_nyboroughs = nyboroughs.to_crs(epsg=4326)

ax = df_nyzones.plot(figsize=(12, 12), alpha=0.3, edgecolor='r')
df_nyboroughs.plot(ax=ax, column='BoroName', legend=True, alpha=0.5, cmap='Accent')

df_nyzones.apply(lambda x: ax.annotate(text=x['OBJECTID'],
                                       xy=x.geometry.centroid.coords[0],
                                       ha='center', weight='normal',
                                       size=6), axis=1)

cx.add_basemap(ax, zoom=12, source=cx.providers.Stamen.TonerLite, crs=df_nyboroughs.crs.to_string())
ax.set_xlabel('Longitude')
ax.set_ylabel('Latitude')
plt.show()