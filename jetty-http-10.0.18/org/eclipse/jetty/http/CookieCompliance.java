/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.http.ComplianceViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookieCompliance
implements ComplianceViolation.Mode {
    private static final Logger LOG = LoggerFactory.getLogger(CookieCompliance.class);
    public static final CookieCompliance RFC6265 = new CookieCompliance("RFC6265", EnumSet.of(Violation.INVALID_COOKIES, Violation.OPTIONAL_WHITE_SPACE, Violation.SPACE_IN_VALUES));
    public static final CookieCompliance RFC6265_STRICT = new CookieCompliance("RFC6265_STRICT", EnumSet.noneOf(Violation.class));
    public static final CookieCompliance RFC6265_LEGACY = new CookieCompliance("RFC6265_LEGACY", EnumSet.of(Violation.ATTRIBUTES, new Violation[]{Violation.BAD_QUOTES, Violation.ESCAPE_IN_QUOTES, Violation.INVALID_COOKIES, Violation.OPTIONAL_WHITE_SPACE, Violation.SPECIAL_CHARS_IN_QUOTES, Violation.SPACE_IN_VALUES}));
    public static final CookieCompliance RFC2965_LEGACY = new CookieCompliance("RFC2965_LEGACY", EnumSet.allOf(Violation.class));
    public static final CookieCompliance RFC2965 = new CookieCompliance("RFC2965", EnumSet.complementOf(EnumSet.of(Violation.BAD_QUOTES, Violation.COMMA_NOT_VALID_OCTET, Violation.RESERVED_NAMES_NOT_DOLLAR_PREFIXED)));
    private static final List<CookieCompliance> KNOWN_MODES = Arrays.asList(RFC6265, RFC6265_STRICT, RFC6265_LEGACY, RFC2965, RFC2965_LEGACY);
    private static final AtomicInteger __custom = new AtomicInteger();
    private final String _name;
    private final Set<Violation> _violations;

    public static CookieCompliance valueOf(String name) {
        for (CookieCompliance compliance : KNOWN_MODES) {
            if (!compliance.getName().equals(name)) continue;
            return compliance;
        }
        return null;
    }

    public static CookieCompliance from(String spec) {
        CookieCompliance compliance = CookieCompliance.valueOf(spec);
        if (compliance == null) {
            EnumSet<Violation> violations;
            String[] elements = spec.split("\\s*,\\s*");
            switch (elements[0]) {
                case "0": {
                    violations = EnumSet.noneOf(Violation.class);
                    break;
                }
                case "*": {
                    violations = EnumSet.allOf(Violation.class);
                    break;
                }
                default: {
                    CookieCompliance mode = CookieCompliance.valueOf(elements[0]);
                    if (mode == null) {
                        throw new IllegalArgumentException("Unknown base mode: " + elements[0]);
                    }
                    violations = mode.getAllowed().isEmpty() ? EnumSet.noneOf(Violation.class) : EnumSet.copyOf(mode.getAllowed());
                }
            }
            for (int i = 1; i < elements.length; ++i) {
                String element = elements[i];
                boolean exclude = element.startsWith("-");
                if (exclude) {
                    element = element.substring(1);
                }
                Violation section = Violation.valueOf(element);
                if (exclude) {
                    violations.remove(section);
                    continue;
                }
                violations.add(section);
            }
            compliance = new CookieCompliance("CUSTOM" + __custom.getAndIncrement(), violations);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("CookieCompliance from {}->{}", (Object)spec, (Object)compliance);
        }
        return compliance;
    }

    public CookieCompliance(String name, Set<Violation> violations) {
        this._name = name;
        this._violations = Collections.unmodifiableSet(EnumSet.copyOf(Objects.requireNonNull(violations)));
    }

    @Override
    public boolean allows(ComplianceViolation violation) {
        return this._violations.contains(violation);
    }

    @Override
    public String getName() {
        return this._name;
    }

    public Set<Violation> getKnown() {
        return EnumSet.allOf(Violation.class);
    }

    public Set<Violation> getAllowed() {
        return this._violations;
    }

    public boolean compliesWith(CookieCompliance mode) {
        return this == mode || this.getAllowed().containsAll(mode.getAllowed());
    }

    public String toString() {
        return String.format("%s@%x%s", this._name, this.hashCode(), this._violations);
    }

    public static enum Violation implements ComplianceViolation
    {
        COMMA_NOT_VALID_OCTET("https://tools.ietf.org/html/rfc6265#section-4.2.1", "Comma not valid as cookie-octet or separator"),
        COMMA_SEPARATOR("https://www.rfc-editor.org/rfc/rfc2965.html", "Comma cookie separator"),
        RESERVED_NAMES_NOT_DOLLAR_PREFIXED("https://tools.ietf.org/html/rfc6265#section-4.2.1", "Reserved name no longer use '$' prefix"),
        SPECIAL_CHARS_IN_QUOTES("https://www.rfc-editor.org/rfc/rfc6265#section-4.2.1", "Special character cannot be quoted"),
        ESCAPE_IN_QUOTES("https://www.rfc-editor.org/rfc/rfc2616#section-2.2", "Escaped character in quotes"),
        BAD_QUOTES("https://www.rfc-editor.org/rfc/rfc2616#section-2.2", "Bad quotes"),
        INVALID_COOKIES("https://tools.ietf.org/html/rfc6265", "Invalid cookie"),
        ATTRIBUTES("https://www.rfc-editor.org/rfc/rfc6265#section-4.2.1", "Cookie attribute"),
        ATTRIBUTE_VALUES("https://www.rfc-editor.org/rfc/rfc6265#section-4.2.1", "Cookie attribute value"),
        OPTIONAL_WHITE_SPACE("https://www.rfc-editor.org/rfc/rfc6265#section-5.2", "White space around name/value"),
        SPACE_IN_VALUES("https://www.rfc-editor.org/rfc/rfc6265#section-5.2", "Space in value");

        private final String url;
        private final String description;

        private Violation(String url, String description) {
            this.url = url;
            this.description = description;
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public String getURL() {
            return this.url;
        }

        @Override
        public String getDescription() {
            return this.description;
        }
    }
}

