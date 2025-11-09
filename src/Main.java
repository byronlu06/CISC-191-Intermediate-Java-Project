
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
 * Loads a sample CSV into a DataSet, filters by year/month, runs the
 * HeatRiskAnalyzer to compute a summary, and prints results
 */

import java.nio.file.Path;
import java.util.List;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		// Put a small CSV next to the project and adjust path as needed
		DataSet dataSet = DataSet.loadFromCsv(Path.of("C:\\Users\\byron\\OneDrive\\Desktop\\CISC-191-Intermediate-Java-Project\\src\\sample_weather.csv"));
		HeatRiskAnalyzer analyzer = new HeatRiskAnalyzer(dataSet);

		int year = 2025, month = 10;
		double threshold = 95.0; // Heat Index threshold
		List<WeatherRecord> days = analyzer.filter(year, month);
		HeatSummary s = analyzer.summarize(days, threshold, false);

		System.out.println("Loaded records: " + dataSet.all().size() + " (skipped: " + dataSet.getSkippedRowCount() + ")");
		System.out.println("Filtered days: " + days.size());
		System.out.println("Heat-day count: " + s.heatDayCount);
		System.out.println("Avg HI: " + s.averageHeatIndex);
		System.out.println("Max HI: " + s.maxHeatIndex);
		System.out.println("Longest streak: " + s.longestStreak);
		System.out.println();
		
		Path report = Path.of("summary.txt");
        new ReportWriter().write(report, s, year, month, threshold, true);
        System.out.println("Summary written to " + report.toAbsolutePath());
        System.out.println("Rows prepared: " + s.tableRows.size());
	}
}
