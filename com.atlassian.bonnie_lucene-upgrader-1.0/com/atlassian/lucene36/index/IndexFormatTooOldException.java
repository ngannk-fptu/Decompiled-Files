/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.store.DataInput;

public class IndexFormatTooOldException
extends CorruptIndexException {
    public IndexFormatTooOldException(String resourceDesc, String version) {
        super("Format version is not supported (resource: " + resourceDesc + "): " + version + ". This version of Lucene only supports indexes created with release 1.9 and later.");
        assert (resourceDesc != null);
    }

    public IndexFormatTooOldException(DataInput in, String version) {
        this(in.toString(), version);
    }

    public IndexFormatTooOldException(String resourceDesc, int version, int minVersion, int maxVersion) {
        super("Format version is not supported (resource: " + resourceDesc + "): " + version + " (needs to be between " + minVersion + " and " + maxVersion + "). This version of Lucene only supports indexes created with release 1.9 and later.");
        assert (resourceDesc != null);
    }

    public IndexFormatTooOldException(DataInput in, int version, int minVersion, int maxVersion) {
        this(in.toString(), version, minVersion, maxVersion);
    }
}

