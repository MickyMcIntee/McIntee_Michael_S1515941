package com.mcintech.mcintee_michael_s1515941;

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

    private Channel channel = new Channel(); //Create new empty channel.

    /**
     * The write to parcel method writes the channel to a parcel, it is called when adding a parcelable
     * item to an intent.
     * @param dest The destination of the parcel, which parcel it is put in.
     * @param flags Any settings during the write of the parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeValue(channel);
    }

    /**
     * Create a new persister using a provided parce.
     * @param p a parcel that has a channel inside it to get the value out
     *          and apply to the channel of the persister.
     */
    public Persister(Parcel p) {
        //Set the objects channel to the value of reading a parcel for the channel class using it's class loader.
        //As this implements the parcelable interface it can be read from parcel.
        channel = (Channel) p.readValue(channel.getClass().getClassLoader());
    }

    /**
     * Create persister with a channel.
     * @param channel a channel to be applied to the objects channel.
     */
    public Persister(Channel channel) {
        this.channel = channel; //This is used when it's not read from a parcel but created from having a channel.
    }

    /**
     * Get channel returns the channel object from the persister to be used.
     * @return the channel from the persister.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Anonymous parcel creations creating a parcel for the Persister type, which can be done
     * because it implements the required parcelable interface and thus the methods required.
     */
    public static final Parcelable.Creator<Persister> CREATOR = new Parcelable.Creator<Persister>(){

        /**
         * The create from parcel method takes a parcel and creates a persister object from that
         * parcel by calling the constructor above that reads the channel from the parcel.
         * @param parcel A parcel with the channel in it.
         * @return the new persister object with the channel.
         */
        @Override
        public Persister createFromParcel(Parcel parcel) {
            return new Persister(parcel);
        }

        /**
         * Creates a persister array with a number of persisters so multiple persisters can be put in the parcel.
         * @param size
         * @return
         */
        @Override
        public Persister[] newArray(int size) {
            //Create an array of persisters to the size of the passed in value which could be used to pull one
            //Array, not necessary here as we only have one persister.
            return new Persister[0];
        }
    };

    /**
     * Returns a hashcode value of the contents of the parcel used for equality normally. Must be implemented
     * as part of the parcelable interface.
     * @return The hashcode value of the contents of the parcelable.
     */
    @Override
    public int describeContents() {
        return hashCode();
    }
}
