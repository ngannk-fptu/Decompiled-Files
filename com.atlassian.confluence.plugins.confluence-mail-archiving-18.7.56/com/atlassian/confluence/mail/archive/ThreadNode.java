/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.lucene.LuceneUtils
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class ThreadNode {
    private long mailId;
    private String title;
    private String messageId;
    private String from;
    private String canonicalSubject;
    private Date date;
    private ThreadNode parent;
    private Set<ThreadNode> children = new TreeSet<ThreadNode>((t1, t2) -> {
        if (t1.equals(t2)) {
            return 0;
        }
        if (t1.getDate() == null || t2.getDate() == null) {
            if (t1.getDate() == t2.getDate()) {
                return System.identityHashCode(t1) - System.identityHashCode(t2);
            }
            if (t1.getDate() == null) {
                return -1;
            }
            return 1;
        }
        int comparison = t1.getDate().compareTo(t2.getDate());
        if (comparison == 0) {
            return System.identityHashCode(t1) - System.identityHashCode(t2);
        }
        return comparison;
    });

    public static ThreadNode getEmptyThreadNode() {
        return new ThreadNode(0L, null);
    }

    public ThreadNode(long mailId, SearchResult searchResult) {
        if (searchResult != null) {
            this.mailId = mailId;
            this.title = searchResult.getField("title");
            this.messageId = searchResult.getField("messageid");
            this.from = searchResult.getField("from");
            this.canonicalSubject = searchResult.getField("canonicalsubject");
            this.date = LuceneUtils.stringToDate((String)searchResult.getField("created"));
        }
    }

    public boolean isEmpty() {
        return this.mailId == 0L;
    }

    public long getMailId() {
        if (this.mailId == 0L) {
            return System.identityHashCode(this);
        }
        return this.mailId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public String getFrom() {
        return this.from;
    }

    public Date getDate() {
        return new Date(this.date.getTime());
    }

    public ThreadNode getParent() {
        return this.parent;
    }

    public void setParent(ThreadNode parent) {
        if (parent != null) {
            if (this.conflicts(parent)) {
                return;
            }
            parent.getChildren().add(this);
        }
        this.parent = parent;
    }

    public Set<ThreadNode> getChildren() {
        return this.children;
    }

    public void setChildren(Set<ThreadNode> children) {
        this.children = children;
    }

    public ThreadNode getNodeWithMessageId(String messageId) {
        if (messageId.equals(this.messageId)) {
            return this;
        }
        for (ThreadNode child : this.children) {
            ThreadNode found = child.getNodeWithMessageId(messageId);
            if (found == null) continue;
            return found;
        }
        return null;
    }

    public int getVisibleThreadCount() {
        int count = 1;
        if (this.getParent() != null) {
            count += this.getParent().getChildren().size();
            if (this.getParent().getParent() != null) {
                ++count;
            }
        }
        return count += this.getChildren().size();
    }

    public int getDescendentsCount() {
        int descendantsCount = this.getChildren().size();
        for (ThreadNode aChildren : this.children) {
            descendantsCount += aChildren.getDescendentsCount();
        }
        return descendantsCount;
    }

    boolean descendentHasId(String otherMessageId) {
        return this.messageId != null && this.messageId.equals(otherMessageId) || this.children.stream().anyMatch(node -> node.descendentHasId(otherMessageId));
    }

    boolean ancestorHasId(String otherMessageId) {
        return this.messageId != null && this.messageId.equals(otherMessageId) || this.parent != null && this.parent.ancestorHasId(otherMessageId);
    }

    private boolean conflicts(ThreadNode parent) {
        return this.descendentHasId(parent.getMessageId()) || this.ancestorHasId(parent.getMessageId()) || parent.descendentHasId(this.messageId) || parent.ancestorHasId(this.messageId);
    }

    public boolean isIsolated() {
        return this.parent == null && this.children.isEmpty();
    }

    public String getCanonicalSubject() {
        return this.canonicalSubject;
    }
}

