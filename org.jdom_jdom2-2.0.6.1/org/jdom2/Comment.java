/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import org.jdom2.Content;
import org.jdom2.IllegalDataException;
import org.jdom2.Parent;
import org.jdom2.Verifier;
import org.jdom2.output.XMLOutputter;

public class Comment
extends Content {
    private static final long serialVersionUID = 200L;
    protected String text;

    protected Comment() {
        super(Content.CType.Comment);
    }

    public Comment(String text) {
        super(Content.CType.Comment);
        this.setText(text);
    }

    public String getValue() {
        return this.text;
    }

    public String getText() {
        return this.text;
    }

    public Comment setText(String text) {
        String reason = Verifier.checkCommentData(text);
        if (reason != null) {
            throw new IllegalDataException(text, "comment", reason);
        }
        this.text = text;
        return this;
    }

    public Comment clone() {
        return (Comment)super.clone();
    }

    public Comment detach() {
        return (Comment)super.detach();
    }

    protected Comment setParent(Parent parent) {
        return (Comment)super.setParent(parent);
    }

    public String toString() {
        return "[Comment: " + new XMLOutputter().outputString(this) + "]";
    }
}

