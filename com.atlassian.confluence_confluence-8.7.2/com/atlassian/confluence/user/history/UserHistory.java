/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.user.history;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UserHistory
implements Serializable {
    private final LinkedList<Long> contentHistory = new LinkedList();
    private final LinkedList<Long> labelHistory = new LinkedList();
    private int maxHistoryLength;

    public UserHistory(int historyLength) {
        this.maxHistoryLength = historyLength;
    }

    public void addContentEntity(ContentEntityObject content) {
        this.addContentEntity(content.getId());
    }

    private synchronized void addContentEntity(Long contentEntityId) {
        if (this.contentHistory.contains(contentEntityId)) {
            this.contentHistory.remove(contentEntityId);
        }
        this.contentHistory.addFirst(contentEntityId);
        this.trimSize();
    }

    public void addLabel(Label label) {
        this.addLabel(label.getId());
    }

    private synchronized void addLabel(Long labelId) {
        if (this.labelHistory.contains(labelId)) {
            this.labelHistory.remove(labelId);
        }
        this.labelHistory.addFirst(labelId);
        this.trimSize();
    }

    private synchronized void trimSize() {
        while (this.contentHistory.size() > this.maxHistoryLength) {
            this.contentHistory.removeLast();
        }
        while (this.labelHistory.size() > this.maxHistoryLength) {
            this.labelHistory.removeLast();
        }
    }

    public int getMaxHistoryLength() {
        return this.maxHistoryLength;
    }

    public void setMaxHistoryLength(int maxHistoryLength) {
        this.maxHistoryLength = maxHistoryLength;
        this.trimSize();
    }

    public synchronized List<Long> getContent() {
        return Collections.unmodifiableList(new ArrayList<Long>(this.contentHistory));
    }

    public synchronized List<Long> getLabels() {
        return Collections.unmodifiableList(new ArrayList<Long>(this.labelHistory));
    }
}

