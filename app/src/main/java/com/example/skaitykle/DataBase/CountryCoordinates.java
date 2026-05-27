package com.example.skaitykle;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class CountryCoordinates {

    private static final Map<String, LatLng> countryMap = new HashMap<>();

    static {

        countryMap.put("Lithuania", new LatLng(55.1694, 23.8813));
        countryMap.put("United Kingdom", new LatLng(55.3781, -3.4360));
        countryMap.put("USA", new LatLng(37.0902, -95.7129));
        countryMap.put("China", new LatLng(35.8617, 104.1954));
        countryMap.put("France", new LatLng(46.2276, 2.2137));
        countryMap.put("Germany", new LatLng(51.1657, 10.4515));
        countryMap.put("Italy", new LatLng(41.8719, 12.5674));
        countryMap.put("Spain", new LatLng(40.4637, -3.7492));
        countryMap.put("Japan", new LatLng(36.2048, 138.2529));
        countryMap.put("Canada", new LatLng(56.1304, -106.3468));
        countryMap.put("Brazil", new LatLng(-14.2350, -51.9253));
        countryMap.put("India", new LatLng(20.5937, 78.9629));
        countryMap.put("Australia", new LatLng(-25.2744, 133.7751));
        countryMap.put("Russia", new LatLng(61.5240, 105.3188));
        countryMap.put("Sweden", new LatLng(60.1282, 18.6435));
        countryMap.put("Norway", new LatLng(60.4720, 8.4689));
        countryMap.put("Finland", new LatLng(61.9241, 25.7482));
        countryMap.put("Poland", new LatLng(51.9194, 19.1451));
        countryMap.put("Ukraine", new LatLng(48.3794, 31.1656));
        countryMap.put("Mexico", new LatLng(23.6345, -102.5528));
        countryMap.put("South Korea", new LatLng(35.9078, 127.7669));
        countryMap.put("Argentina", new LatLng(-38.4161, -63.6167));
        countryMap.put("Turkey", new LatLng(38.9637, 35.2433));
        countryMap.put("Greece", new LatLng(39.0742, 21.8243));
        countryMap.put("Egypt", new LatLng(26.8206, 30.8025));

    }

    public static LatLng getCoordinates(String country) {

        if (countryMap.containsKey(country)) {

            return countryMap.get(country);

        } else {

            return null;
        }
    }
}