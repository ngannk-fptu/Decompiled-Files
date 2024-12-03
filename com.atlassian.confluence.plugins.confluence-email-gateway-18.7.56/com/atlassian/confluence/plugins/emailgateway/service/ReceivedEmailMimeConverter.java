/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeMessage
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public interface ReceivedEmailMimeConverter {
    public ReceivedEmail convertMimeMessage(MimeMessage var1, InternetAddress var2) throws Exception;
}

