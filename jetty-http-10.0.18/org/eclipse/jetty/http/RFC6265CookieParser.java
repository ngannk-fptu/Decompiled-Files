/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.StringUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.http;

import java.util.Locale;
import org.eclipse.jetty.http.ComplianceViolation;
import org.eclipse.jetty.http.CookieCompliance;
import org.eclipse.jetty.http.CookieParser;
import org.eclipse.jetty.http.HttpTokens;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RFC6265CookieParser
implements CookieParser {
    protected static final Logger LOG = LoggerFactory.getLogger(RFC6265CookieParser.class);
    private final CookieParser.Handler _handler;
    private final CookieCompliance _complianceMode;
    private final ComplianceViolation.Listener _complianceListener;

    protected RFC6265CookieParser(CookieParser.Handler handler, CookieCompliance compliance, ComplianceViolation.Listener complianceListener) {
        this._handler = handler;
        this._complianceMode = compliance;
        this._complianceListener = complianceListener;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void parseField(String field) {
        state = State.START;
        attributeName = null;
        value = null;
        cookieName = null;
        cookieValue = null;
        cookiePath = null;
        cookieDomain = null;
        cookieComment = null;
        cookieVersion = 0;
        cookieInvalid = false;
        spaces = 0;
        length = field.length();
        string = new StringBuilder();
        block25: for (i = 0; i <= length; ++i) {
            c = i == length ? ';' : (char)field.charAt(i);
            token = HttpTokens.getToken(c);
            if (token == null) {
                if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                    throw new CookieParser.InvalidCookieException("Invalid Cookie character");
                }
                state = State.INVALID_COOKIE;
                continue;
            }
            switch (1.$SwitchMap$org$eclipse$jetty$http$RFC6265CookieParser$State[state.ordinal()]) {
                case 1: {
                    if (c == ' ' || c == '\t' || c == ';') continue block25;
                    string.setLength(0);
                    if (token.isRfc2616Token()) {
                        if (!(StringUtil.isBlank((String)cookieName) || c == '$' && (this._complianceMode.allows(CookieCompliance.Violation.ATTRIBUTES) || this._complianceMode.allows(CookieCompliance.Violation.ATTRIBUTE_VALUES)))) {
                            this._handler.addCookie(cookieName, cookieValue, cookieVersion, cookieDomain, cookiePath, cookieComment);
                            cookieName = null;
                            cookieValue = null;
                            cookieDomain = null;
                            cookiePath = null;
                            cookieComment = null;
                        }
                        string.append(c);
                        state = State.IN_NAME;
                        continue block25;
                    }
                    if (this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = State.INVALID_COOKIE;
                        continue block25;
                    }
                    throw new CookieParser.InvalidCookieException("Bad Cookie name");
                }
                case 2: {
                    if (c == '=') {
                        if (string.charAt(0) == '$') {
                            attributeName = string.toString();
                        } else {
                            cookieName = string.toString();
                        }
                        state = State.VALUE;
                        continue block25;
                    }
                    if ((c == ' ' || c == '\t') && this._complianceMode.allows(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE, field);
                        if (string.charAt(0) == '$') {
                            attributeName = string.toString();
                        } else {
                            cookieName = string.toString();
                        }
                        state = State.AFTER_NAME;
                        continue block25;
                    }
                    if (token.isRfc2616Token()) {
                        string.append(c);
                        continue block25;
                    }
                    if (this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = c == ';' ? State.START : State.INVALID_COOKIE;
                        continue block25;
                    }
                    throw new CookieParser.InvalidCookieException("Bad Cookie name");
                }
                case 3: {
                    if (c == '=') {
                        state = State.VALUE;
                        continue block25;
                    }
                    if (c == ';' || c == ',') {
                        state = State.START;
                        continue block25;
                    }
                    if (this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = State.INVALID_COOKIE;
                        continue block25;
                    }
                    throw new CookieParser.InvalidCookieException("Bad Cookie");
                }
                case 4: {
                    if (c == ' ' && this._complianceMode.allows(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE, field);
                        continue block25;
                    }
                    string.setLength(0);
                    if (c == '\"') {
                        state = State.IN_QUOTED_VALUE;
                        continue block25;
                    }
                    if (c == ';') {
                        value = "";
                        --i;
                        state = State.END;
                        continue block25;
                    }
                    if (token.isRfc6265CookieOctet()) {
                        string.append(c);
                        state = State.IN_VALUE;
                        continue block25;
                    }
                    if (this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = State.INVALID_COOKIE;
                        continue block25;
                    }
                    throw new CookieParser.InvalidCookieException("Bad Cookie value");
                }
                case 5: {
                    if (c == ' ' && this._complianceMode.allows(CookieCompliance.Violation.SPACE_IN_VALUES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.SPACE_IN_VALUES, field);
                        spaces = 1;
                        state = State.SPACE_IN_VALUE;
                        continue block25;
                    }
                    if (c == ' ' || c == ';' || c == ',' || c == '\t') {
                        value = string.toString();
                        --i;
                        state = State.END;
                        continue block25;
                    }
                    if (token.isRfc6265CookieOctet()) {
                        string.append(c);
                        continue block25;
                    }
                    if (this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = State.INVALID_COOKIE;
                        continue block25;
                    }
                    throw new CookieParser.InvalidCookieException("Bad Cookie value");
                }
                case 6: {
                    if (c == ' ') {
                        ++spaces;
                        continue block25;
                    }
                    if (c == ';' || c == ',' || c == '\t') {
                        value = string.toString();
                        --i;
                        state = State.END;
                        continue block25;
                    }
                    if (token.isRfc6265CookieOctet()) {
                        string.append(" ".repeat(spaces)).append(c);
                        state = State.IN_VALUE;
                        continue block25;
                    }
                    if (this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = State.INVALID_COOKIE;
                        continue block25;
                    }
                    throw new CookieParser.InvalidCookieException("Bad Cookie value");
                }
                case 7: {
                    if (c == '\"') {
                        value = string.toString();
                        state = State.AFTER_QUOTED_VALUE;
                        continue block25;
                    }
                    if (c == '\\' && this._complianceMode.allows(CookieCompliance.Violation.ESCAPE_IN_QUOTES)) {
                        state = State.ESCAPED_VALUE;
                        continue block25;
                    }
                    if (token.isRfc6265CookieOctet()) {
                        string.append(c);
                        continue block25;
                    }
                    if (this._complianceMode.allows(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES, field);
                        string.append(c);
                        continue block25;
                    }
                    if (c == ',' && this._complianceMode.allows(CookieCompliance.Violation.COMMA_NOT_VALID_OCTET)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.COMMA_NOT_VALID_OCTET, field);
                        string.append(c);
                        continue block25;
                    }
                    if (c == ' ' && this._complianceMode.allows(CookieCompliance.Violation.SPACE_IN_VALUES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.SPACE_IN_VALUES, field);
                        string.append(c);
                        continue block25;
                    }
                    if (this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        string.append(c);
                        if (cookieInvalid) continue block25;
                        cookieInvalid = true;
                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        continue block25;
                    }
                    throw new CookieParser.InvalidCookieException("Bad Cookie quoted value");
                }
                case 8: {
                    string.append(c);
                    state = State.IN_QUOTED_VALUE;
                    continue block25;
                }
                case 9: {
                    if (c == ';' || c == ',' || c == ' ' || c == '\t') {
                        --i;
                        state = cookieInvalid != false ? State.INVALID_COOKIE : State.END;
                        continue block25;
                    }
                    if (this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = State.INVALID_COOKIE;
                        continue block25;
                    }
                    throw new CookieParser.InvalidCookieException("Bad Cookie quoted value");
                }
                case 10: {
                    if (c != 59) ** GOTO lbl196
                    state = State.START;
                    ** GOTO lbl209
lbl196:
                    // 1 sources

                    if (c != 44) ** GOTO lbl206
                    if (this._complianceMode.allows(CookieCompliance.Violation.COMMA_SEPARATOR)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.COMMA_SEPARATOR, field);
                        state = State.START;
                    } else {
                        if (this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                            this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                            state = State.INVALID_COOKIE;
                            continue block25;
                        }
                        throw new CookieParser.InvalidCookieException("Comma cookie separator");
lbl206:
                        // 1 sources

                        if ((c == ' ' || c == '\t') && this._complianceMode.allows(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE)) {
                            this.reportComplianceViolation(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE, field);
                            continue block25;
                        }
                    }
lbl209:
                    // 4 sources

                    if (StringUtil.isBlank(attributeName)) {
                        cookieValue = value;
                    } else {
                        if (this._complianceMode.allows(CookieCompliance.Violation.ATTRIBUTE_VALUES)) {
                            this.reportComplianceViolation(CookieCompliance.Violation.ATTRIBUTES, field);
                            var18_18 = attributeName.toLowerCase(Locale.ENGLISH);
                            var19_19 = -1;
                            switch (var18_18.hashCode()) {
                                case 36680265: {
                                    if (!var18_18.equals("$path")) break;
                                    var19_19 = 0;
                                    break;
                                }
                                case 559163880: {
                                    if (!var18_18.equals("$domain")) break;
                                    var19_19 = 1;
                                    break;
                                }
                                case 36693669: {
                                    if (!var18_18.equals("$port")) break;
                                    var19_19 = 2;
                                    break;
                                }
                                case -1331729356: {
                                    if (!var18_18.equals("$version")) break;
                                    var19_19 = 3;
                                }
                            }
                            switch (var19_19) {
                                case 0: {
                                    cookiePath = value;
                                    break;
                                }
                                case 1: {
                                    cookieDomain = value;
                                    break;
                                }
                                case 2: {
                                    cookieComment = "$port=" + value;
                                    break;
                                }
                                case 3: {
                                    cookieVersion = Integer.parseInt(value);
                                    break;
                                }
                                default: {
                                    if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                                        throw new IllegalArgumentException("Invalid Cookie attribute");
                                    }
                                    this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                                    state = State.INVALID_COOKIE;
                                    break;
                                }
                            }
                        } else if (this._complianceMode.allows(CookieCompliance.Violation.ATTRIBUTES)) {
                            this.reportComplianceViolation(CookieCompliance.Violation.ATTRIBUTES, field);
                        } else {
                            cookieName = attributeName;
                            cookieValue = value;
                        }
                        attributeName = null;
                    }
                    value = null;
                    if (state != State.END) continue block25;
                    throw new CookieParser.InvalidCookieException("Invalid cookie");
                }
                case 11: {
                    attributeName = null;
                    value = null;
                    cookieName = null;
                    cookieValue = null;
                    cookiePath = null;
                    cookieDomain = null;
                    cookieComment = null;
                    cookieInvalid = false;
                    if (c != 59) continue block25;
                    state = State.START;
                }
            }
        }
        if (!cookieInvalid && !StringUtil.isBlank(cookieName)) {
            this._handler.addCookie(cookieName, cookieValue, cookieVersion, cookieDomain, cookiePath, cookieComment);
        }
    }

    protected void reportComplianceViolation(CookieCompliance.Violation violation, String reason) {
        if (this._complianceListener != null) {
            this._complianceListener.onComplianceViolation(this._complianceMode, violation, reason);
        }
    }

    private static enum State {
        START,
        IN_NAME,
        AFTER_NAME,
        VALUE,
        IN_VALUE,
        SPACE_IN_VALUE,
        IN_QUOTED_VALUE,
        ESCAPED_VALUE,
        AFTER_QUOTED_VALUE,
        END,
        INVALID_COOKIE;

    }
}

