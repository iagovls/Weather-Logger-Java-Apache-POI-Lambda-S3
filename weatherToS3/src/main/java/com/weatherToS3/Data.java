package com.weatherToS3;

public class Data {
    private double temp;
    private double feelsLike;
    private int humidity;

    public Data(double temp, double feelsLike, int humidity) {
        this.temp = temp;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}
