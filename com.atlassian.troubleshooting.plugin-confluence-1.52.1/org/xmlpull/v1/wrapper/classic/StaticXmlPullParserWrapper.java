/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.util.XmlPullUtil;
import org.xmlpull.v1.wrapper.XmlPullParserWrapper;
import org.xmlpull.v1.wrapper.classic.XmlPullParserDelegate;

public class StaticXmlPullParserWrapper
extends XmlPullParserDelegate
implements XmlPullParserWrapper {
    public StaticXmlPullParserWrapper(XmlPullParser pp) {
        super(pp);
    }

    public String getAttributeValue(String name) {
        return XmlPullUtil.getAttributeValue(this.pp, name);
    }

    public String getRequiredAttributeValue(String name) throws IOException, XmlPullParserException {
        return XmlPullUtil.getRequiredAttributeValue(this.pp, null, name);
    }

    public String getRequiredAttributeValue(String namespace, String name) throws IOException, XmlPullParserException {
        return XmlPullUtil.getRequiredAttributeValue(this.pp, namespace, name);
    }

    public String getRequiredElementText(String namespace, String name) throws IOException, XmlPullParserException {
        if (name == null) {
            throw new XmlPullParserException("name for element can not be null");
        }
        String text = null;
        this.nextStartTag(namespace, name);
        if (this.isNil()) {
            this.nextEndTag(namespace, name);
        } else {
            text = this.pp.nextText();
        }
        this.pp.require(3, namespace, name);
        return text;
    }

    public boolean isNil() throws IOException, XmlPullParserException {
        boolean result = false;
        String value = this.pp.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
        if ("true".equals(value)) {
            result = true;
        }
        return result;
    }

    public String getPITarget() throws IllegalStateException {
        return XmlPullUtil.getPITarget(this.pp);
    }

    public String getPIData() throws IllegalStateException {
        return XmlPullUtil.getPIData(this.pp);
    }

    public boolean matches(int type, String namespace, String name) throws XmlPullParserException {
        return XmlPullUtil.matches(this.pp, type, namespace, name);
    }

    public void nextStartTag() throws XmlPullParserException, IOException {
        if (this.pp.nextTag() != 2) {
            throw new XmlPullParserException("expected START_TAG and not " + this.pp.getPositionDescription());
        }
    }

    public void nextStartTag(String name) throws XmlPullParserException, IOException {
        this.pp.nextTag();
        this.pp.require(2, null, name);
    }

    public void nextStartTag(String namespace, String name) throws XmlPullParserException, IOException {
        this.pp.nextTag();
        this.pp.require(2, namespace, name);
    }

    public void nextEndTag() throws XmlPullParserException, IOException {
        XmlPullUtil.nextEndTag(this.pp);
    }

    public void nextEndTag(String name) throws XmlPullParserException, IOException {
        XmlPullUtil.nextEndTag(this.pp, null, name);
    }

    public void nextEndTag(String namespace, String name) throws XmlPullParserException, IOException {
        XmlPullUtil.nextEndTag(this.pp, namespace, name);
    }

    public String nextText(String namespace, String name) throws IOException, XmlPullParserException {
        return XmlPullUtil.nextText(this.pp, namespace, name);
    }

    public void skipSubTree() throws XmlPullParserException, IOException {
        XmlPullUtil.skipSubTree(this.pp);
    }

    public double readDouble() throws XmlPullParserException, IOException {
        double d;
        String value = this.pp.nextText();
        try {
            d = Double.parseDouble(value);
        }
        catch (NumberFormatException ex) {
            if (value.equals("INF") || value.toLowerCase().equals("infinity")) {
                d = Double.POSITIVE_INFINITY;
            }
            if (value.equals("-INF") || value.toLowerCase().equals("-infinity")) {
                d = Double.NEGATIVE_INFINITY;
            }
            if (value.equals("NaN")) {
                d = Double.NaN;
            }
            throw new XmlPullParserException("can't parse double value '" + value + "'", this, ex);
        }
        return d;
    }

    public float readFloat() throws XmlPullParserException, IOException {
        float f;
        String value = this.pp.nextText();
        try {
            f = Float.parseFloat(value);
        }
        catch (NumberFormatException ex) {
            if (value.equals("INF") || value.toLowerCase().equals("infinity")) {
                f = Float.POSITIVE_INFINITY;
            }
            if (value.equals("-INF") || value.toLowerCase().equals("-infinity")) {
                f = Float.NEGATIVE_INFINITY;
            }
            if (value.equals("NaN")) {
                f = Float.NaN;
            }
            throw new XmlPullParserException("can't parse float value '" + value + "'", this, ex);
        }
        return f;
    }

    private int parseDigits(String text, int offset, int length) throws XmlPullParserException {
        int value = 0;
        if (length > 9) {
            try {
                value = Integer.parseInt(text.substring(offset, offset + length));
            }
            catch (NumberFormatException ex) {
                throw new XmlPullParserException(ex.getMessage());
            }
        } else {
            int limit = offset + length;
            while (offset < limit) {
                char chr;
                if ((chr = text.charAt(offset++)) >= '0' && chr <= '9') {
                    value = value * 10 + (chr - 48);
                    continue;
                }
                throw new XmlPullParserException("non-digit in number value", this, null);
            }
        }
        return value;
    }

    private int parseInt(String text) throws XmlPullParserException {
        int offset = 0;
        int limit = text.length();
        if (limit == 0) {
            throw new XmlPullParserException("empty number value", this, null);
        }
        boolean negate = false;
        char chr = text.charAt(0);
        if (chr == '-') {
            if (limit > 9) {
                try {
                    return Integer.parseInt(text);
                }
                catch (NumberFormatException ex) {
                    throw new XmlPullParserException(ex.getMessage(), this, null);
                }
            }
            negate = true;
            ++offset;
        } else if (chr == '+') {
            ++offset;
        }
        if (offset >= limit) {
            throw new XmlPullParserException("Invalid number format", this, null);
        }
        int value = this.parseDigits(text, offset, limit - offset);
        if (negate) {
            return -value;
        }
        return value;
    }

    public int readInt() throws XmlPullParserException, IOException {
        try {
            int i = this.parseInt(this.pp.nextText());
            return i;
        }
        catch (NumberFormatException ex) {
            throw new XmlPullParserException("can't parse int value", this, ex);
        }
    }

    public String readString() throws XmlPullParserException, IOException {
        String xsiNil = this.pp.getAttributeValue("http://www.w3.org/2001/XMLSchema", "nil");
        if ("true".equals(xsiNil)) {
            this.nextEndTag();
            return null;
        }
        return this.pp.nextText();
    }

    public double readDoubleElement(String namespace, String name) throws XmlPullParserException, IOException {
        this.pp.require(2, namespace, name);
        return this.readDouble();
    }

    public float readFloatElement(String namespace, String name) throws XmlPullParserException, IOException {
        this.pp.require(2, namespace, name);
        return this.readFloat();
    }

    public int readIntElement(String namespace, String name) throws XmlPullParserException, IOException {
        this.pp.require(2, namespace, name);
        return this.readInt();
    }

    public String readStringElemet(String namespace, String name) throws XmlPullParserException, IOException {
        this.pp.require(2, namespace, name);
        return this.readString();
    }
}

