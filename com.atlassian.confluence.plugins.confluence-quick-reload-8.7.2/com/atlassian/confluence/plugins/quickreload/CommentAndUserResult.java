/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.tinymceplugin.rest.entities.CommentResult
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.quickreload;

import com.atlassian.confluence.plugins.quickreload.Commenter;
import com.atlassian.confluence.tinymceplugin.rest.entities.CommentResult;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CommentAndUserResult {
    @XmlElement
    private Commenter commenter;
    @XmlElement
    private CommentResult comment;

    private CommentAndUserResult() {
    }

    public CommentAndUserResult(Commenter commenter, CommentResult comment) {
        this.commenter = commenter;
        this.comment = comment;
    }
}

