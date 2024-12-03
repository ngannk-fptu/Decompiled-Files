/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.dto;

import com.atlassian.confluence.plugins.rest.dto.UserDto;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
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
    private int numberOfLikes;
    @XmlAttribute
    private int numberOfComments;
    @XmlAttribute
    private String excerpt;
    @XmlAttribute
    private List<String> imageUris;
    @XmlAttribute
    private String iconCssClass;
    @XmlAttribute
    private String score;

    public StreamItem() {
    }

    public StreamItem(long id, String title, String url, UserDto author, String friendlyDate, int numberOfLikes, int numberOfComments) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.author = author;
        this.friendlyDate = friendlyDate;
        this.numberOfLikes = numberOfLikes;
        this.numberOfComments = numberOfComments;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String toString() {
        return new StringJoiner(", ", StreamItem.class.getSimpleName() + "[", "]").add("id=" + this.id).add("title='" + this.title + "'").add("url='" + this.url + "'").add("author=" + this.author).add("friendlyDate='" + this.friendlyDate + "'").add("numberOfLikes=" + this.numberOfLikes).add("numberOfComments=" + this.numberOfComments).add("excerpt='" + this.excerpt + "'").add("imageUris=" + this.imageUris).add("iconCssClass='" + this.iconCssClass + "'").add("score='" + this.score + "'").toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StreamItem)) {
            return false;
        }
        StreamItem that = (StreamItem)o;
        return this.id == that.id && this.numberOfLikes == that.numberOfLikes && this.numberOfComments == that.numberOfComments && Objects.equals(this.title, that.title) && Objects.equals(this.url, that.url) && Objects.equals(this.author, that.author) && Objects.equals(this.friendlyDate, that.friendlyDate) && Objects.equals(this.excerpt, that.excerpt) && Objects.equals(this.imageUris, that.imageUris) && Objects.equals(this.iconCssClass, that.iconCssClass) && Objects.equals(this.score, that.score);
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }
}

