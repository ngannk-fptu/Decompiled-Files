/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class XMLUtils {
    public static String getContainedText(Node parent, String childTagName) {
        try {
            Node tag = ((Element)parent).getElementsByTagName(childTagName).item(0);
            String text = ((Text)tag.getFirstChild()).getData();
            return text;
        }
        catch (Exception e) {
            return null;
        }
    }
}

