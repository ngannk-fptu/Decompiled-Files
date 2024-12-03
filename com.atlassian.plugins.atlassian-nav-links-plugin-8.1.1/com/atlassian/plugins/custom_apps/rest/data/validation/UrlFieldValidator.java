/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.validator.routines.InetAddressValidator
 *  org.apache.commons.validator.routines.RegexValidator
 *  org.apache.commons.validator.routines.UrlValidator
 */
package com.atlassian.plugins.custom_apps.rest.data.validation;

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.UrlValidator;

public abstract class UrlFieldValidator {
    public static UrlFieldValidator jira() {
        return new Jira();
    }

    public abstract boolean isValid(@Nonnull String var1);

    public static class Jira
    extends UrlFieldValidator {
        @Override
        public boolean isValid(@Nonnull String input) {
            return JiraBaseUrlValidator.isValid(input);
        }
    }

    public static final class JiraBaseUrlValidator {
        private static UrlValidator urlValidator;

        public static boolean isValid(String url) {
            if (url == null) {
                return false;
            }
            try {
                URI uri = new URI(url);
                if (uri.getAuthority() == null) {
                    return false;
                }
                url = new URI(uri.getScheme(), IDN.toASCII(uri.getAuthority()), uri.getPath(), null, null).toASCIIString();
                return urlValidator.isValid(url);
            }
            catch (URISyntaxException e) {
                return false;
            }
        }

        static {
            String[] schemes = new String[]{"http", "https"};
            urlValidator = new MyValidator(schemes);
        }

        private static class MyValidator
        extends UrlValidator {
            private static final String AUTHORITY_CHARS_REGEX = "\\p{Alnum}\\-\\.";
            private static final String AUTHORITY_REGEX = "^([\\p{Alnum}\\-\\.]*)(:\\d*)?(.*)?";
            private static final Pattern AUTHORITY_PATTERN = Pattern.compile("^([\\p{Alnum}\\-\\.]*)(:\\d*)?(.*)?");
            private static final int PARSE_AUTHORITY_HOST_IP = 1;
            private static final int PARSE_AUTHORITY_PORT = 2;
            private static final int PARSE_AUTHORITY_EXTRA = 3;
            private static final String DOMAIN_LABEL_REGEX = "\\p{Alnum}(?>[\\p{Alnum}-]*\\p{Alnum})*";
            private static final String TOP_LABEL_REGEX = "\\p{Alpha}{2,}";
            private static final String IDN_TOP_LABEL_REGEX = "xn--\\p{Alnum}+";
            private static final String DOMAIN_NAME_REGEX = "^(?:\\p{Alnum}(?>[\\p{Alnum}-]*\\p{Alnum})*\\.)+(\\p{Alpha}{2,}|xn--\\p{Alnum}+)$";
            private static final String PORT_REGEX = "^:(\\d{1,5})$";
            private static final Pattern PORT_PATTERN = Pattern.compile("^:(\\d{1,5})$");
            private final RegexValidator domainRegex = new RegexValidator("^(?:\\p{Alnum}(?>[\\p{Alnum}-]*\\p{Alnum})*\\.)+(\\p{Alpha}{2,}|xn--\\p{Alnum}+)$");
            private final RegexValidator hostnameRegex = new RegexValidator("\\p{Alnum}(?>[\\p{Alnum}-]*\\p{Alnum})*");

            public MyValidator(String[] schemes) {
                super(schemes);
            }

            private boolean isValidDomain(String domain) {
                String[] groups = this.domainRegex.match(domain);
                if (groups != null && groups.length > 0) {
                    return true;
                }
                return this.hostnameRegex.isValid(domain);
            }

            protected boolean isValidAuthority(String authority) {
                InetAddressValidator inetAddressValidator;
                if (authority == null) {
                    return false;
                }
                Matcher authorityMatcher = AUTHORITY_PATTERN.matcher(authority);
                if (!authorityMatcher.matches()) {
                    return false;
                }
                String hostLocation = authorityMatcher.group(1);
                if (!this.isValidDomain(hostLocation) && !(inetAddressValidator = InetAddressValidator.getInstance()).isValid(hostLocation)) {
                    return false;
                }
                String port = authorityMatcher.group(2);
                if (port != null && !PORT_PATTERN.matcher(port).matches()) {
                    return false;
                }
                String extra = authorityMatcher.group(3);
                return extra == null || extra.trim().length() <= 0;
            }
        }
    }
}

