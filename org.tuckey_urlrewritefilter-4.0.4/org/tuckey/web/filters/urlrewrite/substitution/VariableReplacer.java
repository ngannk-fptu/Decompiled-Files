/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tuckey.web.filters.urlrewrite.TypeConverter;
import org.tuckey.web.filters.urlrewrite.substitution.ChainedSubstitutionFilters;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilter;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

public class VariableReplacer
implements SubstitutionFilter {
    private static Log log = Log.getLog(VariableReplacer.class);
    private static Pattern toVariablePattern = Pattern.compile("(?<!\\\\)%\\{([-a-zA-Z:]*)\\}");
    private static ServletContext servletContext;

    public static boolean containsVariable(String to) {
        Matcher variableMatcher = toVariablePattern.matcher(to);
        return variableMatcher.find();
    }

    public VariableReplacer() {
    }

    public VariableReplacer(ServletContext sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Servlet context is null");
        }
        servletContext = sc;
    }

    public static String replace(String subjectOfReplacement, HttpServletRequest hsRequest) {
        return new VariableReplacer().substitute(subjectOfReplacement, new SubstitutionContext(hsRequest, null, null, null), new ChainedSubstitutionFilters(Collections.EMPTY_LIST));
    }

    public static String replaceWithServletContext(String subjectOfReplacement, HttpServletRequest hsRequest, ServletContext sc) {
        return new VariableReplacer(sc).substitute(subjectOfReplacement, new SubstitutionContext(hsRequest, null, null, null), new ChainedSubstitutionFilters(Collections.EMPTY_LIST));
    }

    public String substitute(String subjectOfReplacement, SubstitutionContext ctx, SubstitutionFilterChain nextFilter) {
        Matcher varMatcher = toVariablePattern.matcher(subjectOfReplacement);
        StringBuffer sb = new StringBuffer();
        boolean anyMatches = false;
        int lastAppendPosition = 0;
        while (varMatcher.find()) {
            anyMatches = true;
            int groupCount = varMatcher.groupCount();
            if (groupCount < 1) {
                log.error("group count on backref finder regex is not as expected");
                if (!log.isDebugEnabled()) continue;
                log.error("varMatcher: " + varMatcher.toString());
                continue;
            }
            String varStr = varMatcher.group(1);
            String varValue = "";
            if (varStr != null) {
                varValue = VariableReplacer.varReplace(varStr, ctx.getHsRequest());
                if (log.isDebugEnabled()) {
                    log.debug("resolved to: " + varValue);
                }
            } else if (log.isDebugEnabled()) {
                log.debug("variable reference is null " + varMatcher);
            }
            String stringBeforeMatch = subjectOfReplacement.substring(lastAppendPosition, varMatcher.start());
            sb.append(nextFilter.substitute(stringBeforeMatch, ctx));
            sb.append(varValue);
            lastAppendPosition = varMatcher.end();
        }
        if (anyMatches) {
            String stringAfterMatch = subjectOfReplacement.substring(lastAppendPosition);
            sb.append(nextFilter.substitute(stringAfterMatch, ctx));
            log.debug("replaced sb is " + sb);
            return sb.toString();
        }
        return nextFilter.substitute(subjectOfReplacement, ctx);
    }

    private static String varReplace(String originalVarStr, HttpServletRequest hsRequest) {
        String varType;
        String varSubName = null;
        int colonIdx = originalVarStr.indexOf(":");
        if (colonIdx != -1 && colonIdx + 1 < originalVarStr.length()) {
            varSubName = originalVarStr.substring(colonIdx + 1);
            varType = originalVarStr.substring(0, colonIdx);
            if (log.isDebugEnabled()) {
                log.debug("variable %{" + originalVarStr + "} type: " + varType + ", name: '" + varSubName + "'");
            }
        } else {
            varType = originalVarStr;
            if (log.isDebugEnabled()) {
                log.debug("variable %{" + originalVarStr + "} type: " + varType);
            }
        }
        TypeConverter type = new TypeConverter();
        type.setType(varType);
        switch (type.getTypeShort()) {
            case 4: {
                return String.valueOf(System.currentTimeMillis());
            }
            case 5: {
                return VariableReplacer.calendarVariable(1);
            }
            case 6: {
                return VariableReplacer.calendarVariable(2);
            }
            case 7: {
                return VariableReplacer.calendarVariable(5);
            }
            case 8: {
                return VariableReplacer.calendarVariable(7);
            }
            case 9: {
                return VariableReplacer.calendarVariable(9);
            }
            case 10: {
                return VariableReplacer.calendarVariable(11);
            }
            case 11: {
                return VariableReplacer.calendarVariable(12);
            }
            case 12: {
                return VariableReplacer.calendarVariable(13);
            }
            case 13: {
                return VariableReplacer.calendarVariable(14);
            }
            case 14: {
                return VariableReplacer.attributeVariable(varSubName == null ? null : hsRequest.getAttribute(varSubName), varSubName);
            }
            case 15: {
                return StringUtils.notNull(hsRequest.getAuthType());
            }
            case 16: {
                return StringUtils.notNull(hsRequest.getCharacterEncoding());
            }
            case 17: {
                return String.valueOf(hsRequest.getContentLength());
            }
            case 18: {
                return StringUtils.notNull(hsRequest.getContentType());
            }
            case 19: {
                return StringUtils.notNull(hsRequest.getContextPath());
            }
            case 20: {
                return VariableReplacer.cookieVariable(hsRequest.getCookies(), varSubName);
            }
            case 39: {
                return String.valueOf(hsRequest.getLocalPort());
            }
            case 21: {
                return StringUtils.notNull(hsRequest.getMethod());
            }
            case 22: {
                return StringUtils.notNull(varSubName == null ? null : hsRequest.getParameter(varSubName));
            }
            case 23: {
                return StringUtils.notNull(hsRequest.getPathInfo());
            }
            case 24: {
                return StringUtils.notNull(hsRequest.getPathTranslated());
            }
            case 25: {
                return StringUtils.notNull(hsRequest.getProtocol());
            }
            case 26: {
                return StringUtils.notNull(hsRequest.getQueryString());
            }
            case 27: {
                return StringUtils.notNull(hsRequest.getRemoteAddr());
            }
            case 28: {
                return StringUtils.notNull(hsRequest.getRemoteHost());
            }
            case 29: {
                return StringUtils.notNull(hsRequest.getRemoteUser());
            }
            case 30: {
                return StringUtils.notNull(hsRequest.getRequestedSessionId());
            }
            case 31: {
                return StringUtils.notNull(hsRequest.getRequestURI());
            }
            case 32: {
                StringBuffer requestUrlBuff = hsRequest.getRequestURL();
                String requestUrlStr = null;
                if (requestUrlBuff != null) {
                    requestUrlStr = requestUrlBuff.toString();
                }
                return StringUtils.notNull(requestUrlStr);
            }
            case 33: {
                Object sessionAttributeValue = null;
                HttpSession session = hsRequest.getSession(false);
                if (session != null && varSubName != null) {
                    sessionAttributeValue = session.getAttribute(varSubName);
                }
                return VariableReplacer.attributeVariable(sessionAttributeValue, varSubName);
            }
            case 34: {
                boolean sessionNew = false;
                HttpSession sessionIsNew = hsRequest.getSession(false);
                if (sessionIsNew != null) {
                    sessionNew = sessionIsNew.isNew();
                }
                return String.valueOf(sessionNew);
            }
            case 35: {
                return String.valueOf(hsRequest.getServerPort());
            }
            case 36: {
                return StringUtils.notNull(hsRequest.getServerName());
            }
            case 37: {
                return StringUtils.notNull(hsRequest.getScheme());
            }
            case 38: {
                return String.valueOf(hsRequest.isUserInRole(varSubName));
            }
            case 40: {
                Exception e = (Exception)hsRequest.getAttribute("javax.servlet.error.exception");
                if (e == null) {
                    return "";
                }
                return e.getClass().getName();
            }
            case 1: {
                return StringUtils.notNull(hsRequest.getHeader(varSubName));
            }
            case 45: {
                Object attr = servletContext.getAttribute(varSubName);
                if (attr == null) {
                    log.debug("No context attribute " + varSubName + ", must be an init-param");
                    return servletContext.getInitParameter(varSubName);
                }
                return StringUtils.notNull(attr.toString());
            }
        }
        log.error("variable %{" + originalVarStr + "} type '" + varType + "' not a valid type");
        return "";
    }

    private static String attributeVariable(Object attribObject, String name) {
        String attribValue = null;
        if (attribObject == null) {
            if (log.isDebugEnabled()) {
                log.debug(name + " doesn't exist");
            }
        } else {
            attribValue = attribObject.toString();
        }
        return StringUtils.notNull(attribValue);
    }

    private static String cookieVariable(Cookie[] cookies, String name) {
        if (cookies == null) {
            return "";
        }
        if (name == null) {
            return "";
        }
        for (int i = 0; i < cookies.length; ++i) {
            Cookie cookie = cookies[i];
            if (cookie == null || !name.equals(cookie.getName())) continue;
            return StringUtils.notNull(cookie.getValue());
        }
        return null;
    }

    private static String calendarVariable(int calField) {
        return String.valueOf(Calendar.getInstance().get(calField));
    }
}

