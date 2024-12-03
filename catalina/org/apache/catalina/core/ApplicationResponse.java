/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletResponse
 *  javax.servlet.ServletResponseWrapper
 */
package org.apache.catalina.core;

import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;

class ApplicationResponse
extends ServletResponseWrapper {
    protected boolean included = false;

    ApplicationResponse(ServletResponse response, boolean included) {
        super(response);
        this.setIncluded(included);
    }

    public void reset() {
        if (!this.included || this.getResponse().isCommitted()) {
            this.getResponse().reset();
        }
    }

    public void setContentLength(int len) {
        if (!this.included) {
            this.getResponse().setContentLength(len);
        }
    }

    public void setContentLengthLong(long len) {
        if (!this.included) {
            this.getResponse().setContentLengthLong(len);
        }
    }

    public void setContentType(String type) {
        if (!this.included) {
            this.getResponse().setContentType(type);
        }
    }

    public void setLocale(Locale loc) {
        if (!this.included) {
            this.getResponse().setLocale(loc);
        }
    }

    public void setBufferSize(int size) {
        if (!this.included) {
            this.getResponse().setBufferSize(size);
        }
    }

    public void setResponse(ServletResponse response) {
        super.setResponse(response);
    }

    void setIncluded(boolean included) {
        this.included = included;
    }
}

