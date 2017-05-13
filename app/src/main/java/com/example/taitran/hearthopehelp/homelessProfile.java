package com.example.taitran.hearthopehelp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by addy on 5/11/17.
 */
@IgnoreExtraProperties
public class homelessProfile {
    public String hName;
    public String hAge;
    public String hHometown;
    public String hUsuallyFound;
    public String hStory;
    public double hLongitude;
    public double hLatitude;

    public homelessProfile(){

    }

    public homelessProfile(String name, String age, String hometown,
                           String usuallyFound, String story, double longitude, double latitude){
        this.hName = name;
        this.hAge = age;
        this.hHometown = hometown;
        this.hUsuallyFound = usuallyFound;
        this.hStory = story;
        this.hLongitude = longitude;
        this.hLatitude  = latitude;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", hName);
        result.put("age", hAge);
        result.put("hometown", hHometown);
        result.put("usuallyFound", hUsuallyFound);
        result.put("story", hStory);
        result.put("Longitude", hLongitude);
        result.put("Latidude", hLatitude);

        return result;
    }
}


