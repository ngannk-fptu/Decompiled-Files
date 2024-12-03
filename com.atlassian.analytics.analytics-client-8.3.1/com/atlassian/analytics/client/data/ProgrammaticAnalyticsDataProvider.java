/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.analytics.client.data;

import com.atlassian.analytics.client.detect.ProgrammaticAnalyticsDetector;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.WebResourceDataProvider;

public class ProgrammaticAnalyticsDataProvider
implements WebResourceDataProvider {
    private final ProgrammaticAnalyticsDetector programmaticAnalyticsDetector;

    public ProgrammaticAnalyticsDataProvider(ProgrammaticAnalyticsDetector programmaticAnalyticsDetector) {
        this.programmaticAnalyticsDetector = programmaticAnalyticsDetector;
    }

    public Jsonable get() {
        return writer -> writer.write(String.valueOf(this.programmaticAnalyticsDetector.isEnabled()));
    }
}

