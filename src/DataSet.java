
/**
 * Lead Author(s):
 * 
 * @author Byron Lu; 5550223691
 *         <<Add additional lead authors here>>
 *
 *         References:
 *         Morelli, R., & Walde, R. (2016).
 *         Java, Java, Java: Object-Oriented Problem Solving
 *         https://open.umn.edu/opentextbooks/textbooks/java-java-java-object-oriented-problem-solving
 *
 *         <<Add more references here>>
 *
 *         Version: 2025-10-30
 */

/**
 * Purpose:
 * Manages a collection of WeatherRecord objects.
 * Handles loading data from CSV files, filtering records, and tracking skipped
 * rows due to invalid data.
 */

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;

// Loads weather data from a CSV and stores it as WeatherRecord objects.
// Also keeps track of how many rows were skipped due to bad formatting.
public class DataSet
{
	// CSV columns: date, maxTemp, minTemp, humidityPercent
	private static final int EXPECTED_COLUMNS = 4;

	private final List<WeatherRecord> records = new ArrayList<>();
	private int skippedRowCount = 0;

	// Reads a CSV with 1 header row and returns a DataSet.
	public static DataSet loadFromCsv(Path csvPath) throws IOException
	{
		DataSet dataSet = new DataSet();

		try (BufferedReader reader = Files.newBufferedReader(csvPath))
		{
			// Skip header
			String line = reader.readLine();
			if (line == null)
			{
				// Empty file
				return dataSet;
			}

			// Read each data row
			while ((line = reader.readLine()) != null)
			{
				String[] cols = line.split(",", -1); // keep empty strings

				// Make sure row has enough columns
				if (cols.length < EXPECTED_COLUMNS)
				{
					dataSet.skippedRowCount++;
					continue;
				}

				try
				{
					LocalDate date = LocalDate.parse(cols[0].trim());
					double maxTemp = Double.parseDouble(cols[1].trim());
					double minTemp = Double.parseDouble(cols[2].trim());
					double humidityPercent = Double.parseDouble(cols[3].trim());

					dataSet.records.add(new WeatherRecord(date, maxTemp,
							minTemp, humidityPercent));
				}
				catch (NumberFormatException | DateTimeParseException e)
				{
					// Bad row -> skip it, keep going
					dataSet.skippedRowCount++;
				}
			}
		}

		return dataSet;
	}

	// All records (read-only view)
	public List<WeatherRecord> all()
	{
		return Collections.unmodifiableList(records);
	}

	// Returns only records that match the predicate
	public List<WeatherRecord> filter(Predicate<WeatherRecord> test)
	{
		List<WeatherRecord> result = new ArrayList<>();
		for (WeatherRecord record : records)
		{
			if (test.test(record))
			{
				result.add(record);
			}
		}
		return result;
	}

	// How many rows were skipped when loading the CSV
	public int getSkippedRowCount()
	{
		return skippedRowCount;
	}
}
