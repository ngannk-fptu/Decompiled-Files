/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Throwables
 *  javax.annotation.Nullable
 *  javax.mail.Flags$Flag
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeMessage
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import javax.annotation.Nullable;
import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletedMessageFilter
implements Predicate<MimeMessage> {
    private static final Logger log = LoggerFactory.getLogger(DeletedMessageFilter.class);

    public boolean apply(@Nullable MimeMessage mimeMessage) {
        boolean pass = false;
        try {
            if (mimeMessage != null) {
                if (mimeMessage.getFlags().contains(Flags.Flag.DELETED)) {
                    log.warn("mimeMessage {} is flagged as deleted", (Object)mimeMessage.getMessageID());
                } else {
                    pass = true;
                }
            } else {
                log.error("mimeMessage is null, cannot apply filter");
            }
            return pass;
        }
        catch (MessagingException e) {
            throw Throwables.propagate((Throwable)e);
        }
    }
}

