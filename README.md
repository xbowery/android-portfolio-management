<p align="center">
  <img src="https://user-images.githubusercontent.com/69230356/173277026-6f17558b-4769-4bcf-af8d-5f1d60a42919.png" />
</p>

# Performance Portfolio Management App

Group Project as part of our Assignment 3 for SMU CS205 - Operating System Concepts with Android

## Background Information

The Portfolio Management application is a standalone Android application that informs users of the various statuses of the stocks based on their tickers.

If you are running this on an emulator, these are the recommended configurations:
- Device: Nexus 6P
- API: Level 30
- Resolution: 1440 x 2560 (560 dpi)
- Multi-Core CPU 4
- RAM: 1536 MB
- SD card: 512 MB

## Group Members
- [Cheah King Yeh](https://github.com/xbowery)
- [Ng Jian Jie, Jerome](https://github.com/GoingCaffeineFree)
- [Won Ying Keat](https://github.com/wonyk)
- [Yuen Kah May](https://github.com/yuenkm40)

## Application Description - Application View

Users will first be greeted by a Welcome page (Splash screen) as seen in the top photo. 

<br>
<br>

<p align="center">
  <img src="https://user-images.githubusercontent.com/69230356/173278781-8554fdb5-d364-4cea-9ddf-359eef8e3679.png" />
</p>

<br>
<br>

Then, they would be brought to the landing page (shown above) and can **iteratively add stock tickers one-by-one up to a limit of 5 stock tickers**. The Add button would be disabled when the limit has been reached. 

An error message “The text field must not be empty!” will pop up if users do not key in the stock ticker name.

Users can then **click on “DOWNLOAD”, before clicking “CALCULATE”** to **view the _annualised returns and volatility of the stocks_**. If users wish to **delete the current stock ticker**, they can click on the bin icon. 

An error message will also be returned if a duplicate stock name is entered.

If an invalid stock ticker name is keyed in, the colour of the ticker name will change to 🔴red🔴 with the word (Invalid), prompting the user to delete the current stock ticker and key in a valid stock ticker name. 

Users can click on “CLEAR ALL” to remove all the stock tickers currently listed on the screen.

### Overall Navigation Diagram (User Flow)

<br>
<br>

<p align="center">
  <img src="https://user-images.githubusercontent.com/69230356/173279095-e9187a8b-f058-47bf-8964-dda00f0ec04a.png" />
</p>

<br>
<br>

## Solution Architecture (Design & Overall Android Components)

<br>
<br>

<p align="center">
  <img src="https://user-images.githubusercontent.com/69230356/173279421-7d474e26-52d5-44b2-be90-54073c3bdbd5.png" />
</p>

<br>
<br>

When a user clicks on the “DOWNLOAD” button, an Intent is created in the MainActivity and DownloadService is activated and fetches the stock data from Finnhub API corresponding to the relevant Tickers via Internet connection. If there is existing stock data stored in the SQLite Database containing the same Ticker name from previously, the data will not be re-downloaded again to prevent duplicate entries and optimise app performance since the time range of data to be collected is fixed.

When a user clicks on the “CALCULATE” button, the CalculateService is started and will start calculating the Annualised Returns and Volatility of Tickers that have been entered by retrieving data from HistoricalDataProvider (Content Provider). A list of Tickers containing the ticker name, annualised returns and annualised volatility is then returned to the MainActivity via a BroadcastReceiver.
