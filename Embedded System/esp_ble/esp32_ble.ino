// BLE
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <DHT.h>


#define DHTPIN 4
#define DHTTYPE DHT22

// chars and service UUIDs
#define SERVICE_UUID "12345678-1234-1234-1234-123456789abc"
#define TEMP_CHAR "abcd1234-ab12-ab12-ab12-ab1234567890"
#define HUMIDITY_CHAR "abcd4321-ab12-ab12-ab12-ab1234567890"
#define HEART_CHAR "dcba1234-ab12-ab12-ab12-ab1234567890"

DHT dht(DHTPIN, DHTTYPE);

// server and chars instances
BLEServer* pServer = nullptr;
BLECharacteristic* temp_char = nullptr;
BLECharacteristic* humidity_char = nullptr;
BLECharacteristic* heart_char = nullptr;

float temp = 0.0;
float humd = 0.0;
float tempT;
float humdT;
unsigned long previousMillis = 0;
const long interval = 10000;
bool deviceConnected = false;
uint32_t lastSentTime = 0;

// connection callback
class ServerCallbacks : public BLEServerCallbacks {
  void onConnect(BLEServer* pServer) {
    deviceConnected = true;
    Serial.println("Device connected");
  }

  void onDisconnect(BLEServer* pServer) {
    deviceConnected = false;
    Serial.println("Device disconnected");

    // restart advertising
    pServer->startAdvertising();
    Serial.println("Advertising restarted, waiting for a client to connect...");
  }
};


void setup(void) {
  Serial.begin(9600);
  Serial.println(F("DHTxx test!"));
  dht.begin();

  // initialize BLE
  BLEDevice::init("DSP_NeoVim");
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new ServerCallbacks());

  // create BLE Service
  BLEService* pService = pServer->createService(SERVICE_UUID);

  // temp sensor char
  temp_char = pService->createCharacteristic(
    TEMP_CHAR,
    BLECharacteristic::PROPERTY_NOTIFY);
  temp_char->addDescriptor(new BLE2902());

  // humidity sensor char
  humidity_char = pService->createCharacteristic(
    HUMIDITY_CHAR,
    BLECharacteristic::PROPERTY_NOTIFY);
  humidity_char->addDescriptor(new BLE2902());

  // hear sensor char
  heart_char = pService->createCharacteristic(
    HEART_CHAR,
    BLECharacteristic::PROPERTY_NOTIFY);
  heart_char->addDescriptor(new BLE2902());

  // start the service
  pService->start();

  // start advertising
  pServer->getAdvertising()->start();
  Serial.println("Waiting for a client to connect...");
}

void loop() {
  if (deviceConnected) {
    // send sensors data each 3 seconds
    if (millis() - lastSentTime > 3000) {
      lastSentTime = millis();
      // read DHT sensor data
      unsigned long currentMillis = millis();
      if (currentMillis - previousMillis >= interval) {
        previousMillis = currentMillis;
        tempT = dht.readTemperature();
        humdT = dht.readHumidity();
        if (isnan(tempT) || isnan(humdT)) {
          Serial.println("Failed to read from DHT sensor!");
        } else {
          temp = tempT;
          humd = humdT;
          Serial.print(temp);
          Serial.println(humd);
        }
      }

      // send the temp data via BLE
      String data = String(humd, 2);
      temp_char->setValue(data.c_str());
      temp_char->notify();

      // send the humidity data via BLE
      String data1 = String(temp, 2);
      humidity_char->setValue(data1.c_str());
      Serial.println("Data Sent: " + data1);
      humidity_char->notify();
    }
  }
}


