package com.example.group_9_project.model;

public class IconsLib {
    private static IconsLib myLib;            // singleton
    private String[] library = {             // library of available icon res names
            "7Eleven",
            "DairyQueen",
            "AandW",
            "Starbucks",
            "TimHorton",
            "Barcelo",
            "BostonPizza",
            "PizzaHut",
            "Wendys",
            "Subway",
            "Freshslice"};

    public static IconsLib getInstance(){
        if(myLib == null){
            myLib = new IconsLib();
        }
        return myLib;
    }

    public String[] getLibrary(){
        return library;
    }

}

