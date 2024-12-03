/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.bedework.util.servlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.bedework.util.misc.Util;
import org.bedework.util.servlet.HttpServletUtils;

public class ReqUtil
implements Serializable {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected boolean errFlag;

    public ReqUtil(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public void setErrFlag(boolean val) {
        this.errFlag = val;
    }

    public boolean getErrFlag() {
        return this.errFlag;
    }

    public String getReqPar(String name) {
        return Util.checkNull(this.request.getParameter(name));
    }

    public String getReqPar(String name, String def) {
        String s = Util.checkNull(this.request.getParameter(name));
        if (s != null) {
            return s;
        }
        return def;
    }

    public boolean present(String name) {
        return this.request.getParameter(name) != null;
    }

    public boolean notNull(String name) throws Throwable {
        return this.getReqPar(name) != null;
    }

    public List<String> getReqPars(String name) throws Throwable {
        String[] s = this.request.getParameterValues(name);
        ArrayList<String> res = null;
        if (s == null || s.length == 0) {
            return null;
        }
        for (String par : s) {
            if ((par = Util.checkNull(par)) == null) continue;
            if (res == null) {
                res = new ArrayList<String>();
            }
            res.add(par);
        }
        return res;
    }

    public Integer getIntReqPar(String name) throws Throwable {
        String reqpar = this.getReqPar(name);
        if (reqpar == null) {
            return null;
        }
        return Integer.valueOf(reqpar);
    }

    public int getIntReqPar(String name, int defaultVal) throws Throwable {
        String reqpar = this.getReqPar(name);
        if (reqpar == null) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(reqpar);
        }
        catch (Throwable t) {
            return defaultVal;
        }
    }

    public Long getLongReqPar(String name) throws Throwable {
        String reqpar = this.getReqPar(name);
        if (reqpar == null) {
            return null;
        }
        return Long.valueOf(reqpar);
    }

    public long getLongReqPar(String name, long defaultVal) throws Throwable {
        String reqpar = this.getReqPar(name);
        if (reqpar == null) {
            return defaultVal;
        }
        try {
            return Long.parseLong(reqpar);
        }
        catch (Throwable t) {
            return defaultVal;
        }
    }

    public Boolean getBooleanReqPar(String name) throws Throwable {
        String reqpar = this.getReqPar(name);
        if (reqpar == null) {
            return null;
        }
        try {
            if (reqpar.equalsIgnoreCase("yes")) {
                reqpar = "true";
            }
            return Boolean.valueOf(reqpar);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public boolean getBooleanReqPar(String name, boolean defVal) throws Throwable {
        boolean val = defVal;
        Boolean valB = this.getBooleanReqPar(name);
        if (valB != null) {
            val = valB;
        }
        return val;
    }

    public void setSessionAttr(String attrName, Object val) {
        HttpSession sess = this.request.getSession(false);
        if (sess == null) {
            return;
        }
        sess.setAttribute(attrName, val);
    }

    public void removeSessionAttr(String attrName) {
        HttpSession sess = this.request.getSession(false);
        if (sess == null) {
            return;
        }
        sess.removeAttribute(attrName);
    }

    public Object getSessionAttr(String attrName) {
        HttpSession sess = this.request.getSession(false);
        if (sess == null) {
            return null;
        }
        return sess.getAttribute(attrName);
    }

    public void setRequestAttr(String attrName, Object val) {
        this.request.setAttribute(attrName, val);
    }

    public Object getRequestAttr(String attrName) {
        return this.request.getAttribute(attrName);
    }

    public String getRemoteAddr() {
        return this.request.getRemoteAddr();
    }

    public String getRemoteHost() {
        return this.request.getRemoteHost();
    }

    public int getRemotePort() {
        return this.request.getRemotePort();
    }

    public Collection<Locale> getLocales() {
        return HttpServletUtils.getLocales(this.request);
    }
}

