package org.weather.model.data

import scala.io.Source
import java.io.PrintWriter
import java.io.File
import java.io.PrintWriter
import java.io.FileNotFoundException

object FileOperations {

  private def filePath(implicit bomCode: String) = bomCode
  private val dataPattern = "^,[0-9]{4}-[0-9]{2}-[0-9]{1,2},".r

  /*
   * From the file read with below structure. It extracts the required fileds.
   * The files are defined in trait CommonData.fields
   * 
   * Structure of file downloaded from bom.
   * (,0)
   * ("Date",1)
   * ("Minimum temperature (°C)",2)
   * ("Maximum temperature (°C)",3)
   * ("Rainfall (mm)",4)
   * ("Evaporation (mm)",5)
   * ("Sunshine (hours)",6)
   * ("Direction of maximum wind gust ",7)
   * ("Speed of maximum wind gust (km/h)",8)
   * ("Time of maximum wind gust",9)
   * ("9am Temperature (°C)",10)
   * ("9am relative humidity (%)",11)
   * ("9am cloud amount (oktas)",12)
   * ("9am wind direction",13)
   * ("9am wind speed (km/h)",14)
   * ("9am MSL pressure (hPa)",15)
   * ("3pm Temperature (°C)",16)
   * ("3pm relative humidity (%)",17)
   * ("3pm cloud amount (oktas)",18)
   * ("3pm wind direction",19)
   * ("3pm wind speed (km/h)",20)
   * ("3pm MSL pressure (hPa)",21)
   * 
   */
  def getBomObservations(implicit bomCode: String) = {
 
    try{
    Source.fromFile(filePath)(CommonData.bomFileCodec).getLines
      .filter(x => dataPattern.findFirstIn(x).isDefined)
      .map(x => {
        val y = x.split(CommonData.bomFileSep)
        //If values missing, the assume zero.
        CommonData.fields(y(1),
          if (y(2) == "") 0.0D else y(2).toDouble,
          if (y(3) == "") 0.0D else y(3).toDouble,
          if (y(4) == "") 0.0D else y(4).toDouble,
          if (y(6) == "") 0.0D else y(6).toDouble,
          if (y(11) == "") 0 else y(11).toInt,
          if (y(12) == "") 4 else y(12).toInt + 1,
          if (y(15) == "") 0.0D else y(15).toDouble)

      }).toList
    } catch {
      case ex: FileNotFoundException => throw new FileNotFoundException(ex.getMessage 
          + ". Data source is not bom and files not found in the current folder")
    }
  }

  def writeForecasts(values: Array[String], outFile: String) = {
    val pw = new PrintWriter(new File(outFile))
    for (value <- values) pw.write(value + "\n")
    pw.close
  }

}