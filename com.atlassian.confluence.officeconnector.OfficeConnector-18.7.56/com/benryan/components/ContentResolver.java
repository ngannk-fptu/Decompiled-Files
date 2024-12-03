/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.benryan.components;

import com.atlassian.confluence.core.ContentEntityObject;
import java.text.ParseException;

public interface ContentResolver {
    public ContentEntityObject getContent(String var1, String var2, String var3, ContentEntityObject var4) throws ParseException;
}

