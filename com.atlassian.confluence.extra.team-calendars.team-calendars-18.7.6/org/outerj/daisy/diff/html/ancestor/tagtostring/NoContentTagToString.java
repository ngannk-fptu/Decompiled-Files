/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.ancestor.tagtostring;

import java.util.ResourceBundle;
import org.outerj.daisy.diff.html.ancestor.ChangeText;
import org.outerj.daisy.diff.html.ancestor.TagChangeSematic;
import org.outerj.daisy.diff.html.ancestor.tagtostring.TagToString;
import org.outerj.daisy.diff.html.dom.TagNode;

public class NoContentTagToString
extends TagToString {
    protected NoContentTagToString(TagNode node, TagChangeSematic sem, ResourceBundle bundle) {
        super(node, sem, bundle);
    }

    @Override
    public void getAddedDescription(ChangeText txt) {
        txt.addText(this.getChangedTo() + " " + this.getArticle().toLowerCase() + " ");
        txt.addHtml("<b>");
        txt.addText(this.getDescription().toLowerCase());
        txt.addHtml("</b>");
        this.addAttributes(txt, this.node.getAttributes());
        txt.addText(".");
    }

    private String getChangedTo() {
        return this.getString("diff-changedto");
    }

    @Override
    public void getRemovedDescription(ChangeText txt) {
        txt.addText(this.getChangedFrom() + " " + this.getArticle().toLowerCase() + " ");
        txt.addHtml("<b>");
        txt.addText(this.getDescription().toLowerCase());
        txt.addHtml("</b>");
        this.addAttributes(txt, this.node.getAttributes());
        txt.addText(".");
    }

    private String getChangedFrom() {
        return this.getString("diff-changedfrom");
    }
}

