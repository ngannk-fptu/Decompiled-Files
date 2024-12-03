/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.catalina.manager.util;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Session;
import org.apache.tomcat.util.ExceptionUtils;

public class SessionUtils {
    private static final String STRUTS_LOCALE_KEY = "org.apache.struts.action.LOCALE";
    private static final String JSTL_LOCALE_KEY = "javax.servlet.jsp.jstl.fmt.locale";
    private static final String SPRING_LOCALE_KEY = "org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE";
    private static final String[] LOCALE_TEST_ATTRIBUTES = new String[]{"org.apache.struts.action.LOCALE", "org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE", "javax.servlet.jsp.jstl.fmt.locale", "Locale", "java.util.Locale"};
    private static final String[] USER_TEST_ATTRIBUTES = new String[]{"Login", "User", "userName", "UserName", "Utilisateur", "SPRING_SECURITY_LAST_USERNAME"};

    private SessionUtils() {
    }

    public static Locale guessLocaleFromSession(Session in_session) {
        return SessionUtils.guessLocaleFromSession(in_session.getSession());
    }

    public static Locale guessLocaleFromSession(HttpSession in_session) {
        if (null == in_session) {
            return null;
        }
        try {
            Object probableEngine;
            Object obj;
            Locale locale = null;
            for (String localeTestAttribute : LOCALE_TEST_ATTRIBUTES) {
                obj = in_session.getAttribute(localeTestAttribute);
                if (obj instanceof Locale) {
                    locale = (Locale)obj;
                    break;
                }
                obj = in_session.getAttribute(localeTestAttribute.toLowerCase(Locale.ENGLISH));
                if (obj instanceof Locale) {
                    locale = (Locale)obj;
                    break;
                }
                obj = in_session.getAttribute(localeTestAttribute.toUpperCase(Locale.ENGLISH));
                if (!(obj instanceof Locale)) continue;
                locale = (Locale)obj;
                break;
            }
            if (null != locale) {
                return locale;
            }
            ArrayList<Object> tapestryArray = new ArrayList<Object>();
            Enumeration enumeration = in_session.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                String name = (String)enumeration.nextElement();
                if (!name.contains("tapestry") || !name.contains("engine") || null == in_session.getAttribute(name)) continue;
                tapestryArray.add(in_session.getAttribute(name));
            }
            if (tapestryArray.size() == 1 && null != (probableEngine = tapestryArray.get(0))) {
                try {
                    Method readMethod = probableEngine.getClass().getMethod("getLocale", null);
                    Object possibleLocale = readMethod.invoke(probableEngine, (Object[])null);
                    if (possibleLocale instanceof Locale) {
                        locale = (Locale)possibleLocale;
                    }
                }
                catch (Exception e) {
                    Throwable t = ExceptionUtils.unwrapInvocationTargetException((Throwable)e);
                    ExceptionUtils.handleThrowable((Throwable)t);
                }
            }
            if (null != locale) {
                return locale;
            }
            ArrayList<Object> localeArray = new ArrayList<Object>();
            Enumeration enumeration2 = in_session.getAttributeNames();
            while (enumeration2.hasMoreElements()) {
                String name = (String)enumeration2.nextElement();
                obj = in_session.getAttribute(name);
                if (!(obj instanceof Locale)) continue;
                localeArray.add(obj);
            }
            if (localeArray.size() == 1) {
                locale = (Locale)localeArray.get(0);
            }
            return locale;
        }
        catch (IllegalStateException ise) {
            return null;
        }
    }

    public static Object guessUserFromSession(Session in_session) {
        if (null == in_session) {
            return null;
        }
        if (in_session.getPrincipal() != null) {
            return in_session.getPrincipal().getName();
        }
        HttpSession httpSession = in_session.getSession();
        if (httpSession == null) {
            return null;
        }
        try {
            Object user = null;
            for (String userTestAttribute : USER_TEST_ATTRIBUTES) {
                Object obj = httpSession.getAttribute(userTestAttribute);
                if (null != obj) {
                    user = obj;
                    break;
                }
                obj = httpSession.getAttribute(userTestAttribute.toLowerCase(Locale.ENGLISH));
                if (null != obj) {
                    user = obj;
                    break;
                }
                obj = httpSession.getAttribute(userTestAttribute.toUpperCase(Locale.ENGLISH));
                if (null == obj) continue;
                user = obj;
                break;
            }
            if (null != user) {
                return user;
            }
            ArrayList<Object> principalArray = new ArrayList<Object>();
            Enumeration enumeration = httpSession.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                String name = (String)enumeration.nextElement();
                Object obj = httpSession.getAttribute(name);
                if (!(obj instanceof Principal) && !(obj instanceof Subject)) continue;
                principalArray.add(obj);
            }
            if (principalArray.size() == 1) {
                user = principalArray.get(0);
            }
            if (null != user) {
                return user;
            }
            return user;
        }
        catch (IllegalStateException ise) {
            return null;
        }
    }

    public static long getUsedTimeForSession(Session in_session) {
        try {
            long diffMilliSeconds = in_session.getThisAccessedTime() - in_session.getCreationTime();
            return diffMilliSeconds;
        }
        catch (IllegalStateException ise) {
            return -1L;
        }
    }

    public static long getTTLForSession(Session in_session) {
        try {
            long diffMilliSeconds = (long)(1000 * in_session.getMaxInactiveInterval()) - (System.currentTimeMillis() - in_session.getThisAccessedTime());
            return diffMilliSeconds;
        }
        catch (IllegalStateException ise) {
            return -1L;
        }
    }

    public static long getInactiveTimeForSession(Session in_session) {
        try {
            long diffMilliSeconds = System.currentTimeMillis() - in_session.getThisAccessedTime();
            return diffMilliSeconds;
        }
        catch (IllegalStateException ise) {
            return -1L;
        }
    }
}

