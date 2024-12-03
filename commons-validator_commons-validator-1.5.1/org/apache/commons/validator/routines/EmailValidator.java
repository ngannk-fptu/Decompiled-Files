/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

public class EmailValidator
implements Serializable {
    private static final long serialVersionUID = 1705927040799295880L;
    private static final String SPECIAL_CHARS = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "(\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]";
    private static final String QUOTED_USER = "(\"(\\\\\"|[^\"])*\")";
    private static final String WORD = "(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\"))";
    private static final String EMAIL_REGEX = "^\\s*?(.+)@(.+?)\\s*$";
    private static final String IP_DOMAIN_REGEX = "^\\[(.*)\\]$";
    private static final String USER_REGEX = "^\\s*(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\"))(\\.(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\")))*$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\s*?(.+)@(.+?)\\s*$");
    private static final Pattern IP_DOMAIN_PATTERN = Pattern.compile("^\\[(.*)\\]$");
    private static final Pattern USER_PATTERN = Pattern.compile("^\\s*(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\"))(\\.(((\\\\.)|[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"(\\\\\"|[^\"])*\")))*$");
    private static final int MAX_USERNAME_LEN = 64;
    private final boolean allowLocal;
    private final boolean allowTld;
    private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator(false, false);
    private static final EmailValidator EMAIL_VALIDATOR_WITH_TLD = new EmailValidator(false, true);
    private static final EmailValidator EMAIL_VALIDATOR_WITH_LOCAL = new EmailValidator(true, false);
    private static final EmailValidator EMAIL_VALIDATOR_WITH_LOCAL_WITH_TLD = new EmailValidator(true, true);

    public static EmailValidator getInstance() {
        return EMAIL_VALIDATOR;
    }

    public static EmailValidator getInstance(boolean allowLocal, boolean allowTld) {
        if (allowLocal) {
            if (allowTld) {
                return EMAIL_VALIDATOR_WITH_LOCAL_WITH_TLD;
            }
            return EMAIL_VALIDATOR_WITH_LOCAL;
        }
        if (allowTld) {
            return EMAIL_VALIDATOR_WITH_TLD;
        }
        return EMAIL_VALIDATOR;
    }

    public static EmailValidator getInstance(boolean allowLocal) {
        return EmailValidator.getInstance(allowLocal, false);
    }

    protected EmailValidator(boolean allowLocal, boolean allowTld) {
        this.allowLocal = allowLocal;
        this.allowTld = allowTld;
    }

    protected EmailValidator(boolean allowLocal) {
        this.allowLocal = allowLocal;
        this.allowTld = false;
    }

    public boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.endsWith(".")) {
            return false;
        }
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            return false;
        }
        if (!this.isValidUser(emailMatcher.group(1))) {
            return false;
        }
        return this.isValidDomain(emailMatcher.group(2));
    }

    protected boolean isValidDomain(String domain) {
        Matcher ipDomainMatcher = IP_DOMAIN_PATTERN.matcher(domain);
        if (ipDomainMatcher.matches()) {
            InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();
            return inetAddressValidator.isValid(ipDomainMatcher.group(1));
        }
        DomainValidator domainValidator = DomainValidator.getInstance(this.allowLocal);
        if (this.allowTld) {
            return domainValidator.isValid(domain) || !domain.startsWith(".") && domainValidator.isValidTld(domain);
        }
        return domainValidator.isValid(domain);
    }

    protected boolean isValidUser(String user) {
        if (user == null || user.length() > 64) {
            return false;
        }
        return USER_PATTERN.matcher(user).matches();
    }
}

