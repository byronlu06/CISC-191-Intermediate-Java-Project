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
 * Defines a functional interface for any heat risk calculation metric.
 * Allows flexibility to implement or pass different types of statistical calculations (e.g., average, streaks, percentiles) to the analyzer.
 */

import java.util.List;

@FunctionalInterface
public interface Metric {
    void compute(List<WeatherRecord> days, double threshold, int minRunDays, HeatSummary out);
}

