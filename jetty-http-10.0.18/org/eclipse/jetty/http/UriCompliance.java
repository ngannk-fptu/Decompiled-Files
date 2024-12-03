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
import org.eclipse.jetty.http.HttpURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UriCompliance
implements ComplianceViolation.Mode {
    protected static final Logger LOG = LoggerFactory.getLogger(UriCompliance.class);
    public static final UriCompliance DEFAULT = new UriCompliance("DEFAULT", EnumSet.of(Violation.AMBIGUOUS_PATH_SEPARATOR, Violation.AMBIGUOUS_PATH_ENCODING));
    public static final UriCompliance LEGACY = new UriCompliance("LEGACY", EnumSet.of(Violation.AMBIGUOUS_PATH_SEGMENT, Violation.AMBIGUOUS_PATH_SEPARATOR, Violation.AMBIGUOUS_PATH_ENCODING, Violation.AMBIGUOUS_EMPTY_SEGMENT, Violation.UTF16_ENCODINGS));
    public static final UriCompliance RFC3986 = new UriCompliance("RFC3986", EnumSet.allOf(Violation.class));
    public static final UriCompliance RFC3986_UNAMBIGUOUS = new UriCompliance("RFC3986_UNAMBIGUOUS", EnumSet.noneOf(Violation.class));
    public static final UriCompliance UNSAFE = new UriCompliance("UNSAFE", EnumSet.allOf(Violation.class));
    @Deprecated
    public static final UriCompliance SAFE = new UriCompliance("SAFE", DEFAULT.getAllowed());
    @Deprecated
    public static final UriCompliance STRICT = new UriCompliance("STRICT", RFC3986.getAllowed());
    private static final AtomicInteger __custom = new AtomicInteger();
    private static final List<UriCompliance> KNOWN_MODES = List.of(DEFAULT, LEGACY, RFC3986, RFC3986_UNAMBIGUOUS, UNSAFE, SAFE, STRICT);
    private final String _name;
    private final Set<Violation> _allowed;

    public static UriCompliance valueOf(String name) {
        for (UriCompliance compliance : KNOWN_MODES) {
            if (!compliance.getName().equals(name)) continue;
            return compliance;
        }
        LOG.warn("Unknown UriCompliance mode {}", (Object)name);
        return null;
    }

    public static UriCompliance from(Set<Violation> violations) {
        return new UriCompliance("CUSTOM" + __custom.getAndIncrement(), violations);
    }

    public static UriCompliance from(String spec) {
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
                UriCompliance mode = UriCompliance.valueOf(elements[0]);
                violations = mode == null ? EnumSet.noneOf(Violation.class) : UriCompliance.copyOf(mode.getAllowed());
                break;
            }
        }
        for (int i = 1; i < elements.length; ++i) {
            String element = elements[i];
            boolean exclude = element.startsWith("-");
            if (exclude) {
                element = element.substring(1);
            }
            if (element.equals("NON_CANONICAL_AMBIGUOUS_PATHS")) continue;
            Violation section = Violation.valueOf(element);
            if (exclude) {
                violations.remove(section);
                continue;
            }
            violations.add(section);
        }
        UriCompliance compliance = new UriCompliance("CUSTOM" + __custom.getAndIncrement(), violations);
        if (LOG.isDebugEnabled()) {
            LOG.debug("UriCompliance from {}->{}", (Object)spec, (Object)compliance);
        }
        return compliance;
    }

    public UriCompliance(String name, Set<Violation> violations) {
        Objects.requireNonNull(violations);
        this._name = name;
        this._allowed = Collections.unmodifiableSet(violations.isEmpty() ? EnumSet.noneOf(Violation.class) : UriCompliance.copyOf(violations));
    }

    @Override
    public boolean allows(ComplianceViolation violation) {
        return violation instanceof Violation && this._allowed.contains(violation);
    }

    @Override
    public String getName() {
        return this._name;
    }

    public Set<Violation> getAllowed() {
        return this._allowed;
    }

    public Set<Violation> getKnown() {
        return EnumSet.allOf(Violation.class);
    }

    public UriCompliance with(String name, Violation ... violations) {
        Set<Violation> union = this._allowed.isEmpty() ? EnumSet.noneOf(Violation.class) : UriCompliance.copyOf(this._allowed);
        union.addAll(UriCompliance.copyOf(violations));
        return new UriCompliance(name, union);
    }

    public UriCompliance without(String name, Violation ... violations) {
        Set<Violation> remainder = this._allowed.isEmpty() ? EnumSet.noneOf(Violation.class) : UriCompliance.copyOf(this._allowed);
        remainder.removeAll(UriCompliance.copyOf(violations));
        return new UriCompliance(name, remainder);
    }

    public String toString() {
        return String.format("%s%s", this._name, this._allowed);
    }

    private static Set<Violation> copyOf(Violation[] violations) {
        if (violations == null || violations.length == 0) {
            return EnumSet.noneOf(Violation.class);
        }
        return EnumSet.copyOf(Arrays.asList(violations));
    }

    private static Set<Violation> copyOf(Set<Violation> violations) {
        if (violations == null || violations.isEmpty()) {
            return EnumSet.noneOf(Violation.class);
        }
        return EnumSet.copyOf(violations);
    }

    public static String checkUriCompliance(UriCompliance compliance, HttpURI uri) {
        for (Violation violation : Violation.values()) {
            if (!uri.hasViolation(violation) || compliance != null && compliance.allows(violation)) continue;
            return violation.getDescription();
        }
        return null;
    }

    public static enum Violation implements ComplianceViolation
    {
        AMBIGUOUS_PATH_SEGMENT("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI path segment"),
        AMBIGUOUS_EMPTY_SEGMENT("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI empty segment"),
        AMBIGUOUS_PATH_SEPARATOR("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI path separator"),
        AMBIGUOUS_PATH_PARAMETER("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI path parameter"),
        AMBIGUOUS_PATH_ENCODING("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI path encoding"),
        UTF16_ENCODINGS("https://www.w3.org/International/iri-edit/draft-duerst-iri.html#anchor29", "UTF16 encoding");

        private final String _url;
        private final String _description;

        private Violation(String url, String description) {
            this._url = url;
            this._description = description;
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public String getURL() {
            return this._url;
        }

        @Override
        public String getDescription() {
            return this._description;
        }
    }
}

