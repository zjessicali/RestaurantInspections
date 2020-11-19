package com.example.group_9_project.network;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.group_9_project.model.Restaurant;
import com.example.group_9_project.model.RestaurantManager;
import com.example.group_9_project.model.UpdateData;
import com.example.group_9_project.ui.MainActivity;
import com.example.group_9_project.ui.MapsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static android.content.Context.MODE_PRIVATE;

//this class deals with network related things and downloads the data
public class FetchData {

    private static final String TAG = "FetchData Class";
    RestaurantManager restaurants = RestaurantManager.getInstance();
    RestaurantManager Rdownloading = RestaurantManager.getSecInstant();
    UpdateData updateData = UpdateData.getInstance();

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

        return Rdownloading;
    }

    public UpdateData fetchUpdateItems(){
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

            parseForUpdate(jsonBodyRest,jsonBodyInsp);

        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return updateData;
    }

    private void parseForUpdate(JSONObject jsonBodyRest, JSONObject jsonBodyInsp)throws IOException, JSONException {
        //check rest
        JSONObject resourcesRest = jsonBodyRest.getJSONObject("result").getJSONArray("resources").getJSONObject(0);
        String last_modified = resourcesRest.getString("last_modified");
        //check if server's last modified is after restaurant manager's last modified
        LocalDateTime server = LocalDateTime.parse(last_modified);
        LocalDateTime dataModified = LocalDateTime.parse(updateData.getLastUpdated());
        if(server.isAfter(dataModified)){
            updateData.setNeedUpdate(true);
        }

        if(!updateData.getNeedUpdate()){//if restaurants dont need upate, check if insp needs
            JSONObject resourcesInsp = jsonBodyInsp.getJSONObject("result").getJSONArray("resources").getJSONObject(0);
            last_modified = resourcesInsp.getString("last_modified");
            server = LocalDateTime.parse(last_modified);
            if(server.isAfter(dataModified)){
                updateData.setNeedUpdate(true);
            }
        }
    }

    private void parseItems(JSONObject jsonBody) throws IOException, JSONException{
        JSONObject resultJSONObj = jsonBody.getJSONObject("result");
        JSONArray resourcesJSONArr = resultJSONObj.getJSONArray("resources");
        JSONObject resources = resourcesJSONArr.getJSONObject(0);

        String last_modified = resources.getString("last_modified");//do smth

        String name = resources.getString("name");

        String csvURL = resources.getString("url");
        if(name.equals("Restaurants")){
            Rdownloading.clearRestaurants();
            readRestCSV(csvURL);
        }
        else{
            readInspCSV(csvURL);
        }
    }

    private BufferedReader inspReader;
    private void readInspCSV(String csvURL) {
        try{
            URL url = new URL(csvURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStream in = connection.getInputStream();

            inspReader = new BufferedReader(
                    new InputStreamReader(in, Charset.forName("UTF-8"))
            );
            Rdownloading.readUpdatedInspectionData(inspReader);
            String csv = getUrlString(csvURL);
            saveInspData(csv, MainActivity.getInstance());
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch csv", ioe);
        }
    }

    private BufferedReader restReader;
    private void readRestCSV(String csvURL)throws IOException{
        try{
            URL url = new URL(csvURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStream in = connection.getInputStream();

            restReader = new BufferedReader(
                    new InputStreamReader(in, Charset.forName("UTF-8"))
            );
            Rdownloading.readRestaurantData(restReader);
            String csv = getUrlString(csvURL);
            saveResData(csv, MainActivity.getInstance());
            Log.d("FetchData", "readCSV");

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch csv", ioe);
        }

    }

    public void saveResData(String csvURL, Context context){
        try {
            FileOutputStream outRest = context.openFileOutput("RestaurantData.csv", MODE_PRIVATE);
            outRest.write(csvURL.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e("FetchData", "Failed to save restaurant csv", e);
        }

    }

    public void saveInspData(String csvURL, Context context){
        try {
            FileOutputStream outInsp = context.openFileOutput("InspectionData.csv", MODE_PRIVATE);
            outInsp.write(csvURL.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e("FetchData", "Failed to save inspection csv", e);
        }
    }


    public void readLastRestCSV(Context context){
        FileInputStream fis;
        try {
            fis = context.openFileInput("RestaurantData.csv");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis, StandardCharsets.UTF_8)
            );
            restaurants.readRestaurantData(reader);

        } catch (FileNotFoundException e) {
            Log.e("FetchData", "Restaurant files not found.");
        }
    }

    public void readLastInspCSV(Context context){
        try {
            FileInputStream fis = context.openFileInput("InspectionData.csv");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis, StandardCharsets.UTF_8)
            );
            restaurants.readUpdatedInspectionData(reader);
        } catch (FileNotFoundException e) {
            Log.e("FetchData", "Inspection files not found.");
        }
    }


}
