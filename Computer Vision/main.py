import cv2
import numpy as np
import urllib.request
import requests  # Import the requests library to send HTTP requests

url = 'http://192.168.183.204/cam-lo.jpg'

cap = cv2.VideoCapture(url)
whT = 320
confThreshold = 0.5
nmsThreshold = 0.3
classesfile = 'coco.names'
classNames = []

with open(classesfile, 'rt') as f:
    classNames = f.read().rstrip('\n').split('\n')

modelConfig = 'yolov3.cfg'
modelWeights = 'yolov3.weights'
net = cv2.dnn.readNetFromDarknet(modelConfig, modelWeights)
net.setPreferableBackend(cv2.dnn.DNN_BACKEND_OPENCV)
net.setPreferableTarget(cv2.dnn.DNN_TARGET_CPU)

# Function to detect objects and send the detected class names to ESP8266
def findObject(outputs, im):
    hT, wT, cT = im.shape
    bbox = []
    classIds = []
    confs = []
    found_cat = False
    found_bird = False
    detected_classes = []  # List to store detected classes

    for output in outputs:
        for det in output:
            scores = det[5:]
            classId = np.argmax(scores)
            confidence = scores[classId]
            if confidence > confThreshold:
                w, h = int(det[2] * wT), int(det[3] * hT)
                x, y = int((det[0] * wT) - w / 2), int((det[1] * hT) - h / 2)
                bbox.append([x, y, w, h])
                classIds.append(classId)
                confs.append(float(confidence))

    indices = cv2.dnn.NMSBoxes(bbox, confs, confThreshold, nmsThreshold)

    # Iterate through the indices and add detected object class names
    for i in indices:
        box = bbox[i]
        x, y, w, h = box[0], box[1], box[2], box[3]
        detected_class = classNames[classIds[i]]
        detected_classes.append(detected_class)  # Store detected class name

        cv2.rectangle(im, (x, y), (x + w, y + h), (255, 0, 255), 2)
        cv2.putText(im, f'{detected_class.upper()} {int(confs[i] * 100)}%', (x, y - 10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 0, 255), 2)

    # Send the detected object classes to the ESP8266 via HTTP POST
    if detected_classes:
        try:
            # Prepare data to send to ESP8266 (convert list to a comma-separated string)
            detected_data = ",".join(detected_classes)
            # Send POST request with detected classes
            response = requests.post('http://192.168.183.211/detected', data={"objects": detected_data})  # Replace with actual IP
            print("Sent to ESP8266:", detected_data)  # Print the sent data for debugging
        except Exception as e:
            print(f"Error sending data to ESP8266: {e}")

while True:
    img_resp = urllib.request.urlopen(url)
    imgnp = np.array(bytearray(img_resp.read()), dtype=np.uint8)
    im = cv2.imdecode(imgnp, -1)

    blob = cv2.dnn.blobFromImage(im, 1 / 255, (whT, whT), [0, 0, 0], 1, crop=False)
    net.setInput(blob)
    layernames = net.getLayerNames()
    outputNames = [layernames[i - 1] for i in net.getUnconnectedOutLayers()]

    outputs = net.forward(outputNames)

    findObject(outputs, im)

    cv2.imshow('Image', im)
    cv2.waitKey(1)
