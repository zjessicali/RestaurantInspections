package com.example.group_9_project.network;

import android.net.Uri;
import android.util.Log;

import com.example.group_9_project.model.RestaurantManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class FetchData {

    private static final String TAG = "FetchData Class";
    RestaurantManager restaurants = RestaurantManager.getInstance();
    //several methods taken or based on code from
    //Bill Phillips, Chris Stewart, Kristin Marsicano - Android Programming_ The Big Nerd Ranch Guide (2017, Big Nerd Ranch) - libgen.lc
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);

            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public RestaurantManager fetchItems() {
        try {
            String restaurantPackageURL = Uri.parse("https://data.surrey.ca/api/3/action/")
                    .buildUpon()
                    .appendPath("package_show")
                    .appendQueryParameter("id", "restaurants")
                    .build().toString();
            String inspectionPackageURL =  Uri.parse("https://data.surrey.ca/api/3/action/")
                    .buildUpon()
                    .appendPath("package_show")
                    .appendQueryParameter("id", "fraser-health-restaurant-inspection-reports")
                    .build().toString();
            String jsonStringRest = getUrlString(restaurantPackageURL);
            JSONObject jsonBodyRest = new JSONObject(jsonStringRest);
            String jsonStringInsp = getUrlString(inspectionPackageURL);
            JSONObject jsonBodyInsp = new JSONObject(jsonStringInsp);

            parseItems( jsonBodyRest);
            parseItems(jsonBodyInsp);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return restaurants;
    }

    private void parseItems(JSONObject jsonBody) throws IOException, JSONException{
        JSONObject resultJSONObj = jsonBody.getJSONObject("result");
        JSONArray resourcesJSONArr = resultJSONObj.getJSONArray("resources");
        JSONObject resources = resourcesJSONArr.getJSONObject(0);

        String lastModified = resources.getString("last_modified");
        restaurants.setLastModified(lastModified);

        String name = resources.getString("name");

        String csvURL = resources.getString("url");
        if(name.equals("Restaurants")){
            readRestCSV(csvURL);
        }
        else{
            readInspCSV(csvURL);
        }

    }

    private void readInspCSV(String csvURL) {
        try{
            URL url = new URL(csvURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStream in = connection.getInputStream();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, Charset.forName("UTF-8"))
            );
            restaurants.readUpdatedInspectionData(reader);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch csv", ioe);
        }
    }


    private void readRestCSV(String csvURL)throws IOException{
        try{

            URL url = new URL(csvURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStream in = connection.getInputStream();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, Charset.forName("UTF-8"))
            );
            restaurants.readRestaurantData(reader);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch csv", ioe);
        }
    }


}
