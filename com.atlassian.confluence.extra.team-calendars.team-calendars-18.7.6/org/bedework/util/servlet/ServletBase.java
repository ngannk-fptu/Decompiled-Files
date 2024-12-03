/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionListener
 */
package org.bedework.util.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.bedework.util.jmx.ConfBase;
import org.bedework.util.servlet.MethodBase;
import org.bedework.util.servlet.io.CharArrayWrappedResponse;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.WebdavTags;

public abstract class ServletBase
extends HttpServlet
implements HttpSessionListener,
ServletContextListener {
    protected boolean debug;
    protected boolean dumpContent;
    protected transient Logger log;
    protected HashMap<String, MethodBase.MethodInfo> methods = new HashMap();
    private static volatile HashMap<String, Waiter> waiters = new HashMap();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.dumpContent = "true".equals(config.getInitParameter("dumpContent"));
        this.addMethods();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            if ((method = this.getMethod(methodName)) == null) {
                this.logIt("No method for '" + methodName + "'");
            } else {
                method.doMethod(req, (HttpServletResponse)resp);
            }
        }
        catch (Throwable t) {
            serverError = this.handleException(t, (HttpServletResponse)resp, serverError);
        }
        finally {
            try {
                this.tryWait(req, false);
            }
            catch (Throwable methodName) {}
            if (this.debug && this.dumpContent && resp instanceof CharArrayWrappedResponse) {
                CharArrayWrappedResponse wresp = (CharArrayWrappedResponse)((Object)resp);
                if (wresp.getUsedOutputStream()) {
                    this.debugMsg("------------------------ response written to output stream -------------------");
                } else {
                    String str = wresp.toString();
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
            try {
                HttpSession sess = req.getSession(false);
                if (sess != null) {
                    sess.invalidate();
                }
            }
            catch (Throwable sess) {}
        }
    }

    private boolean handleException(Throwable t, HttpServletResponse resp, boolean serverError) {
        if (serverError) {
            return true;
        }
        try {
            this.getLogger().error((Object)this, t);
            this.sendError(t, resp);
            return true;
        }
        catch (Throwable t1) {
            return true;
        }
    }

    private void sendError(Throwable t, HttpServletResponse resp) {
        try {
            if (this.debug) {
                this.debugMsg("setStatus(500)");
            }
            resp.sendError(500, t.getMessage());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private boolean emitError(QName errorTag, String extra, Writer wtr) {
        try {
            XmlEmit xml = new XmlEmit();
            xml.startEmit(wtr);
            xml.openTag(WebdavTags.error);
            xml.closeTag(WebdavTags.error);
            xml.flush();
            return true;
        }
        catch (Throwable t1) {
            return false;
        }
    }

    protected void addMethod(String methodName, MethodBase.MethodInfo info) {
        this.methods.put(methodName, info);
    }

    protected abstract void addMethods();

    protected abstract void initMethodBase(MethodBase var1, ConfBase var2, boolean var3) throws ServletException;

    public MethodBase getMethod(String name) throws Exception {
        MethodBase.MethodInfo mi = this.methods.get(name.toUpperCase());
        try {
            MethodBase mb = mi.getMethodClass().newInstance();
            this.initMethodBase(mb, this.getConfigurator(), this.dumpContent);
            return mb;
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            throw new Exception(t);
        }
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
            String val;
            String key;
            Enumeration names = req.getHeaderNames();
            String title = "Request headers";
            log.debug(title);
            while (names.hasMoreElements()) {
                key = (String)names.nextElement();
                val = req.getHeader(key);
                log.debug("  " + key + " = \"" + val + "\"");
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
                val = req.getParameter(key);
                log.debug("  " + key + " = \"" + val + "\"");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    protected abstract ConfBase getConfigurator();

    public void contextInitialized(ServletContextEvent sce) {
        this.getConfigurator().start();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        this.getConfigurator().stop();
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

