/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.tuckey.web.filters.urlrewrite;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.tuckey.web.filters.urlrewrite.ConditionMatch;
import org.tuckey.web.filters.urlrewrite.UrlRewriteWrappedResponse;
import org.tuckey.web.filters.urlrewrite.substitution.BackReferenceReplacer;
import org.tuckey.web.filters.urlrewrite.substitution.ChainedSubstitutionFilters;
import org.tuckey.web.filters.urlrewrite.substitution.FunctionReplacer;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;
import org.tuckey.web.filters.urlrewrite.substitution.VariableReplacer;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.NumberUtils;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

public class SetAttribute {
    private static Log log = Log.getLog(SetAttribute.class);
    private boolean initialised = false;
    private boolean valid = false;
    private String error = null;
    private short type;
    private String name;
    private String value;
    private int numericValue;
    private Locale locale;
    private static final short SET_TYPE_REQUEST = 0;
    private static final short SET_TYPE_SESSION = 1;
    private static final short SET_TYPE_RESPONSE_HEADER = 2;
    private static final short SET_TYPE_COOKIE = 3;
    private static final short SET_TYPE_CONTENT_TYPE = 4;
    private static final short SET_TYPE_CHARSET = 5;
    private static final short SET_TYPE_LOCALE = 6;
    private static final short SET_TYPE_STAUS = 7;
    private static final short SET_TYPE_PARAM = 8;
    private static final short SET_TYPE_EXPIRES = 9;
    private static final short SET_TYPE_METHOD = 10;
    private long expiresValueAdd = 0L;
    private boolean valueContainsVariable = false;
    private boolean valueContainsBackRef = false;
    private boolean valueContainsFunction = false;

    public String getType() {
        if (this.type == 2) {
            return "response-header";
        }
        if (this.type == 1) {
            return "session";
        }
        if (this.type == 3) {
            return "cookie";
        }
        if (this.type == 4) {
            return "content-type";
        }
        if (this.type == 5) {
            return "charset";
        }
        if (this.type == 6) {
            return "locale";
        }
        if (this.type == 7) {
            return "status";
        }
        if (this.type == 8) {
            return "parameter";
        }
        if (this.type == 9) {
            return "expires";
        }
        if (this.type == 10) {
            return "method";
        }
        return "request";
    }

    public void setType(String typeStr) {
        if ("response-header".equals(typeStr)) {
            this.type = (short)2;
        } else if ("session".equals(typeStr)) {
            this.type = 1;
        } else if ("cookie".equals(typeStr)) {
            this.type = (short)3;
        } else if ("content-type".equals(typeStr)) {
            this.type = (short)4;
        } else if ("charset".equals(typeStr)) {
            this.type = (short)5;
        } else if ("locale".equals(typeStr)) {
            this.type = (short)6;
        } else if ("status".equals(typeStr)) {
            this.type = (short)7;
        } else if ("parameter".equals(typeStr) || "param".equals(typeStr)) {
            this.type = (short)8;
        } else if ("expires".equals(typeStr)) {
            this.type = (short)9;
        } else if ("request".equals(typeStr) || StringUtils.isBlank(typeStr)) {
            this.type = 0;
        } else if ("method".equals(typeStr)) {
            this.type = (short)10;
        } else {
            this.setError("type (" + typeStr + ") is not valid");
        }
    }

    private void setError(String s) {
        log.error("set " + this.getDisplayName() + " had error: " + s);
        this.error = s;
    }

    public String getError() {
        return this.error;
    }

    public String getDisplayName() {
        return "Set " + this.getType() + " " + this.name + " " + this.value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void execute(ConditionMatch lastConditionMatch, StringMatchingMatcher toMatcher, HttpServletRequest hsRequest, HttpServletResponse hsResponse) {
        if (log.isDebugEnabled()) {
            log.debug("set " + this.getDisplayName() + " called");
        }
        if (!this.valid) {
            log.debug("not valid, skipping");
            return;
        }
        if (!this.initialised) {
            log.debug("not initialised, skipping");
            return;
        }
        String value = this.value;
        SubstitutionContext substitutionContext = new SubstitutionContext(hsRequest, toMatcher, lastConditionMatch, null);
        SubstitutionFilterChain substitutionFilter = ChainedSubstitutionFilters.getDefaultSubstitutionChain(false, this.valueContainsFunction, this.valueContainsVariable, this.valueContainsBackRef);
        value = substitutionFilter.substitute(value, substitutionContext);
        if (this.type == 0) {
            log.debug("setting request attrib");
            hsRequest.setAttribute(this.name, (Object)value);
        } else if (this.type == 10) {
            log.debug("setting request method");
            if (hsResponse instanceof UrlRewriteWrappedResponse) {
                ((UrlRewriteWrappedResponse)hsResponse).setOverridenMethod(value);
            } else {
                log.warn("unable to set request method as request not a UrlRewriteWrappedResponse");
            }
        } else if (this.type == 8) {
            log.debug("setting request parameter");
            if (hsResponse instanceof UrlRewriteWrappedResponse) {
                ((UrlRewriteWrappedResponse)hsResponse).addOverridenRequestParameter(this.name, value);
            } else {
                log.warn("unable to set request parameter as request not a UrlRewriteWrappedResponse");
            }
        } else if (this.type == 1) {
            log.debug("setting session attrib");
            HttpSession session = hsRequest.getSession(true);
            if (session == null) {
                log.warn("could not create a new session for a request");
            } else {
                session.setAttribute(this.name, (Object)value);
            }
        } else if (this.type == 2) {
            log.debug("setting response header");
            hsResponse.addHeader(this.name, value);
        } else if (this.type == 7) {
            log.debug("setting status");
            hsResponse.setStatus(this.numericValue);
        } else if (this.type == 3) {
            Cookie cookieToAdd = this.getCookie(this.name, value);
            if (cookieToAdd != null) {
                log.debug("adding cookie");
                hsResponse.addCookie(cookieToAdd);
            }
        } else if (this.type == 4) {
            log.debug("setting content type");
            hsResponse.setContentType(value);
        } else if (this.type == 5) {
            log.debug("setting charset");
            hsResponse.setCharacterEncoding(value);
        } else if (this.type == 6) {
            log.debug("setting charset");
            hsResponse.setLocale(this.locale);
        } else if (this.type == 9) {
            log.debug("setting expires");
            hsResponse.setDateHeader("Expires", System.currentTimeMillis() + this.expiresValueAdd);
        } else {
            log.warn("unknown type" + this.type);
        }
    }

    public boolean initialise() {
        this.initialised = true;
        if (this.value != null) {
            if (BackReferenceReplacer.containsBackRef(this.value)) {
                this.valueContainsBackRef = true;
            }
            if (VariableReplacer.containsVariable(this.value)) {
                this.valueContainsVariable = true;
            }
            if (FunctionReplacer.containsFunction(this.value)) {
                this.valueContainsFunction = true;
            }
        }
        if (this.type == 7) {
            this.initNumericValue();
        } else if (this.type == 6) {
            this.locale = null;
            if (this.value == null) {
                this.setError("Locale is not valid because value is null");
            } else if (this.value.matches("[a-zA-Z][a-zA-Z]")) {
                this.locale = new Locale(this.value);
            } else if (this.value.matches("[a-zA-Z][a-zA-Z]-[a-zA-Z][a-zA-Z]")) {
                this.locale = new Locale(this.value.substring(1, 2), this.value.substring(2, 4));
            } else if (this.value.matches("[a-zA-Z][a-zA-Z]-[a-zA-Z][a-zA-Z]-.*")) {
                this.locale = new Locale(this.value.substring(1, 2), this.value.substring(4, 5), this.value.substring(6, this.value.length()));
            } else {
                this.setError("Locale " + this.value + " is not valid (valid locales are, zh, zh-CN, zh-CN-rural)");
            }
        } else if (this.type == 3) {
            if (this.value != null && this.name != null) {
                this.getCookie(this.name, this.value);
            } else {
                this.setError("cookie must have a name and a value");
            }
        } else if (this.type == 9) {
            if (this.value != null) {
                this.expiresValueAdd = this.parseTimeValue(this.value);
            } else {
                this.setError("expires must have a value");
            }
        }
        if (this.error == null) {
            this.valid = true;
        }
        return this.valid;
    }

    protected long parseTimeValue(String parsingValue) {
        long calculatedMillis = 0L;
        if (parsingValue.startsWith("access")) {
            parsingValue = parsingValue.substring("access".length()).trim();
        }
        if (parsingValue.startsWith("plus")) {
            parsingValue = parsingValue.substring("plus".length()).trim();
        }
        log.debug("calculating expires ms based on '" + parsingValue + "'");
        Matcher matcher = Pattern.compile("([0-9]+)\\s+(\\w+)").matcher(parsingValue);
        while (matcher.find()) {
            long num = NumberUtils.stringToInt(matcher.group(1), -1);
            if (num < 0L) {
                this.setError("could not calculate numeric value of " + matcher.group(1));
            }
            String part = matcher.group(2);
            log.debug("adding '" + num + "' '" + part + "'");
            long addThisRound = 0L;
            if (part.matches("year[s]?")) {
                addThisRound = num * Math.round(3.15576E10);
            }
            if (part.matches("month[s]?")) {
                addThisRound = num * Math.round(2.6298E9);
            }
            if (part.matches("week[s]?")) {
                addThisRound = num * 604800000L;
            }
            if (part.matches("day[s]?")) {
                addThisRound = num * 86400000L;
            }
            if (part.matches("hour[s]?")) {
                addThisRound = num * 3600000L;
            }
            if (part.matches("minute[s]?")) {
                addThisRound = num * 60000L;
            }
            if (part.matches("second[s]?")) {
                addThisRound = num * 1000L;
            }
            if (addThisRound == 0L) {
                this.setError("unkown time unit '" + part + "'");
            }
            calculatedMillis += addThisRound;
        }
        if (calculatedMillis == 0L) {
            this.setError("could not calculate expires time from '" + parsingValue + "'");
        }
        return calculatedMillis;
    }

    private Cookie getCookie(String name, String value) {
        Cookie cookie;
        if (log.isDebugEnabled()) {
            log.debug("making cookie for " + name + ", " + value);
        }
        if (name == null) {
            log.info("getCookie called with null name");
            return null;
        }
        if (value != null && value.indexOf(":") != -1) {
            String[] items = value.split(":");
            cookie = new Cookie(name, items[0]);
            if (items.length > 1) {
                cookie.setDomain(items[1]);
            }
            if (items.length > 2) {
                cookie.setMaxAge(NumberUtils.stringToInt(items[2]));
            }
            if (items.length > 3) {
                cookie.setPath(items[3]);
            }
        } else {
            cookie = new Cookie(name, value);
        }
        return cookie;
    }

    private void initNumericValue() {
        if (this.numericValue == 0) {
            this.numericValue = NumberUtils.stringToInt(StringUtils.trim(this.value));
            if (this.numericValue == 0 && !"0".equals(this.value)) {
                this.setError("Value " + this.value + " is not a valid number (tried to cast to java type long)");
            }
        }
    }
}

