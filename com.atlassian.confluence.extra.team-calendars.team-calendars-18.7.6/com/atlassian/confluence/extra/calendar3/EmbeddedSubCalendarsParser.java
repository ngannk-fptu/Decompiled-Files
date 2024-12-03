/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.core.ContentEntityObject;
import java.util.Collection;

public interface EmbeddedSubCalendarsParser {
    public Collection<String> getEmbeddedSubCalendarIds(ContentEntityObject var1);
}

