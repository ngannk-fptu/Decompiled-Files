/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.RewrittenUrlClass;
import org.tuckey.web.filters.urlrewrite.Run;
import org.tuckey.web.filters.urlrewrite.Runnable;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public class CatchElem
implements Runnable {
    private static Log log = Log.getLog(CatchElem.class);
    private String classStr;
    private String error = null;
    private boolean valid = false;
    private boolean initialised = false;
    private Class exceptionClass;
    private ArrayList runs = new ArrayList();
    private static boolean loadClass = true;

    public static void setLoadClass(boolean loadClass) {
        CatchElem.loadClass = loadClass;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isInitialised() {
        return this.initialised;
    }

    public boolean initialise(ServletContext servletContext) {
        this.initialised = true;
        boolean ok = true;
        if (log.isDebugEnabled()) {
            log.debug("looking for class " + this.classStr);
        }
        if (loadClass) {
            try {
                this.exceptionClass = Class.forName(this.classStr);
            }
            catch (ClassNotFoundException e) {
                this.setError("could not find " + this.classStr + " got a " + e.toString(), e);
                return false;
            }
            catch (NoClassDefFoundError e) {
                this.setError("could not find " + this.classStr + " got a " + e.toString(), e);
                return false;
            }
        }
        for (int i = 0; i < this.runs.size(); ++i) {
            Run run = (Run)this.runs.get(i);
            if (run.initialise(servletContext, this.exceptionClass)) continue;
            ok = false;
        }
        this.valid = ok;
        return this.valid;
    }

    public boolean matches(Throwable t) {
        return t != null && this.exceptionClass != null && this.exceptionClass.isInstance(t);
    }

    protected RewrittenUrl execute(HttpServletRequest hsRequest, HttpServletResponse hsResponse, Throwable originalThrowable) throws IOException, ServletException, InvocationTargetException {
        int runsSize = this.runs.size();
        RewriteMatch lastRunMatch = null;
        if (runsSize > 0) {
            log.trace("performing runs");
            for (int i = 0; i < runsSize; ++i) {
                Run run = (Run)this.runs.get(i);
                lastRunMatch = run.execute(hsRequest, hsResponse, originalThrowable);
            }
        }
        if (lastRunMatch == null) {
            return null;
        }
        return new RewrittenUrlClass(lastRunMatch);
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
        log.error(error);
    }

    public void setError(String error, Throwable t) {
        this.error = error;
        log.error(error, t);
    }

    public String getClassStr() {
        return this.classStr;
    }

    public void setClassStr(String classStr) {
        this.classStr = classStr;
    }

    public void addRun(Run run) {
        this.runs.add(run);
    }

    public List getRuns() {
        return this.runs;
    }
}

