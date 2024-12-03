/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.ccev;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class ValidatorJs {
    private static final Pattern tldBasic = Pattern.compile("^([a-z\\u00a1-\\uffff]{2,}|xn[a-z0-9-]{2,})$", 2);
    private static final Pattern tldSpecialAndSpaces = Pattern.compile("[\\s\\u2002-\\u200B\\u202F\\u205F\\u3000\\uFEFF\\uDB40\\uDC20\\u00A9\\uFFFD]");
    private static final Pattern allNumeric = Pattern.compile("^\\d+$");
    private static final Pattern domainPartBasic = Pattern.compile("^[a-z\\u00a1-\\uffff0-9-]+$", 2);
    private static final Pattern fullWidthChars = Pattern.compile("[\\uff01-\\uff5e]");
    private static final Pattern outerHyphen = Pattern.compile("(^-)|(-$)");
    private static final Pattern quotedEmailUserUtf8 = Pattern.compile("^([\\s\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f\\x21\\x23-\\x5b\\x5d-\\x7e\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]|(\\\\[\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))*$", 2);
    private static final Pattern emailUserUtf8Part = Pattern.compile("^[a-z\\d!#$%&'*+\\-/=?^_`{|}~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]+$", 2);
    private static final int defaultMaxEmailLength = 254;

    ValidatorJs() {
    }

    static boolean isEmail(String email) {
        if (email.length() > 254) {
            return false;
        }
        int atIndex = email.lastIndexOf(64);
        if (atIndex < 1) {
            return false;
        }
        String user = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);
        if (user.getBytes(StandardCharsets.UTF_8).length > 64 || domain.getBytes(StandardCharsets.UTF_8).length > 254) {
            return false;
        }
        if (!ValidatorJs.isFqdn(domain)) {
            return false;
        }
        if (user.charAt(0) == '\"') {
            user = user.substring(1, user.length() - 1);
            return quotedEmailUserUtf8.matcher(user).matches();
        }
        String[] userParts = user.split("\\.", -1);
        return Arrays.stream(userParts).allMatch(p -> emailUserUtf8Part.matcher((CharSequence)p).matches());
    }

    static boolean isFqdn(String domain) {
        String[] parts = domain.split("\\.", -1);
        String tld = parts[parts.length - 1];
        if (parts.length < 2 || !tldBasic.matcher(tld).matches() || tldSpecialAndSpaces.matcher(tld).find() || allNumeric.matcher(tld).matches()) {
            return false;
        }
        return Arrays.stream(parts).allMatch(part -> part.length() <= 63 && domainPartBasic.matcher((CharSequence)part).matches() && !fullWidthChars.matcher((CharSequence)part).find() && !outerHyphen.matcher((CharSequence)part).find());
    }
}

