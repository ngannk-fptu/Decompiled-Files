/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionListener
 */
package org.bedework.webdav.servlet.common;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.bedework.util.servlet.io.CharArrayWrappedResponse;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.AclMethod;
import org.bedework.webdav.servlet.common.CopyMethod;
import org.bedework.webdav.servlet.common.DeleteMethod;
import org.bedework.webdav.servlet.common.GetMethod;
import org.bedework.webdav.servlet.common.HeadMethod;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.common.MkcolMethod;
import org.bedework.webdav.servlet.common.MoveMethod;
import org.bedework.webdav.servlet.common.OptionsMethod;
import org.bedework.webdav.servlet.common.PostMethod;
import org.bedework.webdav.servlet.common.PropFindMethod;
import org.bedework.webdav.servlet.common.PropPatchMethod;
import org.bedework.webdav.servlet.common.PutMethod;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;

public abstract class WebdavServlet
extends HttpServlet
implements HttpSessionListener {
    protected boolean debug;
    protected boolean dumpContent;
    protected boolean preserveSession;
    protected transient Logger log;
    protected HashMap<String, MethodBase.MethodInfo> methods = new HashMap();
    private static volatile HashMap<String, Waiter> waiters = new HashMap();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.dumpContent = "true".equals(config.getInitParameter("dumpContent"));
        this.preserveSession = "true".equals(config.getInitParameter("preserve-session"));
        this.addMethods();
    }

    public void setPreserveSession(boolean val) {
        this.preserveSession = val;
    }

    public abstract WebdavNsIntf getNsIntf(HttpServletRequest var1) throws WebdavException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebdavNsIntf intf = null;
        boolean serverError = false;
        try {
            MethodBase method;
            String methodName;
            this.debug = this.getLogger().isDebugEnabled();
            if (this.debug) {
                this.debugMsg("entry: " + req.getMethod());
                this.dumpRequest(req);
            }
            this.tryWait(req, true);
            intf = this.getNsIntf(req);
            if (req.getCharacterEncoding() == null) {
                req.setCharacterEncoding("UTF-8");
                if (this.debug) {
                    this.debugMsg("No charset specified in request; forced to UTF-8");
                }
            }
            if (this.debug && this.dumpContent) {
                resp = new CharArrayWrappedResponse((HttpServletResponse)resp, this.getLogger());
            }
            if ((methodName = req.getHeader("X-HTTP-Method-Override")) == null) {
                methodName = req.getMethod();
            }
            if ((method = intf.getMethod(methodName)) == null) {
                this.logIt("No method for '" + methodName + "'");
                resp.setStatus(405);
            } else {
                method.checkServerInfo(req, (HttpServletResponse)resp);
                method.doMethod(req, (HttpServletResponse)resp);
            }
        }
        catch (WebdavForbidden wdf) {
            this.sendError(intf, wdf, (HttpServletResponse)resp);
        }
        catch (Throwable t) {
            serverError = this.handleException(intf, t, (HttpServletResponse)resp, serverError);
        }
        finally {
            if (intf != null) {
                try {
                    intf.close();
                }
                catch (Throwable t) {
                    serverError = this.handleException(intf, t, (HttpServletResponse)resp, serverError);
                }
            }
            try {
                this.tryWait(req, false);
            }
            catch (Throwable t) {}
            if (this.debug && this.dumpContent && resp instanceof CharArrayWrappedResponse) {
                CharArrayWrappedResponse wresp = (CharArrayWrappedResponse)((Object)resp);
                if (wresp.getUsedOutputStream()) {
                    this.debugMsg("------------------------ response written to output stream -------------------");
                } else {
                    String str = wresp.toString();
                    if (str == null || str.length() == 0) {
                        this.debugMsg("------------------------ No response content -------------------");
                        resp.setContentLength(0);
                    } else {
                        this.debugMsg("------------------------ Dump of response -------------------");
                        this.debugMsg(str);
                        this.debugMsg("---------------------- End dump of response -----------------");
                        byte[] bs = str.getBytes();
                        resp = (HttpServletResponse)wresp.getResponse();
                        this.debugMsg("contentLength=" + bs.length);
                        resp.setContentLength(bs.length);
                        resp.getOutputStream().write(bs);
                    }
                }
            }
            if (!this.preserveSession) {
                try {
                    HttpSession sess = req.getSession(false);
                    if (sess != null) {
                        sess.invalidate();
                    }
                }
                catch (Throwable sess) {}
            }
        }
    }

    private boolean handleException(WebdavNsIntf intf, Throwable t, HttpServletResponse resp, boolean serverError) {
        if (serverError) {
            return true;
        }
        try {
            if (t instanceof WebdavException) {
                WebdavException wde = (WebdavException)t;
                int status = wde.getStatusCode();
                if (status == 500) {
                    this.getLogger().error((Object)this, wde);
                    serverError = true;
                }
                this.sendError(intf, wde, resp);
                return serverError;
            }
            this.getLogger().error((Object)this, t);
            this.sendError(intf, t, resp);
            return true;
        }
        catch (Throwable t1) {
            return true;
        }
    }

    private void sendError(WebdavNsIntf intf, Throwable t, HttpServletResponse resp) {
        try {
            try {
                intf.rollback();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (t instanceof WebdavException) {
                WebdavException wde = (WebdavException)t;
                QName errorTag = wde.getErrorTag();
                if (errorTag != null) {
                    if (this.debug) {
                        this.debugMsg("setStatus(" + wde.getStatusCode() + ") message=" + wde.getMessage());
                    }
                    resp.setStatus(wde.getStatusCode());
                    resp.setContentType("text/xml; charset=UTF-8");
                    if (!this.emitError(intf, errorTag, wde.getMessage(), resp.getWriter())) {
                        StringWriter sw = new StringWriter();
                        this.emitError(intf, errorTag, wde.getMessage(), sw);
                        try {
                            if (this.debug) {
                                this.debugMsg("setStatus(" + wde.getStatusCode() + ") message=" + wde.getMessage());
                            }
                            resp.sendError(wde.getStatusCode(), sw.toString());
                        }
                        catch (Throwable throwable) {}
                    }
                } else {
                    if (this.debug) {
                        this.debugMsg("setStatus(" + wde.getStatusCode() + ") message=" + wde.getMessage());
                    }
                    resp.sendError(wde.getStatusCode(), wde.getMessage());
                }
            } else {
                if (this.debug) {
                    this.debugMsg("setStatus(500) message=" + t.getMessage());
                }
                resp.sendError(500, t.getMessage());
            }
        }
        catch (Throwable ignored) {
            resp.setStatus(500);
        }
    }

    private boolean emitError(WebdavNsIntf intf, QName errorTag, String extra, Writer wtr) {
        try {
            XmlEmit xml = new XmlEmit();
            intf.addNamespace(xml);
            xml.startEmit(wtr);
            xml.openTag(WebdavTags.error);
            intf.emitError(errorTag, extra, xml);
            xml.closeTag(WebdavTags.error);
            xml.flush();
            return true;
        }
        catch (Throwable t1) {
            return false;
        }
    }

    protected void addMethods() {
        this.methods.put("ACL", new MethodBase.MethodInfo(AclMethod.class, false));
        this.methods.put("COPY", new MethodBase.MethodInfo(CopyMethod.class, false));
        this.methods.put("GET", new MethodBase.MethodInfo(GetMethod.class, false));
        this.methods.put("HEAD", new MethodBase.MethodInfo(HeadMethod.class, false));
        this.methods.put("OPTIONS", new MethodBase.MethodInfo(OptionsMethod.class, false));
        this.methods.put("PROPFIND", new MethodBase.MethodInfo(PropFindMethod.class, false));
        this.methods.put("DELETE", new MethodBase.MethodInfo(DeleteMethod.class, true));
        this.methods.put("MKCOL", new MethodBase.MethodInfo(MkcolMethod.class, true));
        this.methods.put("MOVE", new MethodBase.MethodInfo(MoveMethod.class, true));
        this.methods.put("POST", new MethodBase.MethodInfo(PostMethod.class, true));
        this.methods.put("PROPPATCH", new MethodBase.MethodInfo(PropPatchMethod.class, true));
        this.methods.put("PUT", new MethodBase.MethodInfo(PutMethod.class, true));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tryWait(HttpServletRequest req, boolean in) throws Throwable {
        Waiter wtr = null;
        Object object = waiters;
        synchronized (object) {
            String key = req.getRemoteUser();
            if (key == null) {
                return;
            }
            wtr = waiters.get(key);
            if (wtr == null) {
                if (!in) {
                    return;
                }
                wtr = new Waiter();
                wtr.active = true;
                waiters.put(key, wtr);
                return;
            }
        }
        object = wtr;
        synchronized (object) {
            if (!in) {
                wtr.active = false;
                wtr.notify();
                return;
            }
            ++wtr.waiting;
            while (wtr.active) {
                if (this.debug) {
                    this.log.debug("in: waiters=" + wtr.waiting);
                }
                wtr.wait();
            }
            --wtr.waiting;
            wtr.active = true;
        }
    }

    public void sessionCreated(HttpSessionEvent se) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String sessid = session.getId();
        if (sessid == null) {
            return;
        }
        HashMap<String, Waiter> hashMap = waiters;
        synchronized (hashMap) {
            waiters.remove(sessid);
        }
    }

    public void dumpRequest(HttpServletRequest req) {
        Logger log = this.getLogger();
        try {
            String key;
            Enumeration names = req.getHeaderNames();
            String title = "Request headers";
            log.debug(title);
            while (names.hasMoreElements()) {
                key = (String)names.nextElement();
                Enumeration vals = req.getHeaders(key);
                while (vals.hasMoreElements()) {
                    String val = (String)vals.nextElement();
                    if (key.toLowerCase().equals("authorization") && val != null && val.toLowerCase().startsWith("basic")) {
                        val = "Basic **********";
                    }
                    log.debug("  " + key + " = \"" + val + "\"");
                }
            }
            names = req.getParameterNames();
            title = "Request parameters";
            log.debug(title + " - global info and uris");
            log.debug("getRemoteAddr = " + req.getRemoteAddr());
            log.debug("getRequestURI = " + req.getRequestURI());
            log.debug("getRemoteUser = " + req.getRemoteUser());
            log.debug("getRequestedSessionId = " + req.getRequestedSessionId());
            log.debug("HttpUtils.getRequestURL(req) = " + req.getRequestURL());
            log.debug("contextPath=" + req.getContextPath());
            log.debug("query=" + req.getQueryString());
            log.debug("contentlen=" + req.getContentLength());
            log.debug("request=" + req);
            log.debug("parameters:");
            log.debug(title);
            while (names.hasMoreElements()) {
                key = (String)names.nextElement();
                String val = req.getParameter(key);
                log.debug("  " + key + " = \"" + val + "\"");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(((Object)((Object)this)).getClass());
        }
        return this.log;
    }

    public void debugMsg(String msg) {
        this.getLogger().debug(msg);
    }

    public void logIt(String msg) {
        this.getLogger().info(msg);
    }

    protected void error(Throwable t) {
        this.getLogger().error((Object)this, t);
    }

    static class Waiter {
        boolean active;
        int waiting;

        Waiter() {
        }
    }
}

