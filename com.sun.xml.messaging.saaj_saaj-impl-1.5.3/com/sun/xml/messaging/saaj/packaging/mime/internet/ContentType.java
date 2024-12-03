/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.internet.HeaderTokenizer;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParameterList;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParseException;

public final class ContentType {
    private String primaryType;
    private String subType;
    private ParameterList list;

    public ContentType() {
    }

    public ContentType(String primaryType, String subType, ParameterList list) {
        this.primaryType = primaryType;
        this.subType = subType;
        if (list == null) {
            list = new ParameterList();
        }
        this.list = list;
    }

    public ContentType(String s) throws ParseException {
        HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        HeaderTokenizer.Token tk = h.next();
        if (tk.getType() != -1) {
            throw new ParseException();
        }
        this.primaryType = tk.getValue();
        tk = h.next();
        if ((char)tk.getType() != '/') {
            throw new ParseException();
        }
        tk = h.next();
        if (tk.getType() != -1) {
            throw new ParseException();
        }
        this.subType = tk.getValue();
        String rem = h.getRemainder();
        if (rem != null) {
            this.list = new ParameterList(rem);
        }
    }

    public ContentType copy() {
        return new ContentType(this.primaryType, this.subType, this.list.copy());
    }

    public String getPrimaryType() {
        return this.primaryType;
    }

    public String getSubType() {
        return this.subType;
    }

    public String getBaseType() {
        return this.primaryType + '/' + this.subType;
    }

    public String getParameter(String name) {
        if (this.list == null) {
            return null;
        }
        return this.list.get(name);
    }

    public ParameterList getParameterList() {
        return this.list;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setParameter(String name, String value) {
        if (this.list == null) {
            this.list = new ParameterList();
        }
        this.list.set(name, value);
    }

    public void setParameterList(ParameterList list) {
        this.list = list;
    }

    public String toString() {
        if (this.primaryType == null || this.subType == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.primaryType).append('/').append(this.subType);
        if (this.list != null) {
            sb.append(this.list.toString());
        }
        return sb.toString();
    }

    public boolean match(ContentType cType) {
        if (!this.primaryType.equalsIgnoreCase(cType.getPrimaryType())) {
            return false;
        }
        String sType = cType.getSubType();
        if (this.subType.charAt(0) == '*' || sType.charAt(0) == '*') {
            return true;
        }
        return this.subType.equalsIgnoreCase(sType);
    }

    public boolean match(String s) {
        try {
            return this.match(new ContentType(s));
        }
        catch (ParseException pex) {
            return false;
        }
    }
}

