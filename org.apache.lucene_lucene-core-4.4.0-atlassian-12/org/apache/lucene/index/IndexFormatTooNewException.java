/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;

public class IndexFormatTooNewException
extends CorruptIndexException {
    public IndexFormatTooNewException(String resourceDesc, int version, int minVersion, int maxVersion) {
        super("Format version is not supported (resource: " + resourceDesc + "): " + version + " (needs to be between " + minVersion + " and " + maxVersion + ")");
        assert (resourceDesc != null);
    }

    public IndexFormatTooNewException(DataInput in, int version, int minVersion, int maxVersion) {
        this(in.toString(), version, minVersion, maxVersion);
    }
}

