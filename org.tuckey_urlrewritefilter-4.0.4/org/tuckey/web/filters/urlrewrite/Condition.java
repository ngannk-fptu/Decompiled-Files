/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.File;
import java.util.Calendar;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tuckey.web.filters.urlrewrite.ConditionMatch;
import org.tuckey.web.filters.urlrewrite.RuleBase;
import org.tuckey.web.filters.urlrewrite.TypeConverter;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.NumberUtils;
import org.tuckey.web.filters.urlrewrite.utils.RegexPattern;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingPattern;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingPatternSyntaxException;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;
import org.tuckey.web.filters.urlrewrite.utils.WildcardPattern;

public class Condition
extends TypeConverter {
    private static Log log = Log.getLog(Condition.class);
    private boolean caseSensitive = false;
    private int id = 0;
    private StringMatchingPattern pattern;
    private String name;
    private short operator;
    private String strValue;
    private long numericValue = 0L;
    private boolean processNextOr = false;
    private boolean valid = false;
    private boolean initialised = false;
    private static final short OPERATOR_EQUAL = 1;
    private static final short OPERATOR_NOT_EQUAL = 2;
    private static final short OPERATOR_GREATER_THAN = 3;
    private static final short OPERATOR_LESS_THAN = 4;
    private static final short OPERATOR_GREATER_THAN_OR_EQUAL = 5;
    private static final short OPERATOR_LESS_THAN_OR_EQUAL = 6;
    private static final short OPERATOR_INSTANCEOF = 7;
    private static final short OPERATOR_IS_DIR = 8;
    private static final short OPERATOR_IS_FILE = 9;
    private static final short OPERATOR_IS_FILE_WITH_SIZE = 10;
    private static final short OPERATOR_NOT_DIR = 11;
    private static final short OPERATOR_NOT_FILE = 12;
    private static final short OPERATOR_NOT_FILE_WITH_SIZE = 13;
    Class instanceOfClass = null;
    private RuleBase rule;

    public boolean matches(HttpServletRequest hsRequest) {
        return this.getConditionMatch(hsRequest) != null;
    }

    public ConditionMatch getConditionMatch(HttpServletRequest hsRequest) {
        if (!this.initialised) {
            log.debug("condition not initialised skipping");
            return null;
        }
        if (!this.valid) {
            log.debug("condition not valid skipping");
            return null;
        }
        switch (this.type) {
            case 4: {
                return this.evaluateNumericCondition(System.currentTimeMillis());
            }
            case 5: {
                return this.evaluateCalendarCondition(1);
            }
            case 6: {
                return this.evaluateCalendarCondition(2);
            }
            case 7: {
                return this.evaluateCalendarCondition(5);
            }
            case 8: {
                return this.evaluateCalendarCondition(7);
            }
            case 9: {
                return this.evaluateCalendarCondition(9);
            }
            case 10: {
                return this.evaluateCalendarCondition(11);
            }
            case 11: {
                return this.evaluateCalendarCondition(12);
            }
            case 12: {
                return this.evaluateCalendarCondition(13);
            }
            case 13: {
                return this.evaluateCalendarCondition(14);
            }
            case 14: {
                return this.evaluateAttributeCondition(this.name == null ? null : hsRequest.getAttribute(this.name));
            }
            case 15: {
                return this.evaluateStringCondition(hsRequest.getAuthType());
            }
            case 16: {
                return this.evaluateStringCondition(hsRequest.getCharacterEncoding());
            }
            case 17: {
                return this.evaluateNumericCondition(hsRequest.getContentLength());
            }
            case 18: {
                return this.evaluateStringCondition(hsRequest.getContentType());
            }
            case 19: {
                return this.evaluateStringCondition(hsRequest.getContextPath());
            }
            case 20: {
                return this.evaluateCookieCondition(hsRequest.getCookies(), this.name);
            }
            case 39: {
                return this.evaluateNumericCondition(hsRequest.getLocalPort());
            }
            case 21: {
                return this.evaluateStringCondition(hsRequest.getMethod());
            }
            case 22: {
                return this.evaluateStringCondition(this.name == null ? null : hsRequest.getParameter(this.name));
            }
            case 23: {
                return this.evaluateStringCondition(hsRequest.getPathInfo());
            }
            case 24: {
                return this.evaluateStringCondition(hsRequest.getPathTranslated());
            }
            case 25: {
                return this.evaluateStringCondition(hsRequest.getProtocol());
            }
            case 26: {
                return this.evaluateStringCondition(hsRequest.getQueryString());
            }
            case 27: {
                return this.evaluateStringCondition(hsRequest.getRemoteAddr());
            }
            case 28: {
                return this.evaluateStringCondition(hsRequest.getRemoteHost());
            }
            case 29: {
                return this.evaluateStringCondition(hsRequest.getRemoteUser());
            }
            case 30: {
                return this.evaluateStringCondition(hsRequest.getRequestedSessionId());
            }
            case 41: {
                return this.evaluateBoolCondition(hsRequest.isRequestedSessionIdFromCookie());
            }
            case 42: {
                return this.evaluateBoolCondition(hsRequest.isRequestedSessionIdFromURL());
            }
            case 43: {
                return this.evaluateBoolCondition(hsRequest.isRequestedSessionIdValid());
            }
            case 31: {
                return this.evaluateStringCondition(hsRequest.getRequestURI());
            }
            case 32: {
                StringBuffer requestUrlBuff = hsRequest.getRequestURL();
                String requestUrlStr = null;
                if (requestUrlBuff != null) {
                    requestUrlStr = requestUrlBuff.toString();
                }
                return this.evaluateStringCondition(requestUrlStr);
            }
            case 33: {
                Object sessionAttributeValue = null;
                HttpSession session = hsRequest.getSession(false);
                if (session != null && this.name != null) {
                    sessionAttributeValue = session.getAttribute(this.name);
                }
                return this.evaluateAttributeCondition(sessionAttributeValue);
            }
            case 34: {
                boolean sessionNew = false;
                HttpSession sessionIsNew = hsRequest.getSession(false);
                if (sessionIsNew != null) {
                    sessionNew = sessionIsNew.isNew();
                }
                return this.evaluateBoolCondition(sessionNew);
            }
            case 35: {
                return this.evaluateNumericCondition(hsRequest.getServerPort());
            }
            case 36: {
                return this.evaluateStringCondition(hsRequest.getServerName());
            }
            case 37: {
                return this.evaluateStringCondition(hsRequest.getScheme());
            }
            case 38: {
                log.debug("is user in role " + this.name + " op " + this.operator);
                return this.evaluateBoolCondition(hsRequest.isUserInRole(this.name));
            }
            case 40: {
                String eName = null;
                Exception e = (Exception)hsRequest.getAttribute("javax.servlet.error.exception");
                if (7 == this.operator) {
                    return this.evaluateInstanceOfCondition(e);
                }
                if (e != null && e.getClass() != null) {
                    eName = e.getClass().getName();
                }
                return this.evaluateStringCondition(eName);
            }
            case 44: {
                if (this.rule.getServletContext() != null) {
                    String fileName = this.rule.getServletContext().getRealPath(hsRequest.getRequestURI());
                    if (log.isDebugEnabled()) {
                        log.debug("fileName found is " + fileName);
                    }
                    return this.evaluateStringCondition(fileName);
                }
                log.error("unable to get servlet context for filename lookup, skipping");
                return null;
            }
        }
        return this.evaluateHeaderCondition(hsRequest);
    }

    private ConditionMatch evaluateAttributeCondition(Object attribObject) {
        String attribValue = null;
        if (attribObject == null) {
            if (log.isDebugEnabled()) {
                log.debug(this.name + " doesn't exist");
            }
        } else {
            attribValue = attribObject.toString();
        }
        if (7 == this.operator) {
            return this.evaluateInstanceOfCondition(attribObject);
        }
        return this.evaluateStringCondition(attribValue);
    }

    private ConditionMatch evaluateInstanceOfCondition(Object obj) {
        if (obj == null) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("is " + obj.getClass() + " an instanceof " + this.instanceOfClass);
        }
        if (this.instanceOfClass == null) {
            log.error("this condition may have failed to initialise correctly, instanceof class is null");
            return null;
        }
        if (this.instanceOfClass.isInstance(obj)) {
            log.debug("yes");
            return new ConditionMatch();
        }
        log.debug("no");
        return null;
    }

    private ConditionMatch evaluateCookieCondition(Cookie[] cookies, String name) {
        if (cookies == null) {
            return this.evaluateBoolCondition(false);
        }
        if (name == null) {
            return this.evaluateBoolCondition(false);
        }
        for (int i = 0; i < cookies.length; ++i) {
            Cookie cookie = cookies[i];
            if (cookie == null || !name.equals(cookie.getName())) continue;
            return this.evaluateStringCondition(cookie.getValue());
        }
        return this.evaluateBoolCondition(false);
    }

    private ConditionMatch evaluateStringCondition(String value) {
        if (this.pattern == null && value == null) {
            log.debug("value is empty and pattern is also, condition false");
            return this.evaluateBoolCondition(false);
        }
        if (this.operator == 8) {
            if (log.isDebugEnabled()) {
                log.debug("checking to see if " + value + " is a directory");
            }
            File fileToCheck = new File(value);
            return this.evaluateBoolCondition(fileToCheck.isDirectory());
        }
        if (this.operator == 9) {
            if (log.isDebugEnabled()) {
                log.debug("checking to see if " + value + " is a file");
            }
            File fileToCheck = new File(value);
            return this.evaluateBoolCondition(fileToCheck.isFile());
        }
        if (this.operator == 10) {
            File fileToCheck;
            if (log.isDebugEnabled()) {
                log.debug("checking to see if " + value + " is a file with size");
            }
            return this.evaluateBoolCondition((fileToCheck = new File(value)).isFile() && fileToCheck.length() > 0L);
        }
        if (this.operator == 11) {
            File fileToCheck;
            if (log.isDebugEnabled()) {
                log.debug("checking to see if " + value + " is not a directory");
            }
            return this.evaluateBoolCondition(!(fileToCheck = new File(value)).isDirectory());
        }
        if (this.operator == 12) {
            File fileToCheck;
            if (log.isDebugEnabled()) {
                log.debug("checking to see if " + value + " is not a file");
            }
            return this.evaluateBoolCondition(!(fileToCheck = new File(value)).isFile());
        }
        if (this.operator == 13) {
            File fileToCheck;
            if (log.isDebugEnabled()) {
                log.debug("checking to see if " + value + " is not a file with size");
            }
            return this.evaluateBoolCondition(!(fileToCheck = new File(value)).isFile() || fileToCheck.length() <= 0L);
        }
        if (this.pattern == null) {
            log.debug("value isn't empty but pattern is, assuming checking for existence, condition true");
            return this.evaluateBoolCondition(true);
        }
        if (value == null) {
            value = "";
        }
        if (log.isDebugEnabled()) {
            log.debug("evaluating \"" + value + "\" against " + this.strValue);
        }
        StringMatchingMatcher matcher = this.pattern.matcher(value);
        return this.evaluateBoolCondition(matcher, matcher.find());
    }

    private ConditionMatch evaluateBoolCondition(boolean outcome) {
        if (log.isTraceEnabled()) {
            log.trace("outcome " + outcome);
        }
        if (this.operator == 2) {
            log.debug("not equal operator in use");
            return !outcome ? new ConditionMatch() : null;
        }
        return outcome ? new ConditionMatch() : null;
    }

    private ConditionMatch evaluateBoolCondition(StringMatchingMatcher matcher, boolean outcome) {
        ConditionMatch conditionMatch = this.evaluateBoolCondition(outcome);
        if (conditionMatch != null) {
            conditionMatch.setMatcher(matcher);
        }
        return conditionMatch;
    }

    private ConditionMatch evaluateHeaderCondition(HttpServletRequest hsRequest) {
        String headerValue = null;
        if (this.name != null) {
            headerValue = hsRequest.getHeader(this.name);
        }
        return this.evaluateStringCondition(headerValue);
    }

    private ConditionMatch evaluateCalendarCondition(int calField) {
        return this.evaluateNumericCondition(Calendar.getInstance().get(calField));
    }

    private ConditionMatch evaluateNumericCondition(long compareWith) {
        if (log.isDebugEnabled()) {
            log.debug("evaluating with operator, is " + compareWith + " " + this.getOperator() + " " + this.numericValue);
        }
        switch (this.operator) {
            case 2: {
                return compareWith != this.numericValue ? new ConditionMatch() : null;
            }
            case 3: {
                return compareWith > this.numericValue ? new ConditionMatch() : null;
            }
            case 4: {
                return compareWith < this.numericValue ? new ConditionMatch() : null;
            }
            case 5: {
                return compareWith >= this.numericValue ? new ConditionMatch() : null;
            }
            case 6: {
                return compareWith <= this.numericValue ? new ConditionMatch() : null;
            }
        }
        return compareWith == this.numericValue ? new ConditionMatch() : null;
    }

    public boolean initialise() {
        this.initialised = true;
        if (this.error != null) {
            return false;
        }
        if (this.type == 0) {
            this.type = 1;
        }
        switch (this.type) {
            case 35: {
                this.initNumericValue();
                break;
            }
            case 4: {
                this.initNumericValue();
                break;
            }
            case 5: {
                this.initNumericValue();
                break;
            }
            case 6: {
                this.initNumericValue();
                break;
            }
            case 7: {
                this.initNumericValue();
                break;
            }
            case 8: {
                this.initNumericValue();
                break;
            }
            case 9: {
                this.initNumericValue();
                break;
            }
            case 10: {
                this.initNumericValue();
                break;
            }
            case 11: {
                this.initNumericValue();
                break;
            }
            case 12: {
                this.initNumericValue();
                break;
            }
            case 13: {
                this.initNumericValue();
                break;
            }
            case 17: {
                this.initNumericValue();
                break;
            }
            case 39: {
                this.initNumericValue();
                break;
            }
            case 38: {
                if (!StringUtils.isBlank(this.name)) break;
                this.name = this.strValue;
                break;
            }
            case 33: {
                if (StringUtils.isBlank(this.name)) {
                    this.setError("you must set a name for session attributes");
                }
                this.initStringValue();
                break;
            }
            case 14: {
                if (StringUtils.isBlank(this.name)) {
                    this.setError("you must set a name for attributes");
                }
                this.initStringValue();
                break;
            }
            case 1: {
                if (StringUtils.isBlank(this.name)) {
                    this.setError("you must set a name for a header");
                }
                this.initStringValue();
                break;
            }
            default: {
                this.initStringValue();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("loaded condition " + this.getType() + " " + this.name + " " + this.strValue);
        }
        this.valid = this.error == null;
        return this.valid;
    }

    private void initStringValue() {
        if (StringUtils.isBlank(this.strValue)) {
            log.debug("value is blank initing pattern to null");
            this.pattern = null;
            return;
        }
        if (7 == this.operator) {
            log.debug("initialising instanceof condition");
            this.strValue = StringUtils.trim(this.strValue);
            try {
                this.instanceOfClass = Class.forName(this.strValue);
            }
            catch (ClassNotFoundException e) {
                this.setError("could not find " + this.strValue + " got a " + e.toString());
            }
            catch (NoClassDefFoundError e) {
                this.setError("could not find " + this.strValue + " got a " + e.toString());
            }
        } else {
            try {
                if (this.rule != null && this.rule.isMatchTypeWildcard()) {
                    log.debug("rule match type is wildcard");
                    this.pattern = new WildcardPattern(this.strValue);
                } else {
                    this.pattern = new RegexPattern(this.strValue, this.caseSensitive);
                }
            }
            catch (StringMatchingPatternSyntaxException e) {
                this.setError("Problem compiling regular expression " + this.strValue + " (" + e.getMessage() + ")");
            }
        }
    }

    private void initNumericValue() {
        if (this.numericValue == 0L) {
            this.numericValue = NumberUtils.stringToLong(StringUtils.trim(this.strValue));
            if (this.numericValue == 0L && !"0".equals(this.strValue)) {
                this.setError("Value " + this.strValue + " is not a valid number (tried to cast to java type long)");
            }
        }
    }

    protected void setError(String s) {
        super.setError(s);
        log.error("Condition " + this.id + " had error: " + s);
    }

    public String getOperator() {
        switch (this.operator) {
            case 2: {
                return "notequal";
            }
            case 3: {
                return "greater";
            }
            case 4: {
                return "less";
            }
            case 5: {
                return "greaterorequal";
            }
            case 6: {
                return "lessorequal";
            }
            case 7: {
                return "instanceof";
            }
            case 1: {
                return "equal";
            }
            case 8: {
                return "isdir";
            }
            case 9: {
                return "isfile";
            }
            case 10: {
                return "isfilewithsize";
            }
            case 11: {
                return "notdir";
            }
            case 12: {
                return "notfile";
            }
            case 13: {
                return "notfilewithsize";
            }
        }
        return "";
    }

    public void setOperator(String operator) {
        if ("notequal".equals(operator)) {
            this.operator = (short)2;
        } else if ("greater".equals(operator)) {
            this.operator = (short)3;
        } else if ("less".equals(operator)) {
            this.operator = (short)4;
        } else if ("greaterorequal".equals(operator)) {
            this.operator = (short)5;
        } else if ("lessorequal".equals(operator)) {
            this.operator = (short)6;
        } else if ("instanceof".equals(operator)) {
            this.operator = (short)7;
        } else if ("equal".equals(operator) || StringUtils.isBlank(operator)) {
            this.operator = 1;
        } else if ("isdir".equals(operator)) {
            this.operator = (short)8;
        } else if ("isfile".equals(operator)) {
            this.operator = (short)9;
        } else if ("isfilewithsize".equals(operator)) {
            this.operator = (short)10;
        } else if ("notdir".equals(operator)) {
            this.operator = (short)11;
        } else if ("notfile".equals(operator)) {
            this.operator = (short)12;
        } else if ("notfilewithsize".equals(operator)) {
            this.operator = (short)13;
        } else {
            this.setError("Operator " + operator + " is not valid");
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNext() {
        if (this.processNextOr) {
            return "or";
        }
        return "and";
    }

    public void setNext(String next) {
        if ("or".equals(next)) {
            this.processNextOr = true;
        } else if ("and".equals(next) || StringUtils.isBlank(next)) {
            this.processNextOr = false;
        } else {
            this.setError("Next " + next + " is not valid (can be 'and', 'or')");
        }
    }

    public String getValue() {
        return this.strValue;
    }

    public void setValue(String value) {
        this.strValue = value;
    }

    public boolean isProcessNextOr() {
        return this.processNextOr;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public String getDisplayName() {
        return "Condtition " + this.id;
    }

    public void setRule(RuleBase rule) {
        this.rule = rule;
    }
}

