package org.weather.model.data

import org.scalatest.FunSuite
import scala.io.Source
import java.nio.file._

class TestGetDataAndFileOperations extends FunSuite {

  test("Test get City to Codes Mapping") {
    //Test to verify City Name, IATACode and Bom file names could be parsed
    val data = GetData.getMappingData.map(x => Array(x._1, x._2.IATACode, x._2.bomCode).mkString(CommonData.defaultWriteSep)).toArray
    assert(data.sorted.deep === CommonData.cities.sorted.deep)
  }

  test("File Download") {
    //Check that File for given date and bom FileName could download.
    GetData.fileDownloader(201605, CommonData.codes("CBR", "IDCJDW2801"))
    assert(Files.exists(Paths.get("IDCJDW2801")))
  }
  
  test("Parse DateRange") {
    //Test to confirm the get max & min date parsed values is equal to default
    //min and max values.
    val x = GetData.getMaxMinDate(CommonData.defaultDateRange)
    assert(x.min == 201505 && x.max == 201606)
  }
  
  test("Get Lat Long") {
     //Test that lat, long and elevation acquired is equal to 
     //actual values.
     assert(GetData.getLatLong("SYDNEY") === "-33.87,151.21,25")
  }
  
  test("File Read") {
    //Check that number of files read for Canberra in May month has 31 rows.
    assert(FileOperations.getBomObservations("IDCJDW2801").size === 31)
  }
}