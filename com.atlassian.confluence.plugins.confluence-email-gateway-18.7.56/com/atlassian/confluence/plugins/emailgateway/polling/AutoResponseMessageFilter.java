/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.mail.MessagingException
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeMessage
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.google.common.base.Predicate;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AutoResponseMessageFilter
implements Predicate<MimeMessage> {
    public boolean apply(MimeMessage message) {
        try {
            if (this.hasExcludedHeader(message, "Precedence", "bulk")) {
                return false;
            }
            if (this.hasExcludedHeader(message, "Auto-Submitted", "auto-replied")) {
                return false;
            }
            if (this.hasExcludedHeader(message, "X-Autoreply", "yes")) {
                return false;
            }
            String returnPath = message.getHeader("Return-Path", ";");
            if (returnPath == null || "<>".equals(returnPath)) {
                return false;
            }
            String from = ((InternetAddress)message.getFrom()[0]).getAddress();
            if (!returnPath.contains(from)) {
                return false;
            }
        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private boolean hasExcludedHeader(MimeMessage input, String header, String term) throws MessagingException {
        String foundHeader = input.getHeader(header, ";");
        return foundHeader != null && foundHeader.toLowerCase().contains(term);
    }
}

