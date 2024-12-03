/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.dailysummary.components;

import com.atlassian.user.User;
import java.util.Date;

public interface SummaryEmailService {
    public int sendEmailForDate(Date var1);

    public boolean sendEmail(User var1, Date var2);
}

