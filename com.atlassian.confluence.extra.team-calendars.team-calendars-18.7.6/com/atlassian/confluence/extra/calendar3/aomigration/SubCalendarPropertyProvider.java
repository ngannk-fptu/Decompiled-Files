/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.JsonPropertyGetter;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import org.json.JSONObject;

public interface SubCalendarPropertyProvider {
    public SubCalendarEntity addProperties(ActiveObjectsServiceWrapper var1, SubCalendarEntity var2, JSONObject var3, JsonPropertyGetter var4);
}

