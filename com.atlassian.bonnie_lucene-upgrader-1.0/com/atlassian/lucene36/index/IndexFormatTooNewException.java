/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.store.DataInput;

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

