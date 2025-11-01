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
 * Handles writing heat risk summaries to a text file.
 * Outputs organized reports with statistics for easy sharing and review.
 */

import java.io.*;
import java.nio.file.*;

public class ReportWriter {
    public void write(Path out, HeatSummary s, int year, int month, double threshold, boolean usePercentile) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            bw.write("Heat Risk Summary\n");
            bw.write("Year: " + year + " Month: " + month + " Threshold: " + threshold + "\n\n");
            bw.write("Total Days: " + s.totalDays + "\n");
            bw.write("Heat-Day Count: " + s.heatDayCount + "\n");
            bw.write("Average HI: " + String.format("%.1f", s.averageHeatIndex) + "\n");
            bw.write("Max HI: " + String.format("%.1f", s.maxHeatIndex) + "\n");
            bw.write("Longest Streak: " + s.longestStreak + "\n");
        }
    }
}
