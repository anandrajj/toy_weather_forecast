toy_weather_forecast
============

Backgroud
---------

The application is designed to predict weather of various important cities around Australia. As the weather observations are over a period of time, time-series analysis using [ARIMA](https://en.wikipedia.org/wiki/Autoregressive_integrated_moving_average)  is used to forecast weather parameters. 

Data required for the prediction is sourced from [BOM (Bureau of Metorology, Austraila)](http://www.bom.gov.au/climate/dwo/).

Forecasts are performed for - CANBERRA, SYDNEY, WOLLONGONG, MELBOURNE, BRISBANE, CAIRNS, GOLD COAST, ADELAIDE, PERTH, HOBART, DARWIN.

Building
--------
	
### Building & Test:
 
### Building:

 Project uses [SBT](http://www.scala-sbt.org/) to build & test the application. To compile, test and build jars: 

 	sbt assembly

### Running:

Command line parms must be specifed in pairs in the format `--name value`. Internally application validates this format & converts to a Map(name -> vlaue)

Application expects, 4 mandatory command line parms viz. dataSource, year, month, outFile. Optional parameter dateRange allows to override the default date range of (201506 to 201606). This range is choosen is default based on data availablity in BOM.

if dataSource is bom, the data is downloaded from bom before forecasting else it expects the data to be in local files in current working dierctory.

To run the project with fat jar in current path:

	java -jar toy-weather-forecast.jar --dataSource bom --year 2016 --month 11


Project organization
--------------------

Project is split into two layers by means of packages. All the data handling like downloading, accessing api, parsing of values is organised under the `org.weather.model.data` and all processing logic required for forecasting is grouped under `org.weather.model.forecast`. Below diagram shows the Objects & functions under the packages.

								  ------------------------------
								  |    toy_weather_forecast    |
							      ------------------------------
                                      |                   |
                                      |                   |
                                      V                   V
		------------------------------------			---------------------------------
		|	org.weather.model.data         |			|  org.weather.model.forecast   |
		|								   |			|                               |
		------------------------------------ 	        ---------------------------------
		     |												|
		     |     ************************					|     ***********************
		     |---->| CommonData           |                 |---->|InitialProcess       |
		     |     ************************                 |     *********************** 
		     |                                              |
		     |     ************************                 |     *********************** 
		     |---->|FileOperations        |                 |---->|PredictFunctions     | 
		     |     ************************                 |     ***********************
		     |                                              |
		     |     ************************                 |     *********************** 
		     |---->|GetData               |                 |---->|PredictWeather       |
		     	   ************************                       ***********************


Execution Flow
--------------

PredictWeather is the entry point into application which controls the order of execution various steps required to complete forecasting. Flow of steps is represented as diagram below.

	***********************************************
	* 	Parsing Arguments & Accuring Data         *										 
	***********************************************
		------------------------------------	
        |Parse & Validate Command line args|
        ------------------------------------
        			  |
        			  V
        ------------------------------------
        |Get the cities & mapping to IATA  | 
        |code and BoM file name for each   | 
        |city                              |  
        ------------------------------------
                      |
                      V
        ------------------------------------
        |Validate & Get the date for which |
        |forecast is to be performed. Also |
        |validate.                         |
        ------------------------------------
                      |
                      V 
        -------------------------------------
        |Download BoM data if the dataSource|
        |is 'bom' for all the cities & the  |
        |input date                         |
        -------------------------------------
        			  |
        			  V
        ***************************************
        * Perform Predictions for all Cities  *
        ***************************************

        ------------------------------------------
        |   Read Obser vation for the city       | <-----------------------  
        ------------------------------------------                        |
        			  |													  |
        			  V                                                   |
        ------------------------------------------						  |	
        | Caluclate the t+1 day for which the    |                        |
        | forecast is being performed		     |                        |
        ------------------------------------------						  |	
        			  |												      |
        			  V                                                   |
        ------------------------------------------					      |
        |Predict Average Temperature, Pressures  |                        |
        |and Relative humidity using ARIMA time- |                        |
        |series analysis.						 |                        |
        ------------------------------------------                        |
                      |                                                   |
        			  V                                       ------------------------------------
        ------------------------------------------            | Repeat For each City in scope of |
        |Predict cloud cover & Sunshine hours 	 |            | forecast                         |
        |for outlook forecasting				 |            ------------------------------------
        ------------------------------------------                        |
        	          | 												  |
        			  V           										  |
        ------------------------------------------                        |
        |Uinsg Google API get Geo coordinates &  |                        |
        |elevation							     |                        |
        ------------------------------------------                        |
        			  |             								      |
        			  V	      											  |
        ------------------------------------------                        |
        |String IATA Code, Geo coordinates, date |				          |
        |forecasting for(utc), outlook, predicted| ------------------------
        |average temperature, pressure and 		 |	
        |relative humidity. 					 |
        ------------------------------------------
                      |
                      V
        ******************************************
        * Write results into the output file     *  
        ******************************************

