package com.cs205.g2t5.portfolio_management;

import android.os.Parcel;
import android.os.Parcelable;

public class Ticker implements Parcelable {
    private final String ticker;
    private double annualisedReturn;
    private double annualisedVolatility;
    private boolean isValid = true;
    private boolean isCalculated;

    public Ticker(String ticker) {
        this.ticker = ticker;
    }

    protected Ticker(Parcel in) {
        ticker = in.readString();
        annualisedReturn = in.readDouble();
        annualisedVolatility = in.readDouble();
        isValid = in.readInt() != 0;
        isCalculated = in.readInt() != 0;
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
        parcel.writeInt(isValid ? 1 : 0);
        parcel.writeInt(isCalculated ? 1 : 0);
    }

    public String getTicker() {
        return ticker;
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
