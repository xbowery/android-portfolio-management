package com.example.serviceexample;

import android.os.Parcel;
import android.os.Parcelable;

public class Ticker implements Parcelable {
    private String ticker;
    private int annualisedReturn;
    private int annualisedVolatility;

    public Ticker() {
    }

    public Ticker(String ticker) {
        this.ticker = ticker;
    }

    public Ticker(String ticker, int annualisedReturn, int annualisedVolatility) {
        this.ticker = ticker;
        this.annualisedReturn = annualisedReturn;
        this.annualisedVolatility = annualisedVolatility;
    }

    protected Ticker(Parcel in) {
        ticker = in.readString();
        annualisedReturn = in.readInt();
        annualisedVolatility = in.readInt();
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
        parcel.writeInt(annualisedReturn);
        parcel.writeInt(annualisedVolatility);
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getAnnualisedReturn() {
        return annualisedReturn;
    }

    public void setAnnualisedReturn(int annualisedReturn) {
        this.annualisedReturn = annualisedReturn;
    }

    public int getAnnualisedVolatility() {
        return annualisedVolatility;
    }

    public void setAnnualisedVolatility(int annualisedVolatility) {
        this.annualisedVolatility = annualisedVolatility;
    }
}
