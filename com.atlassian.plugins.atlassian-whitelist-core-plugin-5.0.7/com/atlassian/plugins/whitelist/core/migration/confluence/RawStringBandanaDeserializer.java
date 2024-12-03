/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.bandana.BandanaSerializer
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.plugins.whitelist.core.migration.confluence;

import com.atlassian.confluence.setup.bandana.BandanaSerializer;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.commons.io.IOUtils;

class RawStringBandanaDeserializer
implements BandanaSerializer {
    RawStringBandanaDeserializer() {
    }

    public void serialize(Object obj, Writer writer) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Object deserialize(Reader reader) throws IOException {
        return IOUtils.toString((Reader)reader);
    }
}

