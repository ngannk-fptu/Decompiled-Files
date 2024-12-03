/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.RewrittenUrlClass;
import org.tuckey.web.filters.urlrewrite.Rule;
import org.tuckey.web.filters.urlrewrite.RuleChain;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.extend.RewriteRule;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

public class ClassRule
implements Rule {
    private static Log log = Log.getLog(ClassRule.class);
    private String classStr;
    private RewriteRule localRule;
    private boolean initialised = false;
    private int id;
    private boolean enabled = true;
    private boolean valid = false;
    private boolean last = true;
    private List errors = new ArrayList();
    private static final String DEAULT_METHOD_STR = "matches";
    private String methodStr = "matches";
    private static Class[] methodParameterTypesHttp = new Class[2];
    private static Class[] methodParameterTypes;
    private Method destroyMethod;
    private Method initMethod;
    private Method matchesMethod;

    public RewrittenUrl matches(String url, HttpServletRequest hsRequest, HttpServletResponse hsResponse, RuleChain chain) throws IOException, ServletException {
        return this.matches(url, hsRequest, hsResponse);
    }

    public RewrittenUrl matches(String url, HttpServletRequest hsRequest, HttpServletResponse hsResponse) throws ServletException, IOException {
        Object returnedObj;
        if (!this.initialised) {
            return null;
        }
        Object[] args = new Object[]{hsRequest, hsResponse};
        if (log.isDebugEnabled()) {
            log.debug("running " + this.classStr + "." + this.methodStr + "(HttpServletRequest, HttpServletResponse)");
        }
        if (this.matchesMethod == null) {
            return null;
        }
        try {
            returnedObj = this.matchesMethod.invoke((Object)this.localRule, args);
        }
        catch (IllegalAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
            throw new ServletException((Throwable)e);
        }
        catch (InvocationTargetException e) {
            Throwable originalThrowable;
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
            if ((originalThrowable = e.getTargetException()) == null && (originalThrowable = e.getCause()) == null) {
                throw new ServletException((Throwable)e);
            }
            if (originalThrowable instanceof Error) {
                throw (Error)originalThrowable;
            }
            if (originalThrowable instanceof RuntimeException) {
                throw (RuntimeException)originalThrowable;
            }
            if (originalThrowable instanceof ServletException) {
                throw (ServletException)originalThrowable;
            }
            if (originalThrowable instanceof IOException) {
                throw (IOException)originalThrowable;
            }
            throw new ServletException(originalThrowable);
        }
        if (returnedObj != null && returnedObj instanceof RewriteMatch) {
            return new RewrittenUrlClass((RewriteMatch)returnedObj);
        }
        return null;
    }

    public boolean initialise(ServletContext context) {
        Object instance;
        Constructor<?> constructor;
        Class<?> ruleClass;
        this.initialised = true;
        try {
            ruleClass = Class.forName(this.classStr);
        }
        catch (ClassNotFoundException e) {
            this.addError("could not find " + this.classStr + " got a " + e.toString(), e);
            return false;
        }
        catch (NoClassDefFoundError e) {
            this.addError("could not find " + this.classStr + " got a " + e.toString(), e);
            return false;
        }
        try {
            constructor = ruleClass.getConstructor(null);
        }
        catch (NoSuchMethodException e) {
            this.addError("could not get constructor for " + this.classStr, e);
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug("looking for " + this.methodStr + " will try with several arguments");
        }
        try {
            this.matchesMethod = ruleClass.getMethod(this.methodStr, methodParameterTypes);
        }
        catch (NoSuchMethodException e) {
            try {
                this.matchesMethod = ruleClass.getMethod(this.methodStr, methodParameterTypesHttp);
            }
            catch (NoSuchMethodException e2) {
                this.addError("could not find " + this.methodStr + "(ServletRequest, ServletResponse) on " + this.classStr, e);
                this.addError("also tried " + this.methodStr + "(HttpServletRequest, HttpServletResponse)", e2);
            }
        }
        Method[] methods = ruleClass.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if ("destroy".equals(method.getName()) && method.getParameterTypes().length == 0) {
                log.debug("found destroy methodStr");
                this.destroyMethod = method;
            }
            if ("init".equals(method.getName()) && method.getParameterTypes().length == 1 && ServletContext.class.getName().equals(method.getParameterTypes()[0].getName())) {
                log.debug("found init methodStr");
                this.initMethod = method;
            }
            if (this.initMethod != null && this.destroyMethod != null) break;
        }
        log.debug("getting new instance of " + this.classStr);
        try {
            instance = constructor.newInstance(null);
        }
        catch (InstantiationException e) {
            this.logInvokeException("constructor", e);
            return false;
        }
        catch (IllegalAccessException e) {
            this.logInvokeException("constructor", e);
            return false;
        }
        catch (InvocationTargetException e) {
            this.logInvokeException("constructor", e);
            return false;
        }
        if (this.initMethod != null) {
            log.debug("about to run init(ServletContext) on " + this.classStr);
            Object[] args = new Object[]{context};
            try {
                this.initMethod.invoke(instance, args);
            }
            catch (IllegalAccessException e) {
                this.logInvokeException("init(ServletContext)", e);
                return false;
            }
            catch (InvocationTargetException e) {
                this.logInvokeException("init(ServletContext)", e);
                return false;
            }
        }
        this.localRule = (RewriteRule)instance;
        this.valid = true;
        return true;
    }

    private void logInvokeException(String methodStr, Exception e) {
        Throwable cause = e.getCause();
        if (cause == null) {
            this.addError("when invoking " + methodStr + " on " + this.classStr + " got an " + e.toString(), e);
        } else {
            this.addError("when invoking " + methodStr + " on " + this.classStr + " got an " + e.toString() + " caused by " + cause.toString(), cause);
        }
    }

    public void destroy() {
        if (this.localRule == null) {
            return;
        }
        this.localRule.destroy();
    }

    public String getName() {
        return this.classStr;
    }

    public String getDisplayName() {
        return "Class Rule " + this.classStr;
    }

    public boolean isLast() {
        return this.last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public void setClassStr(String classStr) {
        this.classStr = classStr;
    }

    public String getClassStr() {
        return this.classStr;
    }

    public void setMethodStr(String methodStr) {
        this.methodStr = StringUtils.trimToNull(methodStr);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setId(int i) {
        this.id = i;
    }

    public int getId() {
        return this.id;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isFilter() {
        return false;
    }

    public List getErrors() {
        return this.errors;
    }

    private void addError(String s, Throwable t) {
        log.error(this.getDisplayName() + " had error: " + s, t);
        this.errors.add(s);
    }

    static {
        ClassRule.methodParameterTypesHttp[0] = HttpServletRequest.class;
        ClassRule.methodParameterTypesHttp[1] = HttpServletResponse.class;
        methodParameterTypes = new Class[2];
        ClassRule.methodParameterTypes[0] = ServletRequest.class;
        ClassRule.methodParameterTypes[1] = ServletResponse.class;
    }
}

