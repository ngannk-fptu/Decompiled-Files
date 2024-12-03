/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.eventmacro;

import com.atlassian.confluence.user.ConfluenceUser;
import java.util.HashMap;
import java.util.Map;

public class Reply {
    private long id;
    private String name;
    private int guests;
    private String comment;
    private String email;
    private boolean confirm;
    private Map<String, String> customValues;
    private Map<String, Boolean> customCheckboxes;
    private String userName;
    private boolean inWaitingList;

    public Reply() {
    }

    public Reply(long id) {
        this.id = id;
    }

    public Reply(long id, String name) {
        this.name = name;
        this.id = id;
    }

    public Reply(long id, String name, String email, int guests, String comment, Map<String, String> customValues, Map<String, Boolean> customCheckboxes, ConfluenceUser user, boolean inWaitingList) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.guests = guests;
        this.comment = comment;
        this.customValues = customValues;
        this.customCheckboxes = customCheckboxes;
        this.userName = user != null ? user.getName() : "";
        this.inWaitingList = inWaitingList;
    }

    public int getGuests() {
        return this.guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConfirm() {
        return this.confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Reply reply = (Reply)o;
        return this.id == reply.getId();
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + new Long(this.id).hashCode();
        return result;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public Map<String, String> getCustomValues() {
        if (this.customValues == null) {
            return new HashMap<String, String>();
        }
        return this.customValues;
    }

    public void setCustomValues(Map<String, String> customValues) {
        this.customValues = customValues;
    }

    public Map<String, Boolean> getCustomCheckboxes() {
        if (this.customCheckboxes == null) {
            return new HashMap<String, Boolean>();
        }
        return this.customCheckboxes;
    }

    public void setCustomCheckboxes(Map<String, Boolean> customCheckboxes) {
        this.customCheckboxes = customCheckboxes;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setInWaitingList(boolean inWaitingList) {
        this.inWaitingList = inWaitingList;
    }

    public boolean isInWaitingList() {
        return this.inWaitingList;
    }
}

