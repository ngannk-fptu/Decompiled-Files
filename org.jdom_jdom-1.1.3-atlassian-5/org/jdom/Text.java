/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import org.jdom.Content;
import org.jdom.IllegalDataException;
import org.jdom.Verifier;

public class Text
extends Content {
    private static final String CVS_ID = "@(#) $RCSfile: Text.java,v $ $Revision: 1.25 $ $Date: 2007/11/10 05:28:59 $ $Name:  $";
    static final String EMPTY_STRING = "";
    protected String value;

    protected Text() {
    }

    public Text(String str) {
        this.setText(str);
    }

    public String getText() {
        return this.value;
    }

    public String getTextTrim() {
        return this.getText().trim();
    }

    public String getTextNormalize() {
        return Text.normalizeString(this.getText());
    }

    public static String normalizeString(String str) {
        if (str == null) {
            return EMPTY_STRING;
        }
        char[] c = str.toCharArray();
        char[] n = new char[c.length];
        boolean white = true;
        int pos = 0;
        for (int i = 0; i < c.length; ++i) {
            if (" \t\n\r".indexOf(c[i]) != -1) {
                if (white) continue;
                n[pos++] = 32;
                white = true;
                continue;
            }
            n[pos++] = c[i];
            white = false;
        }
        if (white && pos > 0) {
            --pos;
        }
        return new String(n, 0, pos);
    }

    public Text setText(String str) {
        if (str == null) {
            this.value = EMPTY_STRING;
            return this;
        }
        String reason = Verifier.checkCharacterData(str);
        if (reason != null) {
            throw new IllegalDataException(str, "character content", reason);
        }
        this.value = str;
        return this;
    }

    public void append(String str) {
        if (str == null) {
            return;
        }
        String reason = Verifier.checkCharacterData(str);
        if (reason != null) {
            throw new IllegalDataException(str, "character content", reason);
        }
        if (str.length() > 0) {
            this.value = this.value + str;
        }
    }

    public void append(Text text) {
        if (text == null) {
            return;
        }
        this.value = this.value + text.getText();
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public String toString() {
        return new StringBuffer(64).append("[Text: ").append(this.getText()).append("]").toString();
    }

    @Override
    public Object clone() {
        Text text = (Text)super.clone();
        text.value = this.value;
        return text;
    }
}

