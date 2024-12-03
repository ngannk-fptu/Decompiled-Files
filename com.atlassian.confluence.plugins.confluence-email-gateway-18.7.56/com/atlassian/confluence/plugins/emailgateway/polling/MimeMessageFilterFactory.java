/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.mail.internet.MimeMessage
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.google.common.base.Predicate;
import javax.mail.internet.MimeMessage;

public interface MimeMessageFilterFactory {
    public Predicate<MimeMessage> getFilter();
}

