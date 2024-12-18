/*
 *  Author : Ahmed Basem
 */
// =======================================================================
// =============================== INCLUDE ===============================
// =======================================================================
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <ESP8266SAM.h>
#include "AudioOutputI2SNoDAC.h"
#include <WiFiUdp.h>
#include <NTPClient.h>        // include NTPClient library
#include <TimeLib.h>          // Include Arduino time library
#include <Adafruit_GFX.h>     // include Adafruit graphics library
#include <Adafruit_ST7735.h>  // include Adafruit ST7735 TFT library

// =======================================================================
// =============================== DEFINES ===============================
// =======================================================================
#define DEBUG
#define DHTPIN 10
#define DHTTYPE DHT22
#define BtnPin 8

#define TFT_RST D4  // TFT RST pin is connected to NodeMCU pin D4 (GPIO2)
#define TFT_CS D8   // TFT CS  pin is connected to NodeMCU pin D3 (GPIO0)
#define TFT_DC D2   // TFT DC  pin is connected to NodeMCU pin D2 (GPIO4)

// =======================================================================
// ============================= GLOBAL VARs =============================
// =======================================================================
// Wifi Settings
const char* ssid = "Beso";
const char* password = "zkrallah";

String mainDetected;
// Current temperature & humidity, updated in loop()
float temp = 0.0;
float humd = 0.0;
float tempT;
float humdT;
unsigned long previousMillis = 0;
const long interval = 10000;
String message;
unsigned long unix_epoch;

// =======================================================================
// ==============================  CLASS =================================
// =======================================================================
DHT dht(DHTPIN, DHTTYPE);
ESP8266WebServer server(80);
AudioOutputI2SNoDAC* out;
WiFiUDP ntpUDP;
Adafruit_ST7735 tft = Adafruit_ST7735(TFT_CS, TFT_DC, TFT_RST);
NTPClient timeClient(ntpUDP, "time.nist.gov", 3600, 60000);

// =======================================================================
// ============================  FUNCTIONS ===============================
// =======================================================================
void handleDetectedObjects();
void SayDetected();

void RTC_display() {
  char dow_matrix[7][10] = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
  byte x_pos[7] = {29, 29, 23, 11, 17, 29, 17};
  static byte previous_dow = 0;

  // Print day of the week
  if (previous_dow != weekday(unix_epoch)) {
    previous_dow = weekday(unix_epoch);
    tft.fillRect(11, 55, 108, 14, ST7735_BLACK);     // Erase day from the display
    tft.setCursor(x_pos[previous_dow - 1], 55);
    tft.setTextColor(ST7735_CYAN, ST7735_BLACK);     // Set text color to cyan and black background
    tft.print(dow_matrix[previous_dow - 1]);
  }

  // Print date
  tft.setCursor(4, 79);
  tft.setTextColor(ST7735_YELLOW, ST7735_BLACK);     // Set text color to yellow and black background
  tft.printf("%02u-%02u-%04u", day(unix_epoch), month(unix_epoch), year(unix_epoch));

  // Print time
  tft.setCursor(16, 136);
  tft.setTextColor(ST7735_GREEN, ST7735_BLACK);      // Set text color to green and black background
  tft.printf("%02u:%02u:%02u", hour(unix_epoch), minute(unix_epoch), second(unix_epoch));
}

void setup() {
  pinMode(BtnPin, INPUT);
  Serial.begin(115200);
  tft.initR(INITR_BLACKTAB);                           // Initialize a ST7735S chip, black tab
  tft.fillScreen(ST7735_BLACK);                        // Fill screen with black color
  tft.drawFastHLine(0, 44, tft.width(), ST7735_BLUE);  // Draw horizontal blue line at position (0, 44)
  tft.setTextColor(ST7735_WHITE, ST7735_BLACK);        // Set text color to white and black background
  tft.setTextSize(1);                                  // Text size = 1
  tft.setCursor(19, 10);                               // Move cursor to position (43, 10) pixel
  tft.print("ESP8266 NodeMCU");
  tft.setCursor(4, 27);                                // Move cursor to position (4, 27) pixel
  tft.print("Wi-Fi Internet Clock");

  WiFi.begin(ssid, password);  // Connect to Wi-Fi
  unsigned long startAttemptTime = millis();

  while (WiFi.status() != WL_CONNECTED && millis() - startAttemptTime < 10000) {
    delay(500);
    Serial.println("Connecting to WiFi...");
  }

  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("Failed to connect to WiFi.");
    return;
  }

  Serial.println("Connected to WiFi");
  Serial.println(WiFi.localIP());                            // Print the ESP8266 IP address
  server.on("/detected", HTTP_POST, handleDetectedObjects);  // Define POST handler for detected objects
  server.begin();                                            // Start the web server

  out = new AudioOutputI2SNoDAC();
  out->SetOutputModeMono(true);
  out->begin();

  tft.fillRect(0, 54, tft.width(), 8, ST7735_BLACK);
  tft.drawFastHLine(0, 102, tft.width(), ST7735_BLUE);  // Draw horizontal blue line at position (0, 102)
  tft.setTextSize(2);                                   // Text size = 2
  tft.setTextColor(ST7735_MAGENTA, ST7735_BLACK);       // Set text color to magenta and black background
  tft.setCursor(37, 112);                               // Move cursor to position (37, 112) pixel
  tft.print("TIME:");

  timeClient.begin();
}

void loop() {
  server.handleClient();  // Handle incoming HTTP requests
  timeClient.update();
  unix_epoch = timeClient.getEpochTime();  // Get UNIX Epoch time
  RTC_display();
  yield();  // Reset the watchdog timer to prevent resets during long tasks
}

void SayDetected() {
  ESP8266SAM* sam = new ESP8266SAM;
  sam->Say(out, mainDetected.c_str());
  delete sam;
}

void handleDetectedObjects() {
  if (server.hasArg("objects")) {
    String detected_objects = server.arg("objects");
    Serial.println("Detected Objects: " + detected_objects);
    if (detected_objects.length() > 2) {
      mainDetected = detected_objects;
      SayDetected();
    }
  }
  server.send(200, "text/plain", "Received objects");
}
