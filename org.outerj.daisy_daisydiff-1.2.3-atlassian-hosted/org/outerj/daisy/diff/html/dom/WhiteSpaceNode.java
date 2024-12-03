/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom;

import org.outerj.daisy.diff.html.dom.Node;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.dom.TextNode;
import org.outerj.daisy.diff.html.modification.Modification;

public class WhiteSpaceNode
extends TextNode {
    public WhiteSpaceNode(TagNode parent, String s) {
        super(parent, s);
    }

    public WhiteSpaceNode(TagNode parent, String s, Node like) {
        this(parent, s);
        try {
            TextNode textNode = (TextNode)like;
            Modification newModification = textNode.getModification().clone();
            newModification.setFirstOfID(false);
            this.setModification(newModification);
        }
        catch (ClassCastException classCastException) {
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
    }

    public static boolean isWhiteSpace(char c) {
        switch (c) {
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': {
                return true;
            }
        }
        return false;
    }
}

