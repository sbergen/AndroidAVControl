# My personal AV Control app for a Samsung TV and Anthem AVM 60

## Disclaimer

This is the first ever native Android app I've written since way back when Android was a new thing.
It's also the first project I've ever written with Kotlin.
A lot of it was inspired by just googling around for related articles.

**Any bad practices are just because of my noobness :)**

## Overview

Created from the pure need for something to control my TV and AVM more simply.
It's probably no use for anyone as is, but feel free to take it and customise it for your needs,
or pick up some pieces you find useful.

## Features

* Switch inputs from both the TV and AVM in sync with a single button
* Power both devices off with a single button
* Control volume of AVM with HW volume buttons
* Switch the AVM display on and off via a toggle

## Known issues

* Turning on a TV through SmartThings is very unreliable. Does not work most of the time.

## Tech overview

* Communicates with the AVM via a raw TCP/IP socket
* Communicates with the TV via the Samsung SmartThings API
  * Uses Moshi, Retrofit2 and Okhttp3 to build the API. Might be a bit overkill :)
* Uses Android data binding for the UI

## Setup

Add the following to local.properties

```
SMART_THINGS_API_TOKEN="..."
SMART_THINGS_DEVICE_ID="..."
AVM_IP="..."
```

where
* `SMART_THINGS_API_TOKEN` can be acquired from Samsung for your own SmartThings account
* `SMART_THINGS_DEVICE_ID` can be figured out by doing a GET request to the SmartThings `/devices` endpoint
* `AVM_IP` is the local IP address for the AVM 60 (or other Anthem device)

After this, things should work normally with Android Studio.

## Future plans

I'd like to control my lights through KNX with the same app,
but currently don't have access to the KNX system in my apartment.
Might never happen.

## References

* [SmartThings API](https://smartthings.developer.samsung.com/docs/api-ref/st-api.html)
* [Anthem MRX 1120-720-520 AVR and AVM 60 AVP serial commands (.xls)](https://www.anthemav.com/downloads/MRX-x20-AVM-60-IP-RS-232.xls)

## License

MIT
