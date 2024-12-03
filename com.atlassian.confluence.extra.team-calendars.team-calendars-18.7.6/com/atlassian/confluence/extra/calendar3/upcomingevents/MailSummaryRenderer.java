/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.upcomingevents;

import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;

public interface MailSummaryRenderer {
    public String renderUpcomingEventsForMail(Collection<String> var1, boolean var2, ConfluenceUser var3);
}

