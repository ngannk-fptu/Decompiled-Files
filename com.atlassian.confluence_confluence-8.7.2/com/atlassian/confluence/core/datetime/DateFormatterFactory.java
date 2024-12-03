/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.datetime;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import java.util.Date;

public interface DateFormatterFactory {
    public DateFormatter createForUser();

    public DateFormatter createGlobal();

    public FriendlyDateFormatter createFriendlyForUser(Date var1);

    public FriendlyDateFormatter createFriendlyForUser();
}

