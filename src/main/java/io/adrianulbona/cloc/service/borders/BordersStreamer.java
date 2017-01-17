package io.adrianulbona.cloc.service.borders;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;

/**
 * Created by adrianulbona on 15/01/2017.
 */
public class BordersStreamer {

	private static final Path BORDERS_PATH = get("data/borders_wkt.csv.gz");

	public Stream<Border> stream() throws IOException {
		try (final InputStream inputStream = new GZIPInputStream(newInputStream(BORDERS_PATH))) {
			return csvParser().parseAll(inputStream).stream().map(line -> new Border(line[0], line[1], line[2]));
		}
	}

	private CsvParser csvParser() {
		final CsvParserSettings settings = new CsvParserSettings();
		settings.setMaxCharsPerColumn(100_000_000);
		return new CsvParser(settings);
	}
}
