package org.weather.model.forecast

import org.scalatest.FunSuite
import org.weather.model.data.CommonData
import java.nio.file.Files
import java.nio.file.Paths

class TestInitalProcess extends FunSuite {

  test("Test Mandotry Parms checking") {
    //Test to confirm if validation throws error valid valid message when mandatory parm is not supplied
    //This tests the private function InitialProcess.checkMandatoryParms
    val thrown = intercept[IllegalArgumentException] {
      InitialProcess.getArgsAsMap(Array("--test", "1"), List("test", "not-found"))
    }
    assert(thrown.getMessage == "Mandatory Parm not-found is not found")
  }

  test("Test to Check all parms are pased correctly") {
    //Test all parms supplied are parsed correctly and converted to map.
    val x = InitialProcess.getArgsAsMap(Array("--test", "1", "--test2", "2"), List("test", "test2"))
    assert(x("test") == "1" && x("test2") == "2")
  }

  test("Test invalid Month exception") {
    //Test month validation throws correct Exception & message
    val thrown = intercept[IllegalArgumentException] {
      InitialProcess.getDate(Map("year" -> "2016", "month" -> "13"))
    }

    assert(thrown.getMessage === "requirement failed: Year or Month is not numeric or month is not in valid range")
  }

  test("Test invalid Year exception") {
    //Test invalid year value in argument throws exception
    val thrown = intercept[IllegalArgumentException] {
      InitialProcess.getDate(Map("year" -> "year", "month" -> "12"))
    }
    assert(thrown.getMessage === "requirement failed: Year or Month is not numeric or month is not in valid range")
  }

  test("Test valid date out of range exception") {
    //Test exception is thrown if input date is out of default range.
    val thrown = intercept[IllegalArgumentException] {
      InitialProcess.getDate(Map("year" -> "2017", "month" -> "01"))
    }

    assert(thrown.getMessage === "requirement failed: Input date is not between min (dateRange(201505,201606).min) & max(dateRange(201505,201606).max) range")
  }

  test("Parsing of valid date") {
    //Test if dare parsing return correct date after validation.
    assert(InitialProcess.getDate(Map("year" -> "2016", "month" -> "01")) == 201601)
  }

  test("Test exception for invalid min & max dates") {
    //Check if invalid dateRange is supplied, correct exception is thrown
    val thrown = intercept[IllegalArgumentException] {
      InitialProcess.getDate(Map("year" -> "2016", "month" -> "01", "dateRange" -> "201606|201505"))
    }

    assert(thrown.getMessage == "requirement failed: date range is not valid")
  }

  test("Test download File") {
    //Test File download correctly downloads the expected file
    InitialProcess.downlaodObservationData(Map("SYDNEY" -> CommonData.codes("SYD", "IDCJDW4019")), 201601, "bom")
    assert(Files.exists(Paths.get("IDCJDW2801")))
  }
  
}