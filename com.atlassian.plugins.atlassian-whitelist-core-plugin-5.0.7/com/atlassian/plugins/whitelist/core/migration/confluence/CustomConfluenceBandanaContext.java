/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.bandana.BandanaSerializer
 *  com.atlassian.confluence.setup.bandana.BandanaSerializerFactory
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 */
package com.atlassian.plugins.whitelist.core.migration.confluence;

import com.atlassian.confluence.setup.bandana.BandanaSerializer;
import com.atlassian.confluence.setup.bandana.BandanaSerializerFactory;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugins.whitelist.core.migration.confluence.RawStringBandanaDeserializer;

public class CustomConfluenceBandanaContext
extends ConfluenceBandanaContext
implements BandanaSerializerFactory {
    private BandanaSerializer bandanaSerializer = new RawStringBandanaDeserializer();

    public BandanaSerializer getSerializer() {
        return this.bandanaSerializer;
    }
}

