
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
 * Stores calculated statistics from the heat risk analysis, such as averages,
 * maximums, counts, and longest streaks of heat days.
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HeatSummary
{
	public int totalDays;
	public int skippedRowCount;
	public int heatDayCount;
	public double averageHeatIndex;
	public double maxHeatIndex;
	public int longestStreak;
	public LocalDate streakStart, streakEnd;
	
	// New GUI with report friendly rows
    public final List<String[]> tableRows = new ArrayList<>();

    public void addRow(String date, String maxT, String hum, String hi, String flag) {
        tableRows.add(new String[]{ date, maxT, hum, hi, flag });
    }
}