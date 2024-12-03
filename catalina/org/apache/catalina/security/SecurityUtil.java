/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.Servlet
 *  javax.servlet.ServletException
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.security;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Globals;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public final class SecurityUtil {
    private static final int INIT = 0;
    private static final int SERVICE = 1;
    private static final int DOFILTER = 1;
    private static final int EVENT = 2;
    private static final int DOFILTEREVENT = 2;
    private static final int DESTROY = 3;
    private static final String INIT_METHOD = "init";
    private static final String DOFILTER_METHOD = "doFilter";
    private static final String SERVICE_METHOD = "service";
    private static final String EVENT_METHOD = "event";
    private static final String DOFILTEREVENT_METHOD = "doFilterEvent";
    private static final String DESTROY_METHOD = "destroy";
    private static final Map<Class<?>, Method[]> classCache = new ConcurrentHashMap();
    private static final Log log = LogFactory.getLog(SecurityUtil.class);
    private static final boolean packageDefinitionEnabled = System.getProperty("package.definition") != null || System.getProperty("package.access") != null;
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.security");

    public static void doAsPrivilege(String methodName, Servlet targetObject) throws Exception {
        SecurityUtil.doAsPrivilege(methodName, targetObject, null, null, null);
    }

    public static void doAsPrivilege(String methodName, Servlet targetObject, Class<?>[] targetType, Object[] targetArguments) throws Exception {
        SecurityUtil.doAsPrivilege(methodName, targetObject, targetType, targetArguments, null);
    }

    public static void doAsPrivilege(String methodName, Servlet targetObject, Class<?>[] targetParameterTypes, Object[] targetArguments, Principal principal) throws Exception {
        Method method = null;
        Method[] methodsCache = classCache.get(Servlet.class);
        if (methodsCache == null) {
            method = SecurityUtil.createMethodAndCacheIt(null, Servlet.class, methodName, targetParameterTypes);
        } else {
            method = SecurityUtil.findMethod(methodsCache, methodName);
            if (method == null) {
                method = SecurityUtil.createMethodAndCacheIt(methodsCache, Servlet.class, methodName, targetParameterTypes);
            }
        }
        SecurityUtil.execute(method, targetObject, targetArguments, principal);
    }

    public static void doAsPrivilege(String methodName, Filter targetObject) throws Exception {
        SecurityUtil.doAsPrivilege(methodName, targetObject, null, null);
    }

    public static void doAsPrivilege(String methodName, Filter targetObject, Class<?>[] targetType, Object[] targetArguments) throws Exception {
        SecurityUtil.doAsPrivilege(methodName, targetObject, targetType, targetArguments, null);
    }

    public static void doAsPrivilege(String methodName, Filter targetObject, Class<?>[] targetParameterTypes, Object[] targetParameterValues, Principal principal) throws Exception {
        Method method = null;
        Method[] methodsCache = classCache.get(Filter.class);
        if (methodsCache == null) {
            method = SecurityUtil.createMethodAndCacheIt(null, Filter.class, methodName, targetParameterTypes);
        } else {
            method = SecurityUtil.findMethod(methodsCache, methodName);
            if (method == null) {
                method = SecurityUtil.createMethodAndCacheIt(methodsCache, Filter.class, methodName, targetParameterTypes);
            }
        }
        SecurityUtil.execute(method, targetObject, targetParameterValues, principal);
    }

    private static void execute(Method method, Object targetObject, Object[] targetArguments, Principal principal) throws Exception {
        try {
            Subject subject = null;
            PrivilegedExceptionAction<Void> pea = () -> {
                method.invoke(targetObject, targetArguments);
                return null;
            };
            if (targetArguments != null && targetArguments[0] instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest)targetArguments[0];
                boolean hasSubject = false;
                HttpSession session = request.getSession(false);
                if (session != null) {
                    subject = (Subject)session.getAttribute("javax.security.auth.subject");
                    boolean bl = hasSubject = subject != null;
                }
                if (subject == null) {
                    subject = new Subject();
                    if (principal != null) {
                        subject.getPrincipals().add(principal);
                    }
                }
                if (session != null && !hasSubject) {
                    session.setAttribute("javax.security.auth.subject", (Object)subject);
                }
            }
            Subject.doAsPrivileged(subject, pea, null);
        }
        catch (PrivilegedActionException pe) {
            Throwable e;
            if (pe.getException() instanceof InvocationTargetException) {
                e = pe.getException().getCause();
                ExceptionUtils.handleThrowable((Throwable)e);
            } else {
                e = pe;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("SecurityUtil.doAsPrivilege"), e);
            }
            if (e instanceof UnavailableException) {
                throw (UnavailableException)e;
            }
            if (e instanceof ServletException) {
                throw (ServletException)e;
            }
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new ServletException(e.getMessage(), e);
        }
    }

    private static Method findMethod(Method[] methodsCache, String methodName) {
        if (methodName.equals(INIT_METHOD)) {
            return methodsCache[0];
        }
        if (methodName.equals(DESTROY_METHOD)) {
            return methodsCache[3];
        }
        if (methodName.equals(SERVICE_METHOD)) {
            return methodsCache[1];
        }
        if (methodName.equals(DOFILTER_METHOD)) {
            return methodsCache[1];
        }
        if (methodName.equals(EVENT_METHOD)) {
            return methodsCache[2];
        }
        if (methodName.equals(DOFILTEREVENT_METHOD)) {
            return methodsCache[2];
        }
        return null;
    }

    private static Method createMethodAndCacheIt(Method[] methodsCache, Class<?> targetType, String methodName, Class<?>[] parameterTypes) throws Exception {
        if (methodsCache == null) {
            methodsCache = new Method[4];
        }
        Method method = targetType.getMethod(methodName, parameterTypes);
        if (methodName.equals(INIT_METHOD)) {
            methodsCache[0] = method;
        } else if (methodName.equals(DESTROY_METHOD)) {
            methodsCache[3] = method;
        } else if (methodName.equals(SERVICE_METHOD)) {
            methodsCache[1] = method;
        } else if (methodName.equals(DOFILTER_METHOD)) {
            methodsCache[1] = method;
        } else if (methodName.equals(EVENT_METHOD)) {
            methodsCache[2] = method;
        } else if (methodName.equals(DOFILTEREVENT_METHOD)) {
            methodsCache[2] = method;
        }
        classCache.put(targetType, methodsCache);
        return method;
    }

    public static void remove(Object cachedObject) {
        classCache.remove(cachedObject);
    }

    public static boolean isPackageProtectionEnabled() {
        return packageDefinitionEnabled && Globals.IS_SECURITY_ENABLED;
    }
}

