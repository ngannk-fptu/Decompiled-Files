/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.analytics.client.eventfilter.reader;

import java.io.InputStream;
import javax.annotation.Nullable;

public interface FilterListReader {
    @Nullable
    public InputStream readFilterList(String var1);
}

