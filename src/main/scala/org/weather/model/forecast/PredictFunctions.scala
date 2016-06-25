package org.weather.model.forecast

import org.joda.time.{ DateTime, DateTimeComparator, DateTimeZone }
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import org.weather.model.data.{ CommonData, FileOperations, GetData }
import com.cloudera.sparkts.models.ARIMA
import breeze.linalg.Vector

/**
 * @author anandraj
 * @written 25 June, 2016
 * @description 
 * 			Organizes the functions used in performing the prediction functions 
 * 
 */

object PredictFunctions {

  /*
   * Determines the t+1th day for which the forecast is being performed. Here, n is the max date in the observations.
   * The date is converted from Australia Date to UTC format as the time is suffixed by 'Z' in the sampled output file.
   */
  def getPlusOneday(bomObservations: List[CommonData.fields]) = {
    bomObservations.map(x => x.date.split("-"))
      .map(t => new DateTime(t(0).toInt, t(1).toInt, t(2).toInt, 12, 0, 0, 0))
      .sortWith((t1, t2) => DateTimeComparator.getInstance.compare(t1, t2) < 0)
      .last.plus(Period.days(1))
      .withZone(DateTimeZone.UTC)
      .toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z"))
  }

  /*
   * For the input vector, executes the ARIMA function to forecast the next value
   */
  def executeARIMAForecast(values: Array[Double]) = {
    ARIMA.fitModel(1, 1, 0, Vector(values))
      .forecast(Vector(values), values.size + 1)
      .toArray
      .last

  }

  /*
   * Function to predict the outlook weather. This application classifies, the out look as Sunny, Rain, Cloudy, 
   * Cold, Partly Cloudy. 
   * 
   */
  def predictOutlook(predictedRH: Int, predicteCloudCover: Int, predictedSunShine: Double, predictedAverageTemp: Double) = {
    //Rain. When highly humid, high cloud cover and less sunshine.
    if (predictedRH > 80D && predicteCloudCover > 6 && predictedSunShine < 3) "Rain" 
    else if (predictedSunShine > 5) "Sunny" //When sunshine is for more than 5 hours then, Sunny.
    else if (predicteCloudCover > 6) "Cloudy" //When high cloud cover then cloudy.
    else if (predictedAverageTemp < 15) "Cold" //If temperature is below 15. Then Cold.
    else "Partly Cloudy" //If not everything else, then "Partly Cloudy"

  }

  /*
   * Core function to execute predictions for various measurements for each of the city.
   * 1. Reads the observations & extracts required fields
   * 2. Determines the date of Forecast in UTC format.
   * 3. Determine forecasted Temperature, Pressure, Relative humidity, cloud cover and sunshine.
   * 4. Then Determines the outlook.
   * 5. Acquires the GeoLocation Coordinates, Elevation and IATA codes for each of the city.
   * 6. All required values are stringed up and returned. 
   */
  
  def performPrediction(cityToCodeMapping: Map[String, CommonData.codes]) =
    for {
      city <- cityToCodeMapping.keys.toArray
      val bomObservations = FileOperations.getBomObservations(cityToCodeMapping(city).bomCode)
      val tPlus1Date = PredictFunctions.getPlusOneday(bomObservations)

      val predictedAverageTemp = PredictFunctions.executeARIMAForecast(bomObservations.map(x => (x.max_temp + x.min_temp) / 2).toArray)
      val predictedPressure = PredictFunctions.executeARIMAForecast(bomObservations.map(x => x.pressure).toArray)
      val predictedRH = PredictFunctions.executeARIMAForecast(bomObservations.map(x => x.relative_humidity.toDouble).toArray).round.toInt
      val predictedCloudCover = bomObservations.map(x => x.cloud_amount).max
      val predictedSunShine = ((bomObservations.map(x => x.sunshine).sum) / bomObservations.size).round.toInt

      val outlook = PredictFunctions.predictOutlook(predictedRH, predictedCloudCover, predictedSunShine, predictedAverageTemp)

      val geoCordinates = GetData.getLatLong(city)
    } yield Array(cityToCodeMapping(city).IATACode, geoCordinates, tPlus1Date, outlook, "%.2f".format(predictedAverageTemp), "%.2f".format(predictedPressure), predictedRH).mkString(CommonData.defaultWriteSep)
}