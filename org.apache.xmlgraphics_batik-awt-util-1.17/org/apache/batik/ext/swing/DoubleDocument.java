/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class DoubleDocument
extends PlainDocument {
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) {
            return;
        }
        String curVal = this.getText(0, this.getLength());
        boolean hasDot = curVal.indexOf(46) != -1;
        char[] buffer = str.toCharArray();
        char[] digit = new char[buffer.length];
        int j = 0;
        if (offs == 0 && buffer != null && buffer.length > 0 && buffer[0] == '-') {
            digit[j++] = buffer[0];
        }
        for (char aBuffer : buffer) {
            if (Character.isDigit(aBuffer)) {
                digit[j++] = aBuffer;
            }
            if (hasDot || aBuffer != '.') continue;
            digit[j++] = 46;
            hasDot = true;
        }
        String added = new String(digit, 0, j);
        try {
            StringBuffer val = new StringBuffer(curVal);
            val.insert(offs, added);
            String valStr = val.toString();
            if (valStr.equals(".") || valStr.equals("-") || valStr.equals("-.")) {
                super.insertString(offs, added, a);
            } else {
                Double.valueOf(valStr);
                super.insertString(offs, added, a);
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
    }

    public void setValue(double d) {
        try {
            this.remove(0, this.getLength());
            this.insertString(0, String.valueOf(d), null);
        }
        catch (BadLocationException badLocationException) {
            // empty catch block
        }
    }

    public double getValue() {
        try {
            String t = this.getText(0, this.getLength());
            if (t != null && t.length() > 0) {
                return Double.parseDouble(t);
            }
            return 0.0;
        }
        catch (BadLocationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

