/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;

public class UpdateCalendarParam {
    @FormParam(value="type")
    String type;
    @FormParam(value="parentId")
    String parentSubCalendarId;
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="name")
    String name;
    @FormParam(value="description")
    String description;
    @FormParam(value="color")
    String color;
    @FormParam(value="spaceKey")
    String spaceKey;
    @FormParam(value="timeZoneId")
    String timeZoneId;
    @FormParam(value="location")
    String location;
    @FormParam(value="userName")
    String userName;
    @FormParam(value="password")
    String password;
    @FormParam(value="include")
    List<String> subCalendarIncludes;
    @FormParam(value="calendarContext")
    @DefaultValue(value="myCalendars")
    String calendarContext;
    @FormParam(value="viewingSpaceKey")
    @DefaultValue(value="viewingSpaceKey")
    String viewingSpaceKey;

    public String getCalendarContext() {
        return this.calendarContext;
    }

    public void setCalendarContext(String calendarContext) {
        this.calendarContext = calendarContext;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentSubCalendarId() {
        return this.parentSubCalendarId;
    }

    public void setParentSubCalendarId(String parentSubCalendarId) {
        this.parentSubCalendarId = parentSubCalendarId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public List<String> getSubCalendarIncludes() {
        return this.subCalendarIncludes;
    }

    public void setSubCalendarIncludes(List<String> subCalendarIncludes) {
        this.subCalendarIncludes = subCalendarIncludes;
    }

    public String getTimeZoneId() {
        return this.timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getViewingSpaceKey() {
        return this.viewingSpaceKey;
    }

    public void setViewingSpaceKey(String viewingSpaceKey) {
        this.viewingSpaceKey = viewingSpaceKey;
    }
}

