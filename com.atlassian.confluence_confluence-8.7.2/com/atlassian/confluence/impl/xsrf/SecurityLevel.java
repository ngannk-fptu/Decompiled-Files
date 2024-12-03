/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.impl.xsrf;

import com.atlassian.xwork.HttpMethod;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

public enum SecurityLevel {
    OPT_IN{

        @Override
        public boolean getDefaultProtection() {
            return false;
        }
    }
    ,
    OPT_OUT{

        @Override
        public boolean getDefaultProtection() {
            return true;
        }
    }
    ,
    DEFAULT{

        @Override
        public boolean getDefaultProtection() {
            HttpServletRequest servletRequest = ServletActionContext.getRequest();
            String httpMethod = servletRequest == null ? "" : servletRequest.getMethod();
            return !HttpMethod.anyMatch((String)httpMethod, (HttpMethod[])new HttpMethod[]{HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.TRACE});
        }
    };


    public abstract boolean getDefaultProtection();
}

