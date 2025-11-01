/**
* Lead Author(s):
* @author Byron Lu; 5550223691
* <<Add additional lead authors here>>
*
* References:
* Morelli, R., & Walde, R. (2016).
* Java, Java, Java: Object-Oriented Problem Solving
* https://open.umn.edu/opentextbooks/textbooks/java-java-java-object-oriented-problem-solving
*
* <<Add more references here>>
*
* Version: 2025-10-30
*/

/**
 * Purpose:
 * Manages a collection of WeatherRecord objects.
 * Handles loading data from CSV files, filtering records, and tracking skipped rows due to invalid data.
 */

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;

/**
 * Holds a list of WeatherRecord rows loaded from a CSV file.
 * Provides read only access and simple filtering.
 */
public class DataSet {

    private static final int EXPECTED_COLUMNS = 4; // date, maxTemp, minTemp, humidityPercent

    private final List<WeatherRecord> records = new ArrayList<>();
    private int skippedRowCount = 0;

    /**
     * Load weather data from a CSV with a single header row.
     * Expected columns: date, maxTemp, minTemp, humidityPercent
     */
    public static DataSet loadFromCsv(Path csvPath) throws IOException {
        DataSet dataSet = new DataSet();

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;

            // Skip the header
            line = reader.readLine();
            if (line == null) {
                // Empty file
                return dataSet;
            }

            // Read each data row
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",", -1); // keep empty strings

                // Basic shape check
                if (cols.length < EXPECTED_COLUMNS) {
                    dataSet.skippedRowCount++;
                    continue;
                }

                try {
                    LocalDate date = LocalDate.parse(cols[0].trim());
                    double maxTemp = Double.parseDouble(cols[1].trim());
                    double minTemp = Double.parseDouble(cols[2].trim());
                    double humidityPercent = Double.parseDouble(cols[3].trim());

                    dataSet.records.add(new WeatherRecord(date, maxTemp, minTemp, humidityPercent));
                } catch (NumberFormatException | DateTimeParseException e) {
                    // Bad number or date format skip this row but keep going
                    dataSet.skippedRowCount++;
                }
            }
        }

        return dataSet;
    }

    /**
     * Returns an unmodifiable view of all records.
     */
    public List<WeatherRecord> all() {
        return Collections.unmodifiableList(records);
    }

    /**
     * Returns a new list with only the records that pass the given test.
     */
    public List<WeatherRecord> filter(Predicate<WeatherRecord> test) {
        List<WeatherRecord> result = new ArrayList<>();
        for (WeatherRecord record : records) {
            if (test.test(record)) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * Number of CSV rows that were skipped due to errors.
     */
    public int getSkippedRowCount() {
        return skippedRowCount;
    }
}
