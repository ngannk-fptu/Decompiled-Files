/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.InetAddressValidator;

@Deprecated
public class EmailValidator {
    private static final String SPECIAL_CHARS = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String ATOM = "[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]+";
    private static final String WORD = "(([^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"[^\"]*\"))";
    private static final Pattern IP_DOMAIN_PATTERN = Pattern.compile("^\\[(.*)\\]$");
    private static final Pattern TLD_PATTERN = Pattern.compile("^([a-zA-Z]+)$");
    private static final Pattern USER_PATTERN = Pattern.compile("^\\s*(([^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"[^\"]*\"))(\\.(([^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\"[^\"]*\")))*$");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]+(\\.[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]+)*\\s*$");
    private static final Pattern ATOM_PATTERN = Pattern.compile("([^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]+)");
    private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator();

    public static EmailValidator getInstance() {
        return EMAIL_VALIDATOR;
    }

    protected EmailValidator() {
    }

    public boolean isValid(String email) {
        return org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email);
    }

    protected boolean isValidDomain(String domain) {
        boolean symbolic = false;
        Matcher ipDomainMatcher = IP_DOMAIN_PATTERN.matcher(domain);
        if (ipDomainMatcher.matches()) {
            InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();
            if (inetAddressValidator.isValid(ipDomainMatcher.group(1))) {
                return true;
            }
        } else {
            symbolic = DOMAIN_PATTERN.matcher(domain).matches();
        }
        if (symbolic) {
            return this.isValidSymbolicDomain(domain);
        }
        return false;
    }

    protected boolean isValidUser(String user) {
        return USER_PATTERN.matcher(user).matches();
    }

    protected boolean isValidIpAddress(String ipAddress) {
        Matcher ipAddressMatcher = IP_DOMAIN_PATTERN.matcher(ipAddress);
        for (int i = 1; i <= 4; ++i) {
            String ipSegment = ipAddressMatcher.group(i);
            if (ipSegment == null || ipSegment.length() <= 0) {
                return false;
            }
            int iIpSegment = 0;
            try {
                iIpSegment = Integer.parseInt(ipSegment);
            }
            catch (NumberFormatException e) {
                return false;
            }
            if (iIpSegment <= 255) continue;
            return false;
        }
        return true;
    }

    protected boolean isValidSymbolicDomain(String domain) {
        String[] domainSegment = new String[10];
        boolean match = true;
        int i = 0;
        Matcher atomMatcher = ATOM_PATTERN.matcher(domain);
        while (match) {
            match = atomMatcher.matches();
            if (!match) continue;
            domainSegment[i] = atomMatcher.group(1);
            int l = domainSegment[i].length() + 1;
            domain = l >= domain.length() ? "" : domain.substring(l);
            ++i;
        }
        int len = i;
        if (len < 2) {
            return false;
        }
        String tld = domainSegment[len - 1];
        if (tld.length() > 1) {
            return TLD_PATTERN.matcher(tld).matches();
        }
        return false;
    }

    protected String stripComments(String emailStr) {
        String result = emailStr;
        String commentPat = "^((?:[^\"\\\\]|\\\\.)*(?:\"(?:[^\"\\\\]|\\\\.)*\"(?:[^\"\\\\]|I111\\\\.)*)*)\\((?:[^()\\\\]|\\\\.)*\\)/";
        Pattern commentMatcher = Pattern.compile(commentPat);
        while (commentMatcher.matcher(result).matches()) {
            result = result.replaceFirst(commentPat, "\u0001 ");
        }
        return result;
    }
}

