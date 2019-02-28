package com.example.mcintee_michael_s1515941;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Channel - A class used to hold information on the downloaded class and hold items of the class.
 * @author michaelmcintee
 * @see java.io.Serializable
 * @version 1.0
 * S1515941
 * BSc SDfB
 */
public class Channel implements Serializable {

    private String title;
    private String link;
    private String description;
    private String language;
    private Date lastBuildDate;
    private Image image;
    private LinkedList<Item> items;

    /**
     * Constructor sets up the channel object with a linked list. It's never needed to load with data as we always construct it by individual xml objects.
     */
    public Channel() {
        items = new LinkedList<>();
    }

    /**
     * This returns the title of the channel.
     * @return The title of the channel.
     */
    public String getTitle() {
        return title;
    }

    /**
     * This sets the value of title to the value of the title param.
     * @param title - This is the value that the channel title will be set to.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This gets the link of the channel supplied in the xml data.
     * @return Link - Return the value of the channel link.
     */
    public String getLink() {
        return link;
    }

    /**
     * This sets the value of the link of the channel.
     * @param link Link - set the value of the link to the link retrieved from the xml.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * This gets the value of the description from the channel object.
     * @return return the value of the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * This sets the value of the description of the channel to the value of the xml.
     * @param description The string to be applied to the description to the channel.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This gets the value of the language of the channel object.
     * @return the string value of the channels language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language of the channel object to the value of the xml.
     * @param language set the value of the language channel to the value of of the language parameter.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Get the value of the last build of the channel date.
     * @return the value of the last build date of the channel.
     */
    public Date getLastBuildDate() {
        return lastBuildDate;
    }

    /**
     * Set the value of the last build date.
     * @param lastBuildDate the value to set the last build date to.
     */
    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = parseDate(lastBuildDate); //Run the parse date function to get the date object of the string value.
    }

    /**
     * The get image returns the image object of the channel.
     * @return The image object of the channel to be returned.
     */
    public Image getImage() {
        return image;
    }

    /**
     * This sets the image object to an image object parameter passed in.
     * @param image this is the image object being passed in to be applied to the channel.
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * This is the get items method which returns the items list in the channels.
     * @return return the list of earthquake items.
     */
    public LinkedList<Item> getItems() {
        return items;
    }

    /**
     * Set the list of items in the channel.
     * @param items the items to be applied to the channel object.
     */
    public void setItems(LinkedList<Item> items) {
        this.items = items;
    }

    /**
     * The add item method which adds earthquake items to the channel item list.
     * @param item the item object to be put in the list.
     */
    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(int index) { items.remove(index); }

    /**
     * Overrides equality check to make sure each element of the object is equal to the values of the passed
     * in channel. This means that equality is tested differently from the default of is this actually
     * that and not is this like that.
     * @param o the object to compare with the current called object.
     * @return a boolean value of true or false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(title, channel.title) &&
                Objects.equals(link, channel.link) &&
                Objects.equals(description, channel.description) &&
                Objects.equals(language, channel.language) &&
                Objects.equals(lastBuildDate, channel.lastBuildDate) &&
                Objects.equals(image, channel.image) &&
                Objects.equals(items, channel.items);
    }

    /**
     * The parse date function which takes a string and turns it in to a date object.
     * @param dateString accept a string date
     * @return return the date object of the string.
     */
    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss").parse(dateString); //Try to parse date in the provided format
        } catch (ParseException e) {
            e.printStackTrace();
            return null; //If can't parse return a null date and print to stack trace.
        }
    }
}
