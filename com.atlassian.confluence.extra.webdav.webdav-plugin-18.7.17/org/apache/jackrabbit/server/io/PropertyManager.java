/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyHandler;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.webdav.property.PropEntry;

public interface PropertyManager {
    public boolean exportProperties(PropertyExportContext var1, boolean var2) throws RepositoryException;

    public Map<? extends PropEntry, ?> alterProperties(PropertyImportContext var1, boolean var2) throws RepositoryException;

    public void addPropertyHandler(PropertyHandler var1);

    public PropertyHandler[] getPropertyHandlers();
}

