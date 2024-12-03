/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.ancestor.tagtostring;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.outerj.daisy.diff.html.ancestor.ChangeText;
import org.outerj.daisy.diff.html.ancestor.TagChangeSematic;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.modification.HtmlLayoutChange;
import org.xml.sax.Attributes;

public class TagToString {
    protected TagNode node;
    protected TagChangeSematic sem;
    private ResourceBundle bundle;
    private HtmlLayoutChange htmlLayoutChange = null;

    protected TagToString(TagNode node, TagChangeSematic sem, ResourceBundle bundle) {
        this.node = node;
        this.sem = sem;
        this.bundle = bundle;
    }

    public String getDescription() {
        return this.getString("diff-" + this.node.getQName());
    }

    public void getRemovedDescription(ChangeText txt) {
        this.htmlLayoutChange = new HtmlLayoutChange();
        this.htmlLayoutChange.setEndingTag(this.node.getEndTag());
        this.htmlLayoutChange.setOpeningTag(this.node.getOpeningTag());
        this.htmlLayoutChange.setType(HtmlLayoutChange.Type.TAG_REMOVED);
        if (this.sem == TagChangeSematic.MOVED) {
            txt.addText(this.getMovedOutOf() + " " + this.getArticle().toLowerCase() + " ");
            txt.addHtml("<b>");
            txt.addText(this.getDescription().toLowerCase());
            txt.addHtml("</b>");
        } else if (this.sem == TagChangeSematic.STYLE) {
            txt.addHtml("<b>");
            txt.addText(this.getDescription());
            txt.addHtml("</b>");
            txt.addText(" " + this.getStyleRemoved().toLowerCase());
        } else {
            txt.addHtml("<b>");
            txt.addText(this.getDescription());
            txt.addHtml("</b>");
            txt.addText(" " + this.getRemoved().toLowerCase());
        }
        this.addAttributes(txt, this.node.getAttributes());
        txt.addText(".");
    }

    public void getAddedDescription(ChangeText txt) {
        this.htmlLayoutChange = new HtmlLayoutChange();
        this.htmlLayoutChange.setEndingTag(this.node.getEndTag());
        this.htmlLayoutChange.setOpeningTag(this.node.getOpeningTag());
        this.htmlLayoutChange.setType(HtmlLayoutChange.Type.TAG_ADDED);
        if (this.sem == TagChangeSematic.MOVED) {
            txt.addText(this.getMovedTo() + " " + this.getArticle().toLowerCase() + " ");
            txt.addHtml("<b>");
            txt.addText(this.getDescription().toLowerCase());
            txt.addHtml("</b>");
        } else if (this.sem == TagChangeSematic.STYLE) {
            txt.addHtml("<b>");
            txt.addText(this.getDescription());
            txt.addHtml("</b>");
            txt.addText(" " + this.getStyleAdded().toLowerCase());
        } else {
            txt.addHtml("<b>");
            txt.addText(this.getDescription());
            txt.addHtml("</b>");
            txt.addText(" " + this.getAdded().toLowerCase());
        }
        this.addAttributes(txt, this.node.getAttributes());
        txt.addText(".");
    }

    protected String getMovedTo() {
        return this.getString("diff-movedto");
    }

    protected String getStyleAdded() {
        return this.getString("diff-styleadded");
    }

    protected String getAdded() {
        return this.getString("diff-added");
    }

    protected String getMovedOutOf() {
        return this.getString("diff-movedoutof");
    }

    protected String getStyleRemoved() {
        return this.getString("diff-styleremoved");
    }

    protected String getRemoved() {
        return this.getString("diff-removed");
    }

    protected void addAttributes(ChangeText txt, Attributes attributes) {
        if (attributes.getLength() < 1) {
            return;
        }
        txt.addText(" " + this.getWith().toLowerCase() + " " + this.translateArgument(attributes.getQName(0)) + " " + attributes.getValue(0));
        for (int i = 1; i < attributes.getLength() - 1; ++i) {
            txt.addText(", " + this.translateArgument(attributes.getQName(i)) + " " + attributes.getValue(i));
        }
        if (attributes.getLength() > 1) {
            txt.addText(" " + this.getAnd().toLowerCase() + " " + this.translateArgument(attributes.getQName(attributes.getLength() - 1)) + " " + attributes.getValue(attributes.getLength() - 1));
        }
    }

    private String getAnd() {
        return this.getString("diff-and");
    }

    private String getWith() {
        return this.getString("diff-with");
    }

    protected String translateArgument(String name) {
        if (name.equalsIgnoreCase("src")) {
            return this.getSource().toLowerCase();
        }
        if (name.equalsIgnoreCase("width")) {
            return this.getWidth().toLowerCase();
        }
        if (name.equalsIgnoreCase("height")) {
            return this.getHeight().toLowerCase();
        }
        return name;
    }

    private String getHeight() {
        return this.getString("diff-height");
    }

    private String getWidth() {
        return this.getString("diff-width");
    }

    protected String getSource() {
        return this.getString("diff-source");
    }

    protected String getArticle() {
        return this.getString("diff-" + this.node.getQName() + "-article");
    }

    public String getString(String key) {
        try {
            return this.bundle.getString(key);
        }
        catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public HtmlLayoutChange getHtmlLayoutChange() {
        return this.htmlLayoutChange;
    }
}

