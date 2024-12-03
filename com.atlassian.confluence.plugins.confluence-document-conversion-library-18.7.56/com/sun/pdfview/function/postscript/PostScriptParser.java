/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class PostScriptParser {
    public List<String> parse(String scriptContent) {
        LinkedList<String> tokens = new LinkedList<String>();
        StringTokenizer tok = new StringTokenizer(scriptContent, " \t\n\r");
        while (tok.hasMoreTokens()) {
            String t = tok.nextToken();
            t = this.filterBlockStart(t);
            if ((t = this.filterBlockEnd(t)).length() <= 0) continue;
            tokens.add(t.trim());
        }
        return tokens;
    }

    private String filterBlockEnd(String t) {
        if (t.endsWith("}")) {
            t = t.substring(0, t.length() - 1);
        }
        return t;
    }

    private String filterBlockStart(String t) {
        if (t.startsWith("{")) {
            t = t.substring(1);
        }
        return t;
    }
}

