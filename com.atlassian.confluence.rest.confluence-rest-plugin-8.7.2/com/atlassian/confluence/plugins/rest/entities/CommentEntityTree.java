/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.plugins.rest.common.expand.Expander
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.rest.entities.CommentEntityTreeExpander;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.plugins.rest.common.expand.Expander;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="comments")
@XmlAccessorType(value=XmlAccessType.FIELD)
@Expander(value=CommentEntityTreeExpander.class)
public class CommentEntityTree {
    @XmlElement(name="content")
    private List<ContentEntity> contents;
    @XmlAttribute(name="total")
    private int total;
    @XmlTransient
    private final List<Comment> comments;

    public CommentEntityTree() {
        this.comments = new ArrayList<Comment>();
        this.total = 0;
    }

    public CommentEntityTree(int total, List<Comment> comments) {
        this.comments = comments;
        this.total = total;
    }

    public int getTotal() {
        return this.total;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public void setContents(List<ContentEntity> contents) {
        this.contents = contents;
    }

    public List<ContentEntity> getContents() {
        return this.contents;
    }

    public String toString() {
        return new StringJoiner(", ", CommentEntityTree.class.getSimpleName() + "[", "]").add("contents=" + this.contents).add("total=" + this.total).add("comments=" + this.comments).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommentEntityTree)) {
            return false;
        }
        CommentEntityTree that = (CommentEntityTree)o;
        return this.total == that.total && Objects.equals(this.contents, that.contents) && Objects.equals(this.comments, that.comments);
    }

    public int hashCode() {
        return Objects.hash(this.total, this.contents, this.comments);
    }
}

