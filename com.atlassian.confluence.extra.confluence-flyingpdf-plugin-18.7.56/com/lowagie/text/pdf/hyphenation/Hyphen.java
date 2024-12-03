/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.hyphenation;

import java.io.Serializable;

public class Hyphen
implements Serializable {
    private static final long serialVersionUID = -7666138517324763063L;
    public String preBreak;
    public String noBreak;
    public String postBreak;

    Hyphen(String pre, String no, String post) {
        this.preBreak = pre;
        this.noBreak = no;
        this.postBreak = post;
    }

    Hyphen(String pre) {
        this.preBreak = pre;
        this.noBreak = null;
        this.postBreak = null;
    }

    public String toString() {
        if (this.noBreak == null && this.postBreak == null && this.preBreak != null && this.preBreak.equals("-")) {
            return "-";
        }
        StringBuilder res = new StringBuilder("{");
        res.append(this.preBreak);
        res.append("}{");
        res.append(this.postBreak);
        res.append("}{");
        res.append(this.noBreak);
        res.append('}');
        return res.toString();
    }
}

