package io.adrianulbona.cloc.service;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.geojson.GeoJsonReader;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Created by adrianulbona on 08/01/2017.
 */
public class GeoJsonParser implements Function<String, Optional<Geometry>> {

	@Override
	public Optional<Geometry> apply(String geoJson) {
		try {
			return of(new GeoJsonReader().read(geoJson));
		} catch (ParseException e) {
			return empty();
		}
	}
}
