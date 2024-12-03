/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 */
package com.atlassian.confluence.setup.bandana;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.setup.bandana.BandanaSerializer;
import com.atlassian.confluence.setup.bandana.BandanaSerializerFactory;

public class BandanaPersisterSupport {
    private BandanaSerializerFactory serializerFactory;

    public BandanaSerializer getSerializer(BandanaContext context) {
        if (context instanceof BandanaSerializerFactory) {
            return ((BandanaSerializerFactory)context).getSerializer();
        }
        return this.serializerFactory.getSerializer();
    }

    public void setSerializerFactory(BandanaSerializerFactory serializerFactory) {
        this.serializerFactory = serializerFactory;
    }
}

