/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.tika.config.Field;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.filter.MetadataFilter;
import org.apache.tika.mime.MediaType;

public class ClearByMimeMetadataFilter
extends MetadataFilter {
    private final Set<String> mimes;

    public ClearByMimeMetadataFilter() {
        this(new HashSet<String>());
    }

    public ClearByMimeMetadataFilter(Set<String> mimes) {
        this.mimes = mimes;
    }

    @Override
    public void filter(Metadata metadata) throws TikaException {
        String mimeString = metadata.get("Content-Type");
        if (mimeString == null) {
            return;
        }
        MediaType mt = MediaType.parse(mimeString);
        if (mt != null) {
            mimeString = mt.getBaseType().toString();
        }
        if (this.mimes.contains(mimeString)) {
            for (String n : metadata.names()) {
                metadata.remove(n);
            }
        }
    }

    @Field
    public void setMimes(List<String> mimes) {
        this.mimes.addAll(mimes);
    }
}

