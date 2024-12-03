/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.util.List;
import javax.jcr.Item;
import org.apache.jackrabbit.server.io.IOContext;
import org.apache.jackrabbit.webdav.property.PropEntry;

public interface PropertyImportContext
extends IOContext {
    public Item getImportRoot();

    public List<? extends PropEntry> getChangeList();
}

