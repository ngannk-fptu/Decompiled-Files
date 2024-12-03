/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.bandana;

import com.atlassian.confluence.setup.bandana.BandanaSerializerFactory;
import com.atlassian.confluence.setup.bandana.XStreamBandanaSerializer;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager;

public class XStreamBandanaSerializerFactory
implements BandanaSerializerFactory {
    private final ConfluenceXStreamManager xStreamManager;

    public XStreamBandanaSerializerFactory(ConfluenceXStreamManager xStreamManager) {
        this.xStreamManager = xStreamManager;
    }

    @Override
    public XStreamBandanaSerializer getSerializer() {
        return new XStreamBandanaSerializer(this.xStreamManager.getConfluenceXStream());
    }
}

