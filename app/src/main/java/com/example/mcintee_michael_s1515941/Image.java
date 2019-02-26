package com.example.mcintee_michael_s1515941;

import java.io.Serializable;

/**
 * The image class which holds information about images stored from the xml. This class implements serializable as the owning object is parcelable.
 * @author michaelmcintee
 * @version 1.0
 * @see java.io.Serializable
 * StudentID - S1515941
 * Programme - BSc SDfB
 */
public class Image implements Serializable {

    String title;
    String url;
    String link;

    /**
     * Blank image creation required as never instantiated with data.
     */
    public Image() {

    }

    /**
     * Get the title of the image
     * @return string title of the image.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the image object.
     * @param title the passed in string to be used to set the image object title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the url of the image object where the image is held.
     * @return the string value of the url of where the image is held.
     */
    public String getURL() {
        return url;
    }

    /**
     * Set the url of the object to the url of where the image is held.
     * @param url the url of where the image is held.
     */
    public void setURL(String url) {
        this.url = url;
    }

    /**
     * Get the link to the image.
     * @return the link to the image.
     */
    public String getLink() {
        return link;
    }

    /**
     * Set the link of the object to the value of the link string.
     * @param link which is the string which will be applied to the object.
     */
    public void setLink(String link) {
        this.link = link;
    }
}
