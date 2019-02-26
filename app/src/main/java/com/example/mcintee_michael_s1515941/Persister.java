package com.example.mcintee_michael_s1515941;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The persister class is created to be a parcelable object which holds all child channel
 * and items to be persisted across intents. We can package it in intents which is useful as
 * when orientation/activity changes take place old activities are either paused or destroyed.
 * @author michaelmcintee
 * @version 1.0
 * @see Parcelable
 * StudentID - S1515941
 * Programme - BSc SDfB
 */
public class Persister implements Parcelable {
    private Channel channel = new Channel();

    public void writeToParcel(Parcel dest, int flags){
        dest.writeValue(channel);
    }

    public Persister(Parcel p) {
        channel = (Channel) p.readValue(channel.getClass().getClassLoader());
    }

    public Persister(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public static final Parcelable.Creator<Persister> CREATOR = new Parcelable.Creator<Persister>(){

        @Override
        public Persister createFromParcel(Parcel parcel) {
            return new Persister(parcel);
        }

        @Override
        public Persister[] newArray(int size) {
            return new Persister[0];
        }
    };

    //return hashcode of object
    public int describeContents() {
        return hashCode();
    }
}
