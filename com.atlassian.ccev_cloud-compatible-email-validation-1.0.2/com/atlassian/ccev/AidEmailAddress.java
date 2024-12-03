/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.ccev;

import java.net.IDN;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class AidEmailAddress {
    AidEmailAddress() {
    }

    static boolean validEmail(String emailString) {
        String trimmedEmailString = emailString.trim();
        String[] parts = trimmedEmailString.split("@");
        if (parts.length != 2) {
            return false;
        }
        Predicate<String> nonEmpty = trimmedEmail -> !trimmedEmail.isEmpty();
        Predicate<String> usernameMaxLength = userName -> userName.getBytes(StandardCharsets.UTF_8).length <= 80;
        Predicate<String> validDomain = domain -> {
            Predicate<String> hasMultipleParts = dm -> dm.matches(".+\\..+");
            Predicate<String> domainMaxLength = dm -> dm.getBytes(StandardCharsets.UTF_8).length <= 255;
            return hasMultipleParts.and(domainMaxLength).test((String)domain);
        };
        Predicate<String> noWhitespaces = email -> Pattern.matches("[^\\p{Space}]+@[^\\p{Space}]+", email);
        Predicate<String> punycode = domain -> {
            String[] domainParts;
            try {
                domainParts = IDN.toASCII(domain).split("\\.");
            }
            catch (IllegalArgumentException e) {
                return false;
            }
            return Arrays.stream(domainParts).allMatch(p -> p.matches("^[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9]$)?"));
        };
        return nonEmpty.test(trimmedEmailString) && noWhitespaces.test(emailString) && usernameMaxLength.test(parts[0]) && validDomain.and(punycode).test(parts[1]);
    }
}

