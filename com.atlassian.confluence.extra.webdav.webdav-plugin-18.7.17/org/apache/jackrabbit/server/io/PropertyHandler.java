/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.webdav.property.PropEntry;

public interface PropertyHandler {
    public boolean canExport(PropertyExportContext var1, boolean var2);

    public boolean exportProperties(PropertyExportContext var1, boolean var2) throws RepositoryException;

    public boolean canImport(PropertyImportContext var1, boolean var2);

    public Map<? extends PropEntry, ?> importProperties(PropertyImportContext var1, boolean var2) throws RepositoryException;
}

