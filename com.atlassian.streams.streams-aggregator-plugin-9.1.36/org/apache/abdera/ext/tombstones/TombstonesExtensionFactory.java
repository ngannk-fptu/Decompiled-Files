/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.ext.tombstones;

import org.apache.abdera.ext.tombstones.Tombstone;
import org.apache.abdera.ext.tombstones.TombstonesHelper;
import org.apache.abdera.util.AbstractExtensionFactory;

public final class TombstonesExtensionFactory
extends AbstractExtensionFactory {
    public TombstonesExtensionFactory() {
        super("http://purl.org/atompub/tombstones/1.0");
        this.addImpl(TombstonesHelper.DELETED_ENTRY, Tombstone.class);
    }
}

