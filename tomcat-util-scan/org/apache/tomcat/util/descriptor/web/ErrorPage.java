/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.UDecoder
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.web.XmlEncodingBase;

public class ErrorPage
extends XmlEncodingBase
implements Serializable {
    private static final long serialVersionUID = 2L;
    private int errorCode = 0;
    private String exceptionType = null;
    private String location = null;

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorCode(String errorCode) {
        try {
            this.errorCode = Integer.parseInt(errorCode);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(nfe);
        }
    }

    public String getExceptionType() {
        return this.exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = UDecoder.URLDecode((String)location, (Charset)this.getCharset());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ErrorPage[");
        if (this.exceptionType == null) {
            sb.append("errorCode=");
            sb.append(this.errorCode);
        } else {
            sb.append("exceptionType=");
            sb.append(this.exceptionType);
        }
        sb.append(", location=");
        sb.append(this.location);
        sb.append(']');
        return sb.toString();
    }

    public String getName() {
        if (this.exceptionType == null) {
            return Integer.toString(this.errorCode);
        }
        return this.exceptionType;
    }
}

