package com.example.serviceexample;

import android.os.Parcel;
import android.os.Parcelable;

public class Ticker implements Parcelable {
    private String ticker;
    private double annualisedReturn;
    private double annualisedVolatility;
    private boolean isValid = true;
    private boolean isCalculated;

    public Ticker() {
    }

    public Ticker(String ticker) {
        this.ticker = ticker;
    }

    public Ticker(String ticker, double annualisedReturn, double annualisedVolatility) {
        this.ticker = ticker;
        this.annualisedReturn = annualisedReturn;
        this.annualisedVolatility = annualisedVolatility;
    }

    protected Ticker(Parcel in) {
        ticker = in.readString();
        annualisedReturn = in.readDouble();
        annualisedVolatility = in.readDouble();
        isValid = in.readBoolean();
        isCalculated = in.readBoolean();
    }

    public static final Creator<Ticker> CREATOR = new Creator<Ticker>() {
        @Override
        public Ticker createFromParcel(Parcel in) {
            return new Ticker(in);
        }

        @Override
        public Ticker[] newArray(int size) {
            return new Ticker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ticker);
        parcel.writeDouble(annualisedReturn);
        parcel.writeDouble(annualisedVolatility);
        parcel.writeBoolean(isValid);
        parcel.writeBoolean(isCalculated);
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getAnnualisedReturn() {
        return annualisedReturn;
    }

    public void setAnnualisedReturn(double annualisedReturn) {
        this.annualisedReturn = annualisedReturn;
    }

    public double getAnnualisedVolatility() {
        return annualisedVolatility;
    }

    public void setAnnualisedVolatility(double annualisedVolatility) {
        this.annualisedVolatility = annualisedVolatility;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticker ticker1 = (Ticker) o;
        return ticker.equals(ticker1.ticker);
    }

    @Override
    public int hashCode() {
        return ticker.hashCode();
    }

    public boolean isCalculated() {
        return isCalculated;
    }

    public void setCalculated(boolean calculated) {
        isCalculated = calculated;
    }
}
