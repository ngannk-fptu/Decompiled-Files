/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.encoding.HeaderTokenizer;
import com.sun.xml.ws.encoding.ParameterList;
import javax.xml.ws.WebServiceException;

public final class ContentType {
    private String primaryType;
    private String subType;
    private ParameterList list;

    public ContentType(String s) throws WebServiceException {
        HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        HeaderTokenizer.Token tk = h.next();
        if (tk.getType() != -1) {
            throw new WebServiceException();
        }
        this.primaryType = tk.getValue();
        tk = h.next();
        if ((char)tk.getType() != '/') {
            throw new WebServiceException();
        }
        tk = h.next();
        if (tk.getType() != -1) {
            throw new WebServiceException();
        }
        this.subType = tk.getValue();
        String rem = h.getRemainder();
        if (rem != null) {
            this.list = new ParameterList(rem);
        }
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
}

