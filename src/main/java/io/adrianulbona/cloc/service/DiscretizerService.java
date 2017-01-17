package io.adrianulbona.cloc.service;

import com.google.gson.Gson;
import com.vividsolutions.jts.io.ParseException;
import io.adrianulbona.cloc.service.borders.Border;
import io.adrianulbona.cloc.service.borders.BordersStreamer;
import io.github.adrianulbona.jts.discretizer.DiscretizerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.io.IOException;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static spark.Spark.*;

/**
 * Created by adrianulbona on 08/08/16.
 */
public class DiscretizerService {

	private final static Logger LOGGER = LoggerFactory.getLogger(DiscretizerService.class);

	public static void main(String[] args) throws IOException {
		new DiscretizerService();
	}

	public DiscretizerService() throws IOException {
		LOGGER.info("Initializing Service...");
		final GeoJsonDiscretizer geoJsonDiscretizer = new GeoJsonDiscretizer(new GeoJsonParser(),
				new DiscretizerFactoryImpl());

		final Map<String, Border> borders = new BordersStreamer().stream()
				.collect(toMap(Border::getCode, identity()));

		enableCORS();
		before("/*", (req, res) -> res.type("application/json"));

		post("discretize/geojson/:precision",
				(req, res) -> geoJsonDiscretizer.apply(geoJson(req), precision(req)));

		exception(NumberFormatException.class, (e, req, res) -> halt(SC_BAD_REQUEST, e.getMessage()));
		exception(IllegalArgumentException.class, (e, req, res) -> halt(SC_BAD_REQUEST, e.getMessage()));
		exception(ParseException.class, (e, req, res) -> halt(SC_BAD_REQUEST, e.getMessage()));

		final Gson gson = new Gson();
		get("border/:code", (req, res) -> borders.get(req.params("code")
				.toUpperCase()), gson::toJson);
		get("border", (req, res) -> borders.keySet(), gson::toJson);

		LOGGER.info("Created Routes...");
	}

	private String geoJson(Request req) {
		return req.body();
	}

	private Integer precision(Request req) {
		return Integer.valueOf(req.params("precision"));
	}


	private static void enableCORS() {
		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Request-Method", "*");
			response.header("Access-Control-Allow-Headers", "*");
		});
	}
}
