package org.weather.model.data

import scala.io.Codec

/**
 * @author anandraj
 * @written 23 June, 2016
 * @description
 * 		Object to hold some of the Common data structures & constants used in various places of the application.
 */
object CommonData {

  val bomBaseUrl = "http://www.bom.gov.au/climate/dwo/" //url prefix for bom data.
  val bomFileCodec = Codec("windows-1252") //Codec of bom data file.

  val defaultFieldSep = "\\|" //psv field separator when reading & splitting.
  val defaultWriteSep = "|" //pipe separator when writing 
  val bomFileSep = "," //Field separator for bom observations data.

  //Date range where the bom data is available. This can be overridden by dateRange command line parm.
  val defaultDateRange = "201505|201606"

  //Case classes to hold related data together.
  case class codes(IATACode: String, bomCode: String)
  case class dateRange(min: Int, max: Int) //Max & Min date ranges.

  //Fileds used in the forecast. 
  case class fields(date: String, //Field 1
                    min_temp: Double, //Field 2
                    max_temp: Double, //Field 3
                    rainfall: Double, //Field 4
                    sunshine: Double, //Field 6
                    relative_humidity: Integer, //Field 11
                    cloud_amount: Integer, //Field 12
                    pressure: Double) //Field 15

  //Array of the cities with corresponding IATACodes and bom file names
  //IATA Codes are collected from http://www.iata.org/publications/Pages/code-search.aspx 
  //As there is no API available. The codes are hard coded here.
  //Ideally these values have to be in config db. To keep the execution simple
  //exercise they are hard-coded here.

  val mapArray = Array(
    "CANBERRA|CBR|IDCJDW2801",
    "SYDNEY|SYD|IDCJDW2124",
    "WOLLONGONG|WOL|IDCJDW2146",
    "MELBOURNE|MEL|IDCJDW3033",
    "BRISBANE|BNE|IDCJDW4019",
    "CAIRNS|CNS|IDCJDW4024",
    "GOLD COAST|OOL|IDCJDW4050",
    "ADELAIDE|ADL|IDCJDW5002",
    "PERTH|PER|IDCJDW6111",
    "HOBART|HBA|IDCJDW7021",
    "DARWIN|DRW|IDCJDW8014")

}