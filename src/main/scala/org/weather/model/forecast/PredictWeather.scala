package org.weather.model.forecast

import org.weather.model.data.{ FileOperations, GetData }

/**
 * @author anandraj
 * @written 23 June 2016
 * @Descriptoin:
 * 	 			Entry point for the weather forecast application, it collects the cities to which weather has to be forecasted,
 * downloads observation data. Then users ARIM time series analysis, to predict the various weather conditions like average
 * temperature(°C), Relative Humidity(%) and Pressure(hPa).
 *
 * Outlook of the day is predicted using max cloud cover in the forecast period, Average sunshine hours and the above mentioned
 * 3 predicted parameters.
 *
 */

object PredictWeather extends App {

  //Declare the mandatory parameters to be validated in the next step.
  val mandatoryParms = List("dataSource", "year", "month", "outFile")

  //Call reusable getArgsAsMap function  to convert. Argument pairs to Scala Maps.
  //It also performs validation to ensure all mandatory parms are provided as command line input.
  implicit val argsMap = InitialProcess.getArgsAsMap(args, mandatoryParms)

  //Get the cities to perform forecast for. It also acquires the bom observation file name for the city and
  //IATA Codes. As these are fixed & won't change, they are hard coded in the CommonData Object.
  val cityToCodeMapping = GetData.getMappingData

  //Using the input year & month argument, for the date and validate that is between give range.
  val date = InitialProcess.getDate

  //DataSource argument determined if the data has to be acquired from bom (Bureau of Meteorology)
  val dataSource = argsMap("dataSource")

  //Initial processing to download the bom (Bureau of Meteorology) 
  //observation data for the give    and cities in scope
  InitialProcess.downlaodObservationData(cityToCodeMapping, date, dataSource)

  //Extract the observations from the download data.
  val finalPredictions = PredictFunctions.performPrediction(cityToCodeMapping)

  //Write the predicted forecast to output file
  FileOperations.writeForecasts(finalPredictions, argsMap("outFile"))

}