package org.weather.model.data

import sys.process._
import java.net.URL
import java.io.File
import scala.io.Source
import org.json4s._
import org.json4s.native.JsonMethods._
import com.typesafe.config.ConfigFactory
import java.io.FileNotFoundException

/**
 * @author anandraj
 * @written 22 June, 2016
 * @description Object to handle data needs of the application
 * 	1. Read data mapping file to get mapping between City -> (IATACode, BOM Code)
 * 	2. Download required historical weather data from Bureau of Meteorology(BOM) Australia.
 *
 */

object GetData {

  /* 
   * Get the Cities for which weather forecasting is done with mapping to IATA Codes and BOM File codes for the city.
   * Mapping is stored in the dataFolder defined above in file mapping.txt
   */
  def getMappingData(): Map[String, CommonData.codes] = {
    val mapping = for (
      line <- CommonData.cities.map(x => x.split(CommonData.defaultFieldSep))
    ) yield (line(0), CommonData.codes(line(1), line(2)))
    mapping.toMap
  }

  /*
   * Function to download weather data for given cities from bom Website for a given 
   * city and date(year & month)
   * eg:url for Darwin, June 2016 data - http://www.bom.gov.au/climate/dwo/201606/text/IDCJDW8014.201606.csv 
   * 
   * */

  def fileDownloader(date: Int, mapping: CommonData.codes) {
    val bomFileCode = mapping.bomCode
    val IATACode = mapping.IATACode
    val url = CommonData.bomBaseUrl + date + "/text/" + bomFileCode + "." + date + ".csv"
    try {
      new URL(url) #> new File(bomFileCode) !!
    } catch {
      case e: Exception => throw new Exception(s"Error when donloading from $url")
    }
  }

  /*
   * Function to get max & min date range between which the observation data is available for download 
   * to this application. If the input date for which the forecast must be predicted.
   */

  def getMaxMinDate(dateRange: String) = {
    //Read only first line from the date range file. Then split the files by default field Separator.   
    val dates = dateRange.split(CommonData.defaultFieldSep)

    require(dates.size == 2 && //Only a tuple of size 2 is supplied
      (dates(0) + dates(1)).forall(_.isDigit) && //Check all the digits is number.
      dates(0).toInt < dates(1).toInt, "date range is not valid") //Check if the left date is less than right

    CommonData.dateRange(dates(0).toInt, dates(1).toInt)

  }
  /*
   * Function to get the Lat, long & elevation form google API.
   */
  def getLatLong(city: String) = {
    implicit val formats = DefaultFormats
    val jsonGeolocation = parse(Source.fromURL("http://maps.googleapis.com/maps/api/geocode/json?address=" + city.replaceAll(" ", "%20"))
      .mkString)
    val latLongJson = (((jsonGeolocation \ "results")(0) \ "geometry") \ "location")
    val lat = (latLongJson \ "lat").extract[Double]
    val long = (latLongJson \ "lng").extract[Double]
    val jsonElevation = parse(Source.fromURL("http://maps.googleapis.com/maps/api/elevation/json?locations=" + lat + "," + long).mkString)
    val elevation = ((jsonElevation \ "results")(0) \ "elevation").extract[Double]
    Array("%.2f".format(lat), "%.2f".format(long), elevation.round).mkString(",")
  }

}