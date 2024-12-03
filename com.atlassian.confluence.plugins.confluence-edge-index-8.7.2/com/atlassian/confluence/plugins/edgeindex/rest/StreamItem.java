/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.dto.UserDto
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.edgeindex.rest;

import com.atlassian.confluence.plugins.edgeindex.rest.CountItem;
import com.atlassian.confluence.plugins.rest.dto.UserDto;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StreamItem {
    @XmlAttribute
    private long id;
    @XmlAttribute
    private String title;
    @XmlAttribute
    private String url;
    @XmlAttribute
    private UserDto author;
    @XmlAttribute
    private String friendlyDate;
    @XmlAttribute
    private String date;
    @XmlAttribute
    private int numberOfLikes;
    @XmlAttribute
    private int numberOfComments;
    @XmlAttribute
    private String excerpt;
    @XmlAttribute
    private List<String> imageUris;
    @XmlAttribute
    private String contentCssClass;
    @XmlAttribute
    private String iconCssClass;
    @XmlAttribute
    private String score;
    @XmlAttribute
    private List<CountItem> counts;

    public StreamItem() {
    }

    public StreamItem(long id, String title, String url, UserDto author, String friendlyDate, String date, int numberOfLikes, int numberOfComments, List<CountItem> counts) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.author = author;
        this.date = date;
        this.friendlyDate = friendlyDate;
        this.numberOfLikes = numberOfLikes;
        this.numberOfComments = numberOfComments;
        this.counts = counts;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContentCssClass() {
        return this.contentCssClass;
    }

    public void setContentCssClass(String contentCssClass) {
        this.contentCssClass = contentCssClass;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UserDto getAuthor() {
        return this.author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
    }

    public String getFriendlyDate() {
        return this.friendlyDate;
    }

    public void setFriendlyDate(String friendlyDate) {
        this.friendlyDate = friendlyDate;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumberOfLikes() {
        return this.numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfComments() {
        return this.numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public String getExcerpt() {
        return this.excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public List<String> getImageUris() {
        return this.imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }

    public String getIconCssClass() {
        return this.iconCssClass;
    }

    public void setIconCssClass(String iconCssClass) {
        this.iconCssClass = iconCssClass;
    }

    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public List<CountItem> getCounts() {
        return this.counts;
    }

    public void setCounts(List<CountItem> counts) {
        this.counts = counts;
    }
}

