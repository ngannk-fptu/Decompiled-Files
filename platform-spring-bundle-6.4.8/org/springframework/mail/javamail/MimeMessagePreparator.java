/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.MimeMessage
 */
package org.springframework.mail.javamail;

import javax.mail.internet.MimeMessage;

@FunctionalInterface
public interface MimeMessagePreparator {
    public void prepare(MimeMessage var1) throws Exception;
}

