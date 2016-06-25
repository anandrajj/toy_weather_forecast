package org.weather.model.forecast

import org.weather.model.data.GetData
import org.weather.model.data.CommonData

/**
 * @author anandraj
 * @written 23 June, 2016
 * @description Functions to support the Initial processing, such as parsing & validating parms, forcast date,
 * downloading data, of the application
 */

object InitialProcess {

  /*
   * Regex pattern for the key of each paramter value.
   */
  private val argNamePattern = "(--)([a-zA-Z0-9_]*)".r

  /*
   * Function the check all mandatory parm required is supplied as arguments 
   */

  private def checkMandatoryParms(argsMap: Map[String, String], mandatoryUserParms: List[String]) {

    mandatoryUserParms.foreach(x => if (!argsMap.contains(x))
      throw new IllegalArgumentException("Mandatory Parm %s is not found".format(x)))
  }

  /*
   * Function to parse the parms & validate parms.
   * Converts args of fomrat (--datSource local) to Map("dataSource" -> "local")
   * 
   */
  def getArgsAsMap(args: Array[String], userMandatoryParms: List[String]) = {
    if ((args.size % 2) == 1) throw new IllegalArgumentException("Number of args must be even. But odd number of arguments found!!")

    val argsMap = args.grouped(2).map(x => {
      val y = (x(0), x(1))

      y match {
        case (argNamePattern(hyphens, argName), value) => (argName, value)
        case _                                         => throw new IllegalArgumentException("""argName pattern is invalid. It must start with -- followed by Alphanumeric. Eg --Parm1""")
      }
    }).toMap

    println("Input Args found:")
    println("-----------------")

    argsMap.foreach(x => println("%s -> %s".format(x._1, x._2)))

    checkMandatoryParms(argsMap, userMandatoryParms)

    argsMap
  }

  /*
   * Validate input year and month.
   * 1. Validates they are numeric
   * 2. Validates month is between 1 & 12
   * 3. Validate if the date is between min & max range. 
   * 
   */
  private def validateFormatDate(year: String, month: String)(implicit argsMap: Map[String, String]): Int = {

    require((year + month).forall(_.isDigit) && //Check all are digits in date
      (1 to 12).contains(month.toInt),
      "Year or Month is not numeric or month is not in valid range") //Check month is between 1 & 12

    val date = "%04d".format(year.toInt) + "%02d".format(month.toInt)
    
    val dateRange = GetData.getMaxMinDate(argsMap.getOrElse("dateRange", CommonData.defaultDateRange))

    //Check if the date is between date ranges specified
    require(date.toInt >= dateRange.min && date.toInt <= dateRange.max,
      s"Input date is not between min ($dateRange.min) & max($dateRange.max) range") 

    date.toInt
  }

  /*
   * From the input year and month, construct the date format as (year + month) which is required to download
   * observations from bom and return date as Int.
   * 
   */
  def getDate(implicit argsMap: Map[String, String]): Int = {
    val year = argsMap("year")
    val month = argsMap("month")

    val date = validateFormatDate(year, month)
    println(s"Date Sucessfully parsed & validated. $date")

    date
  }

  /*
   * If data source is bom, then download the data for give date & cities. 
   * else Do Nothing 
   * 
   */

  def downlaodObservationData(cityToCodeMapping: Map[String, CommonData.codes], date: Int, dataSource: String) = {

    dataSource match {
      case "bom" => for (city <- cityToCodeMapping) {
        GetData.fileDownloader(date, city._2)
        val cityName = city._1
        println(s"$cityName -> Sucessfully downloaded observations !!")
      }
      case _ => Unit
    }

  }

}