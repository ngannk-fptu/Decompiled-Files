/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.msv;

import org.codehaus.stax2.validation.ValidationContext;
import org.xml.sax.Attributes;

final class AttributeProxy
implements Attributes {
    private final ValidationContext mContext;

    public AttributeProxy(ValidationContext ctxt) {
        this.mContext = ctxt;
    }

    @Override
    public int getIndex(String qName) {
        int cix = qName.indexOf(58);
        int acount = this.mContext.getAttributeCount();
        if (cix < 0) {
            for (int i = 0; i < acount; ++i) {
                String prefix;
                if (!qName.equals(this.mContext.getAttributeLocalName(i)) || (prefix = this.mContext.getAttributePrefix(i)) != null && prefix.length() != 0) continue;
                return i;
            }
        } else {
            String prefix = qName.substring(0, cix);
            String ln = qName.substring(cix + 1);
            for (int i = 0; i < acount; ++i) {
                String p2;
                if (!ln.equals(this.mContext.getAttributeLocalName(i)) || (p2 = this.mContext.getAttributePrefix(i)) == null || !prefix.equals(p2)) continue;
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getIndex(String uri, String localName) {
        return this.mContext.findAttributeIndex(uri, localName);
    }

    @Override
    public int getLength() {
        return this.mContext.getAttributeCount();
    }

    @Override
    public String getLocalName(int index) {
        return this.mContext.getAttributeLocalName(index);
    }

    @Override
    public String getQName(int index) {
        String prefix = this.mContext.getAttributePrefix(index);
        String ln = this.mContext.getAttributeLocalName(index);
        if (prefix == null || prefix.length() == 0) {
            return ln;
        }
        StringBuilder sb = new StringBuilder(prefix.length() + 1 + ln.length());
        sb.append(prefix);
        sb.append(':');
        sb.append(ln);
        return sb.toString();
    }

    @Override
    public String getType(int index) {
        return this.mContext.getAttributeType(index);
    }

    @Override
    public String getType(String qName) {
        return this.getType(this.getIndex(qName));
    }

    @Override
    public String getType(String uri, String localName) {
        return this.getType(this.getIndex(uri, localName));
    }

    @Override
    public String getURI(int index) {
        return this.mContext.getAttributeNamespace(index);
    }

    @Override
    public String getValue(int index) {
        return this.mContext.getAttributeValue(index);
    }

    @Override
    public String getValue(String qName) {
        return this.getValue(this.getIndex(qName));
    }

    @Override
    public String getValue(String uri, String localName) {
        return this.mContext.getAttributeValue(uri, localName);
    }
}

