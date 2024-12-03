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

public final class HttpCompliance
implements ComplianceViolation.Mode {
    protected static final Logger LOG = LoggerFactory.getLogger(HttpCompliance.class);
    public static final String VIOLATIONS_ATTR = "org.eclipse.jetty.http.compliance.violations";
    public static final HttpCompliance RFC7230 = new HttpCompliance("RFC7230", EnumSet.noneOf(Violation.class));
    public static final HttpCompliance RFC2616 = new HttpCompliance("RFC2616", EnumSet.of(Violation.HTTP_0_9, Violation.MULTILINE_FIELD_VALUE, Violation.MISMATCHED_AUTHORITY));
    public static final HttpCompliance LEGACY = new HttpCompliance("LEGACY", EnumSet.complementOf(EnumSet.of(Violation.CASE_INSENSITIVE_METHOD)));
    public static final HttpCompliance RFC2616_LEGACY = RFC2616.with("RFC2616_LEGACY", Violation.CASE_INSENSITIVE_METHOD, Violation.NO_COLON_AFTER_FIELD_NAME, Violation.TRANSFER_ENCODING_WITH_CONTENT_LENGTH, Violation.MULTIPLE_CONTENT_LENGTHS);
    public static final HttpCompliance RFC7230_LEGACY = RFC7230.with("RFC7230_LEGACY", Violation.CASE_INSENSITIVE_METHOD);
    private static final List<HttpCompliance> KNOWN_MODES = Arrays.asList(RFC7230, RFC2616, LEGACY, RFC2616_LEGACY, RFC7230_LEGACY);
    private static final AtomicInteger __custom = new AtomicInteger();
    private final String _name;
    private final Set<Violation> _violations;

    public static HttpCompliance valueOf(String name) {
        for (HttpCompliance compliance : KNOWN_MODES) {
            if (!compliance.getName().equals(name)) continue;
            return compliance;
        }
        LOG.warn("Unknown HttpCompliance mode {}", (Object)name);
        return null;
    }

    public static HttpCompliance from(String spec) {
        EnumSet<Violation> sections;
        String[] elements = spec.split("\\s*,\\s*");
        switch (elements[0]) {
            case "0": {
                sections = EnumSet.noneOf(Violation.class);
                break;
            }
            case "*": {
                sections = EnumSet.allOf(Violation.class);
                break;
            }
            default: {
                HttpCompliance mode = HttpCompliance.valueOf(elements[0]);
                sections = mode == null ? EnumSet.noneOf(Violation.class) : HttpCompliance.copyOf(mode.getAllowed());
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
                sections.remove(section);
                continue;
            }
            sections.add(section);
        }
        return new HttpCompliance("CUSTOM" + __custom.getAndIncrement(), sections);
    }

    private HttpCompliance(String name, Set<Violation> violations) {
        Objects.requireNonNull(violations);
        this._name = name;
        this._violations = Collections.unmodifiableSet(violations.isEmpty() ? EnumSet.noneOf(Violation.class) : HttpCompliance.copyOf(violations));
    }

    @Override
    public boolean allows(ComplianceViolation violation) {
        return violation instanceof Violation && this._violations.contains(violation);
    }

    @Override
    public String getName() {
        return this._name;
    }

    public Set<Violation> getAllowed() {
        return this._violations;
    }

    public Set<Violation> getKnown() {
        return EnumSet.allOf(Violation.class);
    }

    public HttpCompliance with(String name, Violation ... violations) {
        Set<Violation> union = this._violations.isEmpty() ? EnumSet.noneOf(Violation.class) : HttpCompliance.copyOf(this._violations);
        union.addAll(HttpCompliance.copyOf(violations));
        return new HttpCompliance(name, union);
    }

    public HttpCompliance without(String name, Violation ... violations) {
        Set<Violation> remainder = this._violations.isEmpty() ? EnumSet.noneOf(Violation.class) : HttpCompliance.copyOf(this._violations);
        remainder.removeAll(HttpCompliance.copyOf(violations));
        return new HttpCompliance(name, remainder);
    }

    public String toString() {
        return String.format("%s%s", this._name, this._violations);
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

    public static enum Violation implements ComplianceViolation
    {
        CASE_SENSITIVE_FIELD_NAME("https://tools.ietf.org/html/rfc7230#section-3.2", "Field name is case-insensitive"),
        CASE_INSENSITIVE_METHOD("https://tools.ietf.org/html/rfc7230#section-3.1.1", "Method is case-sensitive"),
        HTTP_0_9("https://tools.ietf.org/html/rfc7230#appendix-A.2", "HTTP/0.9 not supported"),
        MULTILINE_FIELD_VALUE("https://tools.ietf.org/html/rfc7230#section-3.2.4", "Line Folding not supported"),
        MULTIPLE_CONTENT_LENGTHS("https://tools.ietf.org/html/rfc7230#section-3.3.2", "Multiple Content-Lengths"),
        TRANSFER_ENCODING_WITH_CONTENT_LENGTH("https://tools.ietf.org/html/rfc7230#section-3.3.1", "Transfer-Encoding and Content-Length"),
        WHITESPACE_AFTER_FIELD_NAME("https://tools.ietf.org/html/rfc7230#section-3.2.4", "Whitespace not allowed after field name"),
        NO_COLON_AFTER_FIELD_NAME("https://tools.ietf.org/html/rfc7230#section-3.2", "Fields must have a Colon"),
        DUPLICATE_HOST_HEADERS("https://www.rfc-editor.org/rfc/rfc7230#section-5.4", "Duplicate Host Header"),
        UNSAFE_HOST_HEADER("https://www.rfc-editor.org/rfc/rfc7230#section-2.7.1", "Invalid Authority"),
        MISMATCHED_AUTHORITY("https://www.rfc-editor.org/rfc/rfc7230#section-5.4", "Mismatched Authority");

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

