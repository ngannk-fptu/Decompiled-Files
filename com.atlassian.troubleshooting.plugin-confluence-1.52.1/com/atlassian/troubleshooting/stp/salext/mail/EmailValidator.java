/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public final class EmailValidator {
    private EmailValidator() {
    }

    @Nonnull
    public static boolean isValidEmailAddress(@Nullable String email) {
        if (email == null) {
            return false;
        }
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        }
        catch (AddressException ex) {
            return false;
        }
        return true;
    }
}

