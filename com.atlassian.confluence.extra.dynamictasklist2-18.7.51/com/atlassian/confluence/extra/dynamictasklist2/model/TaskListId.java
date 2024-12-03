/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.util.Base32;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TaskListId {
    private final int occurrence;
    private final String listName;

    public TaskListId(String listName, int occurrence) {
        if (occurrence < 1) {
            throw new IllegalArgumentException("occurrence must be a positive integer");
        }
        this.listName = StringUtils.defaultString((String)listName);
        this.occurrence = occurrence;
    }

    public TaskListId(String listId) {
        if (StringUtils.isBlank((CharSequence)listId)) {
            throw new IllegalArgumentException("listId cannot be null or blank");
        }
        int occurrenceSeparatorIndex = listId.indexOf(":");
        if (occurrenceSeparatorIndex == -1) {
            throw new IllegalArgumentException("listId not correctly formatted. Missing separator character: ':'");
        }
        this.occurrence = TaskList.getOccuranceFromId(listId);
        if (occurrenceSeparatorIndex < listId.length() - 1) {
            try {
                this.listName = new String(Base32.decode(listId.substring(occurrenceSeparatorIndex + 1)), "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.listName = "";
        }
    }

    public int getOccurrence() {
        return this.occurrence;
    }

    public String getListName() {
        return this.listName;
    }

    public String toString() {
        try {
            return this.occurrence + ":" + Base32.encode(this.listName.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals((Object)this, (Object)obj, (String[])new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode((Object)this, (String[])new String[0]);
    }
}

