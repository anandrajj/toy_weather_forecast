package org.weather.model.forecast

import org.weather.model.data._
import org.joda.time._
import org.joda.time.format._
import java.util.TimeZone
import org.weather.model.data.CommonData
import org.joda.time.DateTimeComparator
import com.cloudera.sparkts.models.ARIMA
import breeze.linalg.Vector

object PredictFunctions {
  def getPlusOneday(bomObservations: List[CommonData.fields]) = {
    bomObservations.map(x => x.date.split("-"))
      .map(t => new DateTime(t(0).toInt, t(1).toInt, t(2).toInt, 12, 0, 0, 0))
      .sortWith((t1, t2) => DateTimeComparator.getInstance.compare(t1, t2) < 0)
      .last.plus(Period.days(1))
      .withZone(DateTimeZone.UTC)
      .toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z"))
  }
  
  def executeARIMAForecast(values: Array[Double]) = {
      ARIMA.fitModel(1, 1, 0, Vector(values))
      .forecast(Vector(values), 1)
      .toArray
      .last
      
  }
}

object PredictWeather extends App {

  val mandatoryParms = List("dataSource", "year", "month")

  implicit val argsMap = InitialProcess.getArgsAsMap(args, mandatoryParms)

  val cityToCodeMapping = GetData.getMappingData

  val date = InitialProcess.getDate
  val dataSource = argsMap("dataSource")

  //Initial processing to download the bom (Bureau of Meteorology) 
  //observation date for the give date and cities in scope
  InitialProcess.downlaodObservationData(cityToCodeMapping, date, dataSource)

  //Extract the observations from the download data.
  val city = "DARWIN"
  val bomObservations = FileOperations.getBomObservations(cityToCodeMapping(city).bomCode)
  val tPlus1Date = PredictFunctions.getPlusOneday(bomObservations)

  val predictedAverageTemp = PredictFunctions.executeARIMAForecast(bomObservations.map(x => (x.max_temp + x.min_temp)/2).toArray)
  val predictedPressure = PredictFunctions.executeARIMAForecast(bomObservations.map(x => x.pressure).toArray)
  val predictedRH = PredictFunctions.executeARIMAForecast(bomObservations.map(x => x.relative_humidity.toDouble).toArray)
  println(Array(cityToCodeMapping(city).IATACode, tPlus1Date, "%.2f".format(predictedAverageTemp), "%.2f".format(predictedPressure), predictedRH.round).mkString(CommonData.defaultWriteSep))
  
}