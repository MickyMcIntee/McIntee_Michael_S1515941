package com.example.mcintee_michael_s1515941;

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

    public Item(String title, String description, String link, String pubDate, String category, double lat, double lon, String originDate, String location, int depth, double magnitude) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = parseDate(pubDate);
        this.category = category;
        this.lat = lat;
        this.lon = lon;
        this.originDate = parseDate(originDate);
        this.location = location;
        this.depth = depth;
        this.magnitude = magnitude;
    }

    public Item() {

    }

    public Date getOriginDate() {
        return originDate;
    }

    public String getLocation() {
        return location;
    }

    public int getDepth() {
        return depth;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDesctription(String description) {
        this.description = description;
        String[] tokens = description.split(";");
        this.originDate = parseDate(tokens[0].substring(tokens[0].indexOf(":")+2,tokens[0].length()-1));
        this.location = tokens[1].substring(tokens[1].indexOf(":")+2,tokens[1].length()-1);
        this.depth = Integer.parseInt(tokens[3].substring(tokens[3].indexOf(":")+2,tokens[3].length()-4));
        this.magnitude = Double.parseDouble(tokens[4].substring(tokens[4].indexOf(":")+2));
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(String date) {
        this.pubDate = parseDate(date);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Double.compare(item.lat, lat) == 0 &&
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

    @Override
    public int hashCode() {
        return Objects.hash(title, description, link, pubDate, category, lat, lon, originDate, location, depth, magnitude);
    }

    private Date parseDate(String pubDate) {
        try {
            return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss").parse(pubDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
