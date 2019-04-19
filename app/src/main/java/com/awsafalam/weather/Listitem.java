package com.awsafalam.weather;



public class Listitem {
    private String Heading;
    private String Decription;
    private String Url;

    public Listitem(String heading, String decription , String url) {
        Heading = heading;
        Decription = decription;
        Url = url;
    }

    public Listitem() {

    }


    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getDecription() {
        return Decription;
    }

    public void setDecription(String decription) {
        Decription = decription;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
