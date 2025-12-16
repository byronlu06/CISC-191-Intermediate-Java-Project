
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
import java.util.List;

// Does the actual analysis work: filtering + stats + streaks.
public class HeatRiskAnalyzer
{
	private final DataSet dataSet;

	// Not heavily used right now, but kept in case you add run rules later
	private int minRunDays = 3;

	public HeatRiskAnalyzer(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	// Sets min run length (future use)
	public void setMinRunDays(int days)
	{
		this.minRunDays = Math.max(1, days);
	}

	// GUI helper: filter + summarize + fill table rows
	public HeatSummary summarizeFor(int year, int month, double threshold)
	{
		List<WeatherRecord> days = filter(year, month);
		HeatSummary s = summarize(days, threshold);
		prepareRows(days, threshold, s);
		return s;
	}

	// Returns all records for a given year/month
	public List<WeatherRecord> filter(int year, int month)
	{
		return dataSet.filter(r -> r.getDate().getYear() == year
				&& r.getDate().getMonthValue() == month);
	}

	// Builds rows for the GUI table (all strings)
	public void prepareRows(List<WeatherRecord> days, double threshold,
			HeatSummary out)
	{
		out.tableRows.clear();

		for (WeatherRecord rec : days)
		{
			String date = rec.getDate().toString();
			String maxT = String.format("%.1f", rec.getMaxTemp());
			String hum = String.format("%.0f", rec.getHumidityPercent());

			double hiVal = rec.heatIndex();
			String hi = String.format("%.1f", hiVal);

			String flag = rec.isHeatDay(threshold) ? "Yes" : "No";
			out.addRow(date, maxT, hum, hi, flag);
		}
	}

	// Computes the main summary numbers (avg/max/count + longest streak)
	public HeatSummary summarize(List<WeatherRecord> dayList, double threshold)
	{
		HeatSummary summary = new HeatSummary();
		summary.totalDays = dayList.size();

		// Skipped rows are tracked by DataSet (GUI reads it from there)
		summary.skippedRowCount = 0;

		// Metric 1: averages/max + heat day count
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

		// Metric 2: longest streak of heat days
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

		// Run all metrics
		List<Metric> steps = new ArrayList<>();
		steps.add(basicStats);
		steps.add(longestStreak);

		for (Metric m : steps)
		{
			m.compute(dayList, threshold, minRunDays, summary);
		}

		return summary;
	}
}
