/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail;

import com.atlassian.mail.Email;

public interface MailThreader {
    public void threadEmail(Email var1);

    public void storeSentEmail(Email var1);

    public String getCustomMessageId(Email var1);
}

