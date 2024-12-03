/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import org.bouncycastle.est.CSRAttributesResponse;
import org.bouncycastle.est.Source;

public class CSRRequestResponse {
    private final CSRAttributesResponse attributesResponse;
    private final Source source;

    public CSRRequestResponse(CSRAttributesResponse attributesResponse, Source session) {
        this.attributesResponse = attributesResponse;
        this.source = session;
    }

    public boolean hasAttributesResponse() {
        return this.attributesResponse != null;
    }

    public CSRAttributesResponse getAttributesResponse() {
        if (this.attributesResponse == null) {
            throw new IllegalStateException("Response has no CSRAttributesResponse.");
        }
        return this.attributesResponse;
    }

    public Object getSession() {
        return this.source.getSession();
    }

    public Source getSource() {
        return this.source;
    }
}

