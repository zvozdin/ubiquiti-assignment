# Device API

This API allows you to manage devices and their network topology.

## Building the Application

```bash
mvn clean package
```

## Run the Application

```bash
java -jar target/ubiquiti-assignment-0.1.0.jar
```

## Endpoints

### Register a new device

- **URL:** `/api/v1/devices`
- **Method:** `POST`
- **Request Body:**

```json
{
  "macAddress": "string",
  "deviceType": "string",
  "uplinkMacAddress": "string"
}
```

### Get all devices

- **URL:** `/api/v1/devices`
- **Method:** `GET`
- **DeviceResponse:**

```json
[
  {
    "macAddress": "string",
    "deviceType": "string"
  }
]
```

### Get a device by MAC address

- **URL:** `/api/v1/devices/{macAddress}`
- **Method:** `GET`
- **DeviceResponse:**

```json
{
"macAddress": "string",
"deviceType": "string"
}
```

### Get the network topology

- **URL:** `/api/v1/devices/topology`
- **Method:** `GET`
- **NetworkTopologyResponse:**

```json
{
  "rootMacAddress": "string",
  "downlinkDevices": [
    {
      "rootMacAddress": "string",
      "downlinkDevices": [
        ...
      ]
    }
  ]
}
``` 

### Get the network topology by MAC address

- **URL:** `/api/v1/devices/topology/{macAddress}`
- **Method:** `GET`
- **NetworkTopologyResponse:**

```json
{
  "rootMacAddress": "string",
  "downlinkDevices": [
    {
      "rootMacAddress": "string",
      "downlinkDevices": [
        ...
      ]
    }
  ]
}
``` 