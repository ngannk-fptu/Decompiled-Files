/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.widgetconnector.services;

import com.atlassian.confluence.extra.widgetconnector.exceptions.EmbedRetrievalException;
import java.util.regex.Pattern;

public interface HttpRetrievalEmbedService {
    public String getEmbedData(String var1, Pattern var2, String var3) throws EmbedRetrievalException;

    public String getNewLocation(String var1);
}

