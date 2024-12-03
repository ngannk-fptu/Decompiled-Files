/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.mail;

import com.atlassian.upm.mail.EmailType;
import com.atlassian.upm.mail.UpmEmail;
import java.util.List;
import java.util.Map;

public interface MailRenderer {
    public String renderEmailSubject(EmailType var1, List<String> var2);

    public String renderEmailBody(EmailType var1, UpmEmail.Format var2, Map<String, Object> var3);
}

