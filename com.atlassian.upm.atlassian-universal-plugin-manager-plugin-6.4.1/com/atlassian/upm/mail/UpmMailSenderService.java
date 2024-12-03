/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.mail;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.Pairs;
import com.atlassian.upm.mail.EmailType;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UpmMailSenderService {
    public boolean canSendEmail();

    public void sendUpmEmail(EmailType var1, Pairs.Pair<String, String> var2, Set<UserKey> var3, List<String> var4, Map<String, Object> var5);
}

