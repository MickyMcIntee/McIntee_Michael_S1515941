package com.mcintech.mcintee_michael_s1515941;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * The item class is a blueprint which objects are made from to hold info on 1 individual earthquake.
 * It is used to hold the info in a complex data structure to mirror the real world.
 * @author michaelmcintee
 * @version 1.0
 * @see java.io.Serializable
 * StudentID - S1515941
 * Programme - BSc SDfB
 */
public class Item implements Serializable {
    private String title;
    private String description;
    private String link;
    private Date pubDate;
    private String category;
    private double lat;
    private double lon;
    private Date originDate;
    private String location;
    private int depth;
    private double magnitude;

    /**
     * The populated item constructor which is used to build an item where all elements of the items
     * information are know.
     * @param title the title of the item
     * @param description the description of the item which holds information in a delimited format
     * @param link a link to where the item can be found
     * @param pubDate the date when the item was published
     * @param category the category of the item
     * @param lat the latitude of the item
     * @param lon the longitude of the item
     * @param originDate the origin date of the item, the time of the quake
     * @param location the location of the earth quake
     * @param depth the depth of the quake
     * @param magnitude the magnitude or power of the quake
     */
    public Item(String title, String description, String link, String pubDate, String category, double lat, double lon, String originDate, String location, int depth, double magnitude) {
        this.title = title;                 //This snipped sets the value of the new objects parameters to the value of the parameters passed in.
        this.description = description;     //As above
        this.link = link;                   //etc.
        this.pubDate = parseDate(pubDate);
        this.category = category;
        this.lat = lat;
        this.lon = lon;
        this.originDate = parseDate(originDate);
        this.location = location;
        this.depth = depth;
        this.magnitude = magnitude;
    }

    /**
     * The blank item constructor is the default constructor where it doesn't set any of the parameters
     * so all the parameters of the object will be at their default state of blank
     * this is used by the pull parser which makes calls to setters when creating the item.
     */
    public Item() {
        //Create the object without doing anything to it's attributes.
    }

    /**
     * Gets the date of the quake.
     * @return a Date object which is the date of the quake.
     */
    public Date getOriginDate() {
        return originDate; //Like all getters this returns the value of the objects field.
    }

    /**
     * Gets the location of the quake.
     * @return the string location of the quake.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the depth of the quake.
     * @return the value of the depth of the quake.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Gets the magnitude of the quake.
     * @return the value of the objects magnitude.
     */
    public double getMagnitude() {
        return magnitude;
    }

    /**
     * Gets the title of the object.
     * @return the title of the object.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The set title method is used to take value passed in to the method and apply it to the
     * objects attribute of the name.
     * @param title to be set to the objects field value of the same name.
     */
    public void setTitle(String title) {
        this.title = title; //Like all setters this.title references the objects title to equal the title parameter passed to the method.
    }

    /**
     * Gets the description of the object.
     * @return the value of the description attribute of the object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description and because the description has more info in a delimited format
     * the description is split out to tokens and each token has string manipulation performed to
     * get the relevant attributes. These attributes are:
     *      The origin date of the quake.
     *      The location of the quake.
     *      The depth of the quake.
     *      The magnitude of the quake.
     * The description holds other info like lat and long but these are described in the xml under
     * a different name space geo so can be retrieved there instead of from the description.
     * @param description The value of the description to be applied to the attribute and split for other
     *                    attributes.
     */
    public void setDescription(String description) {
        this.description = description;
        String[] tokens = description.split(";");
        this.originDate = parseDate(tokens[0].substring(tokens[0].indexOf(":")+2,tokens[0].length()-1));
        this.location = tokens[1].substring(tokens[1].indexOf(":")+2,tokens[1].length()-1);
        this.depth = Integer.parseInt(tokens[3].substring(tokens[3].indexOf(":")+2,tokens[3].length()-4));
        this.magnitude = Double.parseDouble(tokens[4].substring(tokens[4].indexOf(":")+2));
    }

    /**
     * Gets the link of the item from the link attribute.
     * @return the value of the objects link attribute.
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the value of the objects link attribute using a string passed in to the method.
     * @param link the value to be applied to the objects link attribute.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Gets the items published date object from the pubDate attribute.
     * @return the value of the objects pubDate Date attribute.
     */
    public Date getPubDate() {
        return pubDate;
    }

    /**
     * Sets the value of the objects pubDate attribute using a passed in string and parsing the
     * date object from it to be applied to the field.
     * @param date a string in the correct standard format to be turned in to a date object and passed to
     *             the field.
     */
    public void setPubDate(String date) {
        this.pubDate = parseDate(date);
    }

    /**
     * Gets the category of the objects category attribute.
     * @return the value of the objects category attribute.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the value of the objects category attribute to the value of a string parameter category.
     * @param category the value to be applied to the objects category field.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the value of the objects lat latitude attribute.
     * @return the value of the objects lat attribute.
     */
    public double getLat() {
        return lat;
    }

    /**
     * The set lat method to set the value of the lat latitude attribute of the object.
     * @param lat the value of the latitude to be applied to the objects attribute.
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Gets the value of the lon attribute from the object.
     * @return the value of the lon attribute of the object for longitude.
     */
    public double getLon() {
        return lon;
    }

    /**
     * Sets the value of the lon longitude attribute of the object.
     * @param lon the value the lon attribute should be set to.
     */
    public void setLon(double lon) {
        this.lon = lon; //Set the value of the called object lon attribute to the value of the lon parameter.
    }

    /**
     * The equals method changes how equality of the items are tested. It ensures that they are not
     * tested literally is this object that object but more is this object like that object,
     * does it have the same attributes etc. this ensures that testing equality is done by if an object
     * is the same from an attribute perspective and not literally object 1 is object 2 because this would
     * never be true. As this is not being used I've left the generated code inside.
     * @param o the object to be tested against the called object.
     * @return a true or false value true if they equal false if they don't
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; //if this object is equal to that then true
        if (o == null || getClass() != o.getClass()) return false; //If the object is null or the class of the called object is not equal to the class of the passed in object is false
        Item item = (Item) o; //Create item from passed in object.
        return Double.compare(item.lat, lat) == 0 && //Test each attribute of the object parameter in turn to the value of the called object.
                Double.compare(item.lon, lon) == 0 &&
                depth == item.depth &&
                Double.compare(item.magnitude, magnitude) == 0 &&
                Objects.equals(title, item.title) &&
                Objects.equals(description, item.description) &&
                Objects.equals(link, item.link) &&
                Objects.equals(pubDate, item.pubDate) &&
                Objects.equals(category, item.category) &&
                Objects.equals(originDate, item.originDate) &&
                Objects.equals(location, item.location);
    }

    /**
     * The parse date string is responsible for accepting a string and getting a date object setting it's
     * value to the value of a date parsed from the string passed in given the format of how the dates
     * are received.
     * @param pubDate a string version of the date to be parsed, in the format of day name, day, month, year hour:minute:second
     * @return the Date object which has a value of the date of the parsed string otherwise null if broke.
     */
    private Date parseDate(String pubDate) {
        try {
            return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss").parse(pubDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
