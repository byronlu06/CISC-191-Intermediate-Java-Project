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
 * Represents a single day of weather data, including temperature and humidity.
 * Provides methods to calculate the Heat Index and determine if the day qualifies as a heat day based on a temperature threshold.
 */

import java.time.LocalDate;

public class WeatherRecord {
	
    private final LocalDate date;
    private final double maxTemp;
    private final double minTemp;
    private final double humidityPercent;

    public WeatherRecord(LocalDate date, double maxTemp, double minTemp, double humidityPercent) {
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.humidityPercent = humidityPercent;
    }


	/**
	 * @return the date
	 */
	public LocalDate getDate()
	{
		return date;
	}


	/**
	 * @return the maxTemp
	 */
	public double getMaxTemp()
	{
		return maxTemp;
	}


	/**
	 * @return the minTemp
	 */
	public double getMinTemp()
	{
		return minTemp;
	}


	/**
	 * @return the humidityPercent
	 */
	public double getHumidityPercent()
	{
		return humidityPercent;
	}
	

    // Heat index calculation estimate
    public double heatIndex() {
        double T = maxTemp, R = humidityPercent;
        double HI = -42.379 + 2.04901523*T + 10.14333127*R
            - 0.22475541*T*R - 0.00683783*T*T - 0.05481717*R*R
            + 0.00122874*T*T*R + 0.00085282*T*R*R - 0.00000199*T*T*R*R;
        return HI;
    }

    public boolean isHeatDay(double threshold) {
        return heatIndex() >= threshold;
    }
}
