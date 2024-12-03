/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.analytics.client.eventfilter.reader;

import com.atlassian.analytics.client.eventfilter.reader.FilterListReader;
import java.io.InputStream;
import javax.annotation.Nullable;

public class LocalListReader
implements FilterListReader {
    @Override
    @Nullable
    public InputStream readFilterList(String listName) {
        return LocalListReader.class.getResourceAsStream(this.getLocalListPath(listName));
    }

    private String getLocalListPath(String listName) {
        return "/filters/" + listName;
    }
}

