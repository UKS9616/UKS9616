package com.coms309.users;



public class JsonStringResponse {
    private String strResponse;

    public JsonStringResponse(String str){
        this.strResponse = str;
    }

    public JsonStringResponse(){}

    // Getter for string response
    public String getStrResponse() {
        return strResponse;
    }

    // Setter for string response
    public void setStrResponse(String strResponse) {
        this.strResponse = strResponse;
    }

    @Override
    public String toString() {
        return strResponse;
    }

}
