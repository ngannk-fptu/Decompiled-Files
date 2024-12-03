/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import java.util.HashMap;
import org.w3c.dom.Node;
import org.w3c.dom.stylesheets.StyleSheet;

public interface StyleSheetFactory {
    public StyleSheet createStyleSheet(Node var1, HashMap<String, String> var2);
}

