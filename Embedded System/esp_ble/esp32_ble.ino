#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

#define SERVICE_UUID           "12345678-1234-1234-1234-123456789abc"
#define SENSOR_CHARACTERISTIC_UUID "abcd1234-ab12-ab12-ab12-ab1234567890"
#define COMMAND_CHARACTERISTIC_UUID "abcd4321-ab12-ab12-ab12-ab1234567890"
#define READ_CHARACTERISTIC_UUID "1234abcd-ab12-ab12-ab12-ab1234567890"

BLEServer* pServer = nullptr;
BLECharacteristic* pSensorCharacteristic = nullptr;
BLECharacteristic* pCommandCharacteristic = nullptr;
BLECharacteristic* pReadCharacteristic = nullptr;

bool deviceConnected = false;
uint32_t lastSentTime = 0;
float healthSensorData = 0.0;

class ServerCallbacks : public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
        deviceConnected = true;
        Serial.println("Device connected");
    }

    void onDisconnect(BLEServer* pServer) {
        deviceConnected = false;
        Serial.println("Device disconnected");

        // Restart advertising
        pServer->startAdvertising();
        Serial.println("Advertising restarted, waiting for a client to connect...");
    }
};

class CommandCallbacks : public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* pCharacteristic) {
        String value = pCharacteristic->getValue().c_str();

        if (value.length() > 0) {
            Serial.print("Received command: ");
            for (int i = 0; i < value.length(); i++) {
                Serial.print(value[i]);
            }
            Serial.println();

            // Handle the command, for example:
            if (value == "START") {
                Serial.println("Starting sensor monitoring...");
                // Implement the start command
            } else if (value == "STOP") {
                Serial.println("Stopping sensor monitoring...");
                // Implement the stop command
            }
        }
    }
};

void setup() {
    Serial.begin(115200);
    
    // Initialize BLE
    BLEDevice::init("ESP32_Health_Device");
    pServer = BLEDevice::createServer();
    pServer->setCallbacks(new ServerCallbacks());

    // Create BLE Service
    BLEService *pService = pServer->createService(SERVICE_UUID);

    // Create Sensor Data Characteristic
    pSensorCharacteristic = pService->createCharacteristic(
                            SENSOR_CHARACTERISTIC_UUID,
                            BLECharacteristic::PROPERTY_NOTIFY
                          );
    pSensorCharacteristic->addDescriptor(new BLE2902());

    // Create Command Characteristic
    pCommandCharacteristic = pService->createCharacteristic(
                             COMMAND_CHARACTERISTIC_UUID,
                             BLECharacteristic::PROPERTY_WRITE
                           );
    pCommandCharacteristic->addDescriptor(new BLE2902());
    pCommandCharacteristic->setCallbacks(new CommandCallbacks());

    // Create Read Data Characteristic
    pReadCharacteristic = pService->createCharacteristic(
                            READ_CHARACTERISTIC_UUID,
                            BLECharacteristic::PROPERTY_READ
                          );
    pReadCharacteristic->addDescriptor(new BLE2902());

    // Start the service
    pService->start();

    // Start advertising
    pServer->getAdvertising()->start();
    Serial.println("Waiting for a client to connect...");
}

void loop() {
    if (deviceConnected) {
        if (millis() - lastSentTime > 5000) {
            lastSentTime = millis();
            
            healthSensorData += 0.1;
            if (healthSensorData > 100) healthSensorData = 0;

            String data = String(healthSensorData, 2);
            pSensorCharacteristic->setValue(data.c_str());
            pSensorCharacteristic->notify();
            
            pReadCharacteristic->setValue(data.c_str());
            
            Serial.print("Sent sensor data: ");
            Serial.println(data);
        }
    }
}
