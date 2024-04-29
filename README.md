# Fully functional Radio Application
## JavaFX application using:
- [radiobrowser4j](https://github.com/sfuhrm/radiobrowser4j) - for getting information abouts stations
- [jlayer](https://github.com/mahozad/jlayer) - for playing audio
- [google-maps-services-java](https://github.com/googlemaps/google-maps-services-java) - for finding appropiate US State with given longitude and latitude
- [async-http-client](https://github.com/AsyncHttpClient/async-http-client) - for getting input stream for player

Application was made only for american (according to API) radio stations (approx. 6000) and has functionalities such as:
- sorting by state/name 
- adding/removing favourite stations

> [!WARNING]
> Some stations don't work because of jlayer limitations of decoding input stream.

> [!IMPORTANT]
> Don't edit resources folder. (possible nuclear fallout)

![image](https://github.com/Matrei3/Java-Radio/assets/115424656/8ac806b8-2829-44fa-9631-1279e74683e1)
![image](https://github.com/Matrei3/Java-Radio/assets/115424656/b053b377-b1bb-4f95-b37d-9d0b98a1651f)
