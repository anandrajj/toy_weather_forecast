package org.weather.model.data

import scala.io.Codec

object CommonData {
  
  val bomBaseUrl = "http://www.bom.gov.au/climate/dwo/"
  val dataFolder = "src/main/resources/"
  val mappingFile = dataFolder + "mapping.txt"
  val maxMinDateFile = dataFolder + "daterange.txt"
  val bomFileCodec = Codec("windows-1252")
  
  val defaultFieldSep = "\\|"
  val defaultWriteSep = "|"
  val bomFileSep = ","

  case class codes(IATACode: String, bomCode: String)
  case class dateRange(min: Int, max: Int)
  case class fields(date: String, //Field 1
      min_temp: Double, //Field 2
      max_temp: Double, //Field 3
      rainfall: Double, //Field 4
      sunshine: Double, //Field 6
      relative_humidity: Integer, //Field 11
      cloud_amount: Integer, //Field 12
      pressure: Double) //Field 15

}