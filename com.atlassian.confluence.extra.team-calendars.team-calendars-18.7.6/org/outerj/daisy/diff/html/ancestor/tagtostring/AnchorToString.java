/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.ancestor.tagtostring;

import java.util.ResourceBundle;
import org.outerj.daisy.diff.html.ancestor.ChangeText;
import org.outerj.daisy.diff.html.ancestor.TagChangeSematic;
import org.outerj.daisy.diff.html.ancestor.tagtostring.TagToString;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class AnchorToString
extends TagToString {
    protected AnchorToString(TagNode node, TagChangeSematic sem, ResourceBundle bundle) {
        super(node, sem, bundle);
    }

    @Override
    protected void addAttributes(ChangeText txt, Attributes attributes) {
        AttributesImpl newAttrs = new AttributesImpl(attributes);
        String href = newAttrs.getValue("href");
        if (href != null) {
            txt.addText(" " + this.getWithDestination().toLowerCase() + " " + href);
            newAttrs.removeAttribute(newAttrs.getIndex("href"));
        }
        super.addAttributes(txt, newAttrs);
    }

    private String getWithDestination() {
        return this.getString("diff-withdestination");
    }
}

