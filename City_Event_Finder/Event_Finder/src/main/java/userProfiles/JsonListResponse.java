package com.coms309.userProfiles;


import java.util.ArrayList;
import java.util.List;



public class JsonListResponse {
    private List<Integer> listResponse;

    public JsonListResponse(List<Integer> userSelRSVPs){
        this.listResponse = userSelRSVPs;
    }

    public JsonListResponse(){
        this.listResponse = new ArrayList<>();
    }

    // Getter for string response
    public List<Integer> getListResponse() {
        return listResponse;
    }

    // Setter for string response
    public void setListResponse(List<Integer> listResponse) {
        this.listResponse = listResponse;
    }

    @Override
    public String toString() {
        return listResponse.toString();
    }

}
