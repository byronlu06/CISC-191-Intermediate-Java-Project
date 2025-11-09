
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
 * Performs the main analysis of weather data by filtering records, calculating
 * heat index metrics, and producing summarized results.
 * Demonstrates polymorphism by running multiple Metric computations on the same
 * dataset.
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Runs heat risk calculations on a DataSet
 * Filters by year/month, then computes summary statistics
 */
public class HeatRiskAnalyzer
{

	private final DataSet dataSet;
	private int minRunDays = 3; // used for future streak/run rules if needed

	public HeatRiskAnalyzer(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	/**
	 * Set the minimum run length for streak related logic
	 */
	public void setMinRunDays(int days)
	{
		this.minRunDays = Math.max(1, days);
	}
	
	// Filter and summarize in one call
	public HeatSummary summarizeFor(int year, int month, double threshold, boolean usePercentile) {
        List<WeatherRecord> days = filter(year, month);
        HeatSummary s = summarize(days, threshold, usePercentile);
        prepareRows(days, threshold, s);
        return s;
    }

	/**
	 * Return all records for the given year and month.
	 */
	public List<WeatherRecord> filter(int year, int month)
	{
		return dataSet.filter(r -> r.getDate().getYear() == year
				&& r.getDate().getMonthValue() == month);
	}
	
	// Fill the heat summary table rows for GUI reports
	public void prepareRows(List<WeatherRecord> days, double threshold, HeatSummary out) {
        out.tableRows.clear();
        for (WeatherRecord rec : days) {
            String date = rec.getDate().toString();
            String maxT = String.format("%.1f", rec.getMaxTemp());
            String hum  = String.format("%.0f", rec.getHumidityPercent());
            double hiVal = rec.heatIndex();
            String hi   = String.format("%.1f", hiVal);
            String flag = rec.isHeatDay(threshold) ? "Yes" : "No";
            out.addRow(date, maxT, hum, hi, flag);
        }
    }

	/**
	 * Compute summary metrics for the given records.
	 * 
	 * @param dayList       days to analyze
	 * @param threshold     Heat Index threshold for a "heat day"
	 * @param usePercentile if true, also compute p90 values
	 */
	public HeatSummary summarize(List<WeatherRecord> dayList, double threshold,
			boolean usePercentile)
	{

		HeatSummary summary = new HeatSummary();
		summary.totalDays = dayList.size();
		// skippedRowCount is tracked at file load time; surface separately in
		// UI
		summary.skippedRowCount = 0;

		// basic stats (avg HI, max HI, heat-day count)
		Metric basicStats = (records, thr, run, out) -> {
			if (records.isEmpty())
			{
				out.averageHeatIndex = 0;
				out.maxHeatIndex = 0;
				out.heatDayCount = 0;
				return;
			}
			double sumHI = 0;
			double maxHI = Double.NEGATIVE_INFINITY;
			int heatDayCount = 0;

			for (WeatherRecord rec : records)
			{
				double hi = rec.heatIndex();
				sumHI += hi;
				if (hi > maxHI) maxHI = hi;
				if (rec.isHeatDay(thr)) heatDayCount++;
			}
			out.averageHeatIndex = sumHI / records.size();
			out.maxHeatIndex = maxHI;
			out.heatDayCount = heatDayCount;
		};

		// longest heat streak (consecutive heat days)
		Metric longestStreak = (records, thr, run, out) -> {
			int current = 0, best = 0;
			LocalDate currentStart = null, bestStart = null, bestEnd = null;

			for (WeatherRecord rec : records)
			{
				if (rec.isHeatDay(thr))
				{
					if (current == 0) currentStart = rec.getDate();
					current++;
					if (current > best)
					{
						best = current;
						bestStart = currentStart;
						bestEnd = rec.getDate();
					}
				}
				else
				{
					current = 0;
				}
			}
			out.longestStreak = best;
			out.streakStart = bestStart;
			out.streakEnd = bestEnd;
		};

		// Build the list of metric steps to run
		List<Metric> steps = new ArrayList<>();
		steps.add(basicStats);
		steps.add(longestStreak);

		// Execute all metrics in order
		for (Metric m : steps)
		{
			m.compute(dayList, threshold, minRunDays, summary);
		}

		return summary;
	}
}
