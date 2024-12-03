/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.xmp;

import com.twelvemonkeys.imageio.metadata.AbstractDirectory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.xmp.XMP;
import java.util.Collection;

final class RDFDescription
extends AbstractDirectory {
    private final String namespace;

    public RDFDescription(Collection<? extends Entry> collection) {
        this(null, collection);
    }

    public RDFDescription(String string, Collection<? extends Entry> collection) {
        super(collection);
        this.namespace = string;
    }

    @Override
    public String toString() {
        return this.namespace != null ? super.toString().replaceAll("^RDFDescription\\[", String.format("%s[%s|%s, ", this.getClass().getSimpleName(), XMP.DEFAULT_NS_MAPPING.get(this.namespace), this.namespace)) : super.toString();
    }
}

