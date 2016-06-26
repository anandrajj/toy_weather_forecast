package org.weather.model.forecast

import org.scalatest.FunSuite
import org.weather.model.data.CommonData

class TestPredictFunctions extends FunSuite {
  //Module to test prediction functions.
  test("Test date t+1 day claculations") {
    //Test that getPlusOneDay calcualtes the date of forecast in UTC format correctly
    assert(PredictFunctions.getPlusOneday(List(CommonData.fields("2016-05-01", 0D, 0D, 0D, 0D, 0, 0, 0D),
      CommonData.fields("2016-04-01", 0D, 0D, 0D, 0D, 0, 0, 0D)))
      == "2016-05-02T02:00:00Z")

  }

  test("Test Arima Forecast") {
    //Test if Arima forecast return n + 1 forecast values for the input vector of size n and check output is Double.
    assert(
      PredictFunctions.executeARIMAForecast(Array(1D, 2D, 3D, 1D, 1D, 2D, 3D, 1D, 1D, 2D, 3D, 1D)) match {
        case x: Double => true
      })
  }

  test("Predict outlook function test & boundary conditions") {
      //Test that predict outlook function returns correct outlook based on various values & boundary conditions.
      assert(PredictFunctions.predictOutlook(81, 7, 2, 20D) === "Rain")
      assert(PredictFunctions.predictOutlook(80, 7, 6, 20D) === "Sunny") //Boundary condition for Humidity & Check Sunny.
      assert(PredictFunctions.predictOutlook(80, 7, 5, 20D) === "Cloudy") //Check Cloudy result and boundary condition for Sunshine
      assert(PredictFunctions.predictOutlook(80, 6, 5, 14D) === "Cold") //Check Cold result & boundary condition for Cloud cover
      assert(PredictFunctions.predictOutlook(80, 6, 5, 16D) === "Partly Cloudy") //Check for Temperature boundary condition & "Partly Cloudy" results
  }
  
 

  test("Test execution of steps for forecasting for Melbourne") {
      //Test that peform predict functions returns all 7 exepcted fields, IATA Codes and geo coordinates.
      InitialProcess.downlaodObservationData(Map("MELBOURNE" -> CommonData.codes("MEL", "IDCJDW3033")), 201605, "bom")
      val x = PredictFunctions.performPrediction(Map("MELBOURNE" -> CommonData.codes("MEL", "IDCJDW3033")))(0).split(CommonData.defaultFieldSep)
      assert(x.size === 7)
      assert(x(0) === "MEL")
      assert(x(1) === "-37.82,144.96,7")
      
  }
}