/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom;

import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.dom.TextNode;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class ImageNode
extends TextNode {
    private AttributesImpl attributes;

    public ImageNode(TagNode parent, Attributes attrs) {
        super(parent, "<img>" + ImageNode.safestring(attrs.getValue("src")).toLowerCase() + "</img>");
        this.attributes = new AttributesImpl(attrs);
    }

    @Override
    public boolean isSameText(TextNode other) {
        ImageNode otherImageNode;
        if (other == null) {
            return false;
        }
        try {
            otherImageNode = (ImageNode)other;
        }
        catch (ClassCastException e) {
            return false;
        }
        return this.getText().equalsIgnoreCase(otherImageNode.getText());
    }

    public AttributesImpl getAttributes() {
        return this.attributes;
    }

    private static String safestring(String nullable) {
        return nullable == null ? "" : nullable;
    }
}

