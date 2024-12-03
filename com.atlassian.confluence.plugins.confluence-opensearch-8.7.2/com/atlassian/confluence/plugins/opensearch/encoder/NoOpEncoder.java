/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.opensearch.encoder;

import com.atlassian.confluence.plugins.opensearch.encoder.Encoder;

public class NoOpEncoder
implements Encoder {
    @Override
    public String encode(String originalText) {
        return originalText;
    }
}

