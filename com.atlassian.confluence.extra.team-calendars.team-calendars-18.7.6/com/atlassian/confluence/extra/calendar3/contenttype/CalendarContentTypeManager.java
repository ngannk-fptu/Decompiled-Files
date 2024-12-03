/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.extra.calendar3.contenttype;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.spaces.Space;
import java.util.Iterator;
import java.util.List;

public interface CalendarContentTypeManager {
    public CustomContentEntityObject loadCalendarContent(String var1);

    public void createCalendarContentTypeFor(Space var1);

    public void createCalendarContentTypeFor(SubCalendarEntity var1);

    public void removeCalendarContentEntity(CustomContentEntityObject var1);

    public Iterator<CustomContentEntityObject> getAllSubCalendarContent();

    public PageResponse<CustomContentEntityObject> getAllSubCalendarContent(PageRequest var1);

    public CustomContentEntityObject loadCalendarContentBySpaceKey(String var1);

    public PageResponse<CustomContentEntityObject> getAllCalendarContent(PageRequest var1);

    public PageResponse<CustomContentEntityObject> getAllCalendarContentBySpaceKeys(PageRequest var1, List<String> var2);

    public PageResponse<CustomContentEntityObject> getSubCalendarContentByIds(PageRequest var1, List<String> var2);
}

