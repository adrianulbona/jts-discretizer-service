package io.adrianulbona.cloc.service;

import com.google.gson.Gson;
import com.vividsolutions.jts.io.ParseException;
import io.github.adrianulbona.jts.discretizer.DiscretizerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.io.IOException;

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

		enableCORS("*", "*", "*");

		post("discretize/geojson/:precision",
				(req, res) -> geoJsonDiscretizer.apply(geoJson(req), precision(req)));

		exception(NumberFormatException.class, (e, req, res) -> halt(SC_BAD_REQUEST, e.getMessage()));
		exception(IllegalArgumentException.class, (e, req, res) -> halt(SC_BAD_REQUEST, e.getMessage()));
		exception(ParseException.class, (e, req, res) -> halt(SC_BAD_REQUEST, e.getMessage()));

		LOGGER.info("Created Routes...");
	}

	private String geoJson(Request req) {
		return req.body();
	}

	private Integer precision(Request req) {
		return Integer.valueOf(req.params("precision"));
	}


	private static void enableCORS(final String origin, final String methods, final String headers) {

		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", origin);
			response.header("Access-Control-Request-Method", methods);
			response.header("Access-Control-Allow-Headers", headers);
			// Note: this may or may not be necessary in your particular application
			response.type("application/json");
		});
	}
}
