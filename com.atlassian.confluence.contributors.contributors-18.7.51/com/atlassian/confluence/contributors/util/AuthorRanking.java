/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 */
package com.atlassian.confluence.contributors.util;

import com.atlassian.confluence.util.GeneralUtil;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class AuthorRanking {
    private int edits;
    private int comments;
    private int labels;
    private int watches;
    private long lastEditTime;
    private long lastCommentTime;
    private long lastLabelTime;
    private final Set<String> labelList;
    private final SortedMap<String, String> labelMap;
    private final SortedMap<String, String> editMap;
    private final SortedMap<String, String> commentMap;
    private final SortedMap<String, String> watchMap;
    private final String idString;
    private final String fullNameString;

    public AuthorRanking(String authorId, String fullName, long lastEditTime) {
        this.idString = authorId;
        this.fullNameString = fullName;
        this.lastEditTime = lastEditTime;
        this.lastCommentTime = 0L;
        this.lastLabelTime = 0L;
        this.labelList = new TreeSet<String>();
        this.labelMap = new TreeMap<String, String>();
        this.editMap = new TreeMap<String, String>();
        this.commentMap = new TreeMap<String, String>();
        this.watchMap = new TreeMap<String, String>();
    }

    public AuthorRanking(String authorId, String fullName) {
        this(authorId, fullName, 0L);
    }

    public String getIdString() {
        return this.idString;
    }

    public String getFullNameString() {
        return this.fullNameString;
    }

    public long getLastEditTime() {
        return this.lastEditTime;
    }

    public void setLastEditTime(long lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public void setLastCommentTime(long lastCommentTime) {
        this.lastCommentTime = lastCommentTime;
    }

    public AuthorRanking incrementEdits() {
        ++this.edits;
        return this;
    }

    public AuthorRanking incrementComments() {
        ++this.comments;
        return this;
    }

    public AuthorRanking incrementLabels() {
        ++this.labels;
        return this;
    }

    public void incrementWatches(String id, String desc) {
        if (!this.watchMap.containsKey(id)) {
            ++this.watches;
            this.watchMap.put(id, desc);
        }
    }

    public void incrementEdits(String id, String desc, long editTime) {
        ++this.edits;
        this.editMap.put(id, desc);
        if (editTime > this.lastEditTime) {
            this.lastEditTime = editTime;
        }
    }

    public void incrementComments(String id, String desc, long commentTime) {
        ++this.comments;
        this.commentMap.put(id, desc);
        if (commentTime > this.lastCommentTime) {
            this.lastCommentTime = commentTime;
        }
    }

    public AuthorRanking incrementLabels(String id, String desc, long labelTime) {
        ++this.labels;
        this.labelMap.put(id, desc);
        if (labelTime > this.lastLabelTime) {
            this.lastLabelTime = labelTime;
        }
        return this;
    }

    public int getEdits() {
        return this.edits;
    }

    public String toString() {
        return this.idString + " " + this.fullNameString + " " + new Date(this.lastEditTime);
    }

    public int getComments() {
        return this.comments;
    }

    public int getLabels() {
        return this.labels;
    }

    public long getLastCommentTime() {
        return this.lastCommentTime;
    }

    public long getLastLabelTime() {
        return this.lastLabelTime;
    }

    public void setLastLabelTime(long lastLabelTime) {
        this.lastLabelTime = lastLabelTime;
    }

    public long getLastActiveTime() {
        long ret = this.lastEditTime;
        if (ret < this.lastLabelTime) {
            ret = this.lastLabelTime;
        }
        if (ret < this.lastCommentTime) {
            ret = this.lastCommentTime;
        }
        return ret;
    }

    public Date getLastActiveDate() {
        return new Date(this.getLastActiveTime());
    }

    public String getRelativeLastActiveTimeStr() {
        return GeneralUtil.getRelativeTime((Date)this.getLastActiveDate());
    }

    public Set<String> getLabelList() {
        return this.labelList;
    }

    public void addLabel(String label) {
        this.labelList.add(label);
    }

    public void setEdits(int edits) {
        this.edits = edits;
    }

    public int getTotalCount() {
        return this.comments + this.edits + this.labels + this.watches;
    }

    public SortedMap<String, String> getCommentMap() {
        return this.commentMap;
    }

    public SortedMap<String, String> getEditMap() {
        return this.editMap;
    }

    public SortedMap<String, String> getLabelMap() {
        return this.labelMap;
    }

    public int getWatches() {
        return this.watches;
    }

    public void setWatches(int watches) {
        this.watches = watches;
    }

    public SortedMap<String, String> getWatchMap() {
        return this.watchMap;
    }
}

