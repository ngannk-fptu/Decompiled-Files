/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.analytics.client.data;

import com.atlassian.analytics.client.detect.PrivacyPolicyUpdateDetector;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.WebResourceDataProvider;

public class PolicyUpdateDataProvider
implements WebResourceDataProvider {
    private final PrivacyPolicyUpdateDetector privacyPolicyUpdateDetector;

    public PolicyUpdateDataProvider(PrivacyPolicyUpdateDetector privacyPolicyUpdateDetector) {
        this.privacyPolicyUpdateDetector = privacyPolicyUpdateDetector;
    }

    public Jsonable get() {
        return writer -> writer.write(String.valueOf(this.privacyPolicyUpdateDetector.isPolicyUpdated()));
    }
}

