toy_weather_forecast
============

Test message

Backgroud
---------

	The application is designed to predict weather of various important cities around Australia. As the weather observations are over a period of time, time-series analysis using [ARIMA](https://en.wikipedia.org/wiki/Autoregressive_integrated_moving_average)  is used to forecast weather parameters. 

	Data required for the prediction is sourced from [BOM (Bureau of Metorology, Austraila)](http://www.bom.gov.au/climate/dwo/).

	Forecasts are performed for - CANBERRA, SYDNEY, WOLLONGONG, MELBOURNE, BRISBANE, CAIRNS, GOLD COAST, ADELAIDE, PERTH, HOBART, DARWIN.

Building:
---------
	
### Building & Test:
 
### Building:

 	Project uses [SBT](http://www.scala-sbt.org/) to build & test the application. To compile, test and build jars: 

 	`sbt assembly`

### Running:

	Command line parms must be specifed in pairs in the format `--name value`. Internally application validates this format & converts to a Map(name -> vlaue)

	Application expects, 4 mandatory command line parms viz. dataSource, year, month, outFile. Optional parameter dateRange allows to override the default date range of (201506 to 201606). This range is choosen is default based on data availablity in BOM.

	if dataSource is bom, the data is downloaded from bom before forecasting else it expects the data to be in local files in current working dierctory.

	To run the project with fat jar in current path:

	`java -jar toy-weather-forecast.jar --dataSource bom --year 2016 --month 11`




	

