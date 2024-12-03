/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionListener
 */
package org.bedework.util.servlet;

import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.log4j.Logger;

public class SessionListener
implements HttpSessionListener {
    private static volatile HashMap<String, Counts> countsMap = new HashMap();
    private static boolean logActive = true;
    private static final String appNameInitParameter = "rpiappname";

    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        ServletContext sc = session.getServletContext();
        String appname = this.getAppName(session);
        Counts ct = this.getCounts(appname);
        ++ct.activeSessions;
        ++ct.totalSessions;
        if (logActive) {
            this.logSessionCounts(session, true);
            sc.log("========= New session(" + appname + "): " + ct.activeSessions + " active, " + ct.totalSessions + " total. vm(used, max)=(" + Runtime.getRuntime().freeMemory() / 0x100000L + "M, " + Runtime.getRuntime().totalMemory() / 0x100000L + "M)");
        }
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        ServletContext sc = session.getServletContext();
        String appname = this.getAppName(session);
        Counts ct = this.getCounts(appname);
        if (ct.activeSessions > 0) {
            --ct.activeSessions;
        }
        if (logActive) {
            this.logSessionCounts(session, false);
            sc.log("========= Session destroyed(" + appname + "): " + ct.activeSessions + " active. vm(used, max)=(" + Runtime.getRuntime().freeMemory() / 0x100000L + "M, " + Runtime.getRuntime().totalMemory() / 0x100000L + "M)");
        }
    }

    public static void setLogActive(boolean val) {
        logActive = val;
    }

    protected void logSessionCounts(HttpSession sess, boolean start) {
        Logger log = Logger.getLogger(this.getClass());
        String appname = this.getAppName(sess);
        Counts ct = this.getCounts(appname);
        StringBuffer sb = start ? new StringBuffer("SESSION-START:") : new StringBuffer("SESSION-END:");
        sb.append(this.getSessionId(sess));
        sb.append(":");
        sb.append(appname);
        sb.append(":");
        sb.append(ct.activeSessions);
        sb.append(":");
        sb.append(ct.totalSessions);
        sb.append(":");
        sb.append(Runtime.getRuntime().freeMemory() / 0x100000L);
        sb.append("M:");
        sb.append(Runtime.getRuntime().totalMemory() / 0x100000L);
        sb.append("M");
        log.info(sb.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Counts getCounts(String name) {
        try {
            HashMap<String, Counts> hashMap = countsMap;
            synchronized (hashMap) {
                Counts c = countsMap.get(name);
                if (c == null) {
                    c = new Counts();
                    countsMap.put(name, c);
                }
                return c;
            }
        }
        catch (Throwable t) {
            return new Counts();
        }
    }

    private String getAppName(HttpSession sess) {
        ServletContext sc = sess.getServletContext();
        String appname = sc.getInitParameter(appNameInitParameter);
        if (appname == null) {
            appname = "?";
        }
        return appname;
    }

    private String getSessionId(HttpSession sess) {
        try {
            if (sess == null) {
                return "NO-SESSIONID";
            }
            return sess.getId();
        }
        catch (Throwable t) {
            return "SESSION-ID-EXCEPTION";
        }
    }

    private static class Counts {
        int activeSessions = 0;
        long totalSessions = 0L;

        private Counts() {
        }
    }
}

