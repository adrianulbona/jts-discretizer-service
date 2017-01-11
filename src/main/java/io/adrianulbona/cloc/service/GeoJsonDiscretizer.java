package io.adrianulbona.cloc.service;

import ch.hsr.geohash.GeoHash;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.geojson.GeoJsonWriter;
import io.github.adrianulbona.jts.discretizer.DiscretizerFactory;
import io.github.adrianulbona.jts.discretizer.GeometryDiscretizer;
import io.github.adrianulbona.jts.discretizer.util.GeoHash2Geometry;
import io.github.adrianulbona.jts.discretizer.util.WGS84Point2Coordinate;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by adrianulbona on 08/01/2017.
 */
@RequiredArgsConstructor
public class GeoJsonDiscretizer implements BiFunction<String, Integer, String> {

	private final Function<String, Optional<Geometry>> geoJson2Geometry;
	private final DiscretizerFactory discretizerFactory;

	@Override
	public String apply(String geoJson, Integer precision) {
		final Geometry geometry = this.geoJson2Geometry.apply(geoJson)
				.orElseThrow(() -> new RuntimeException("Unable to parse geometry"));
		final GeometryDiscretizer<Geometry> discretizer = this.discretizerFactory.discretizer(geometry);
		return geoJson(discretizer.apply(geometry, precision));
	}

	protected String geoJson(Set<GeoHash> geoHashes) {
		final Set<Polygon> geometries = geoHashes
				.stream()
				.map(geoHash -> (Polygon) new GeoHash2Geometry(new WGS84Point2Coordinate()).apply(
						geoHash, new GeometryFactory()))
				.collect(toSet());
		final Polygon[] polygons = geometries.toArray(new Polygon[geometries.size()]);
		final MultiPolygon multiPolygon = new GeometryFactory().createMultiPolygon(polygons);
		return new GeoJsonWriter().write(multiPolygon);
	}
}
