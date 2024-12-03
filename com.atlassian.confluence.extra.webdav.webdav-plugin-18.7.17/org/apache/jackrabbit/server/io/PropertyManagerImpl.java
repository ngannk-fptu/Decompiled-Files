/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyHandler;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.server.io.PropertyManager;
import org.apache.jackrabbit.server.io.XmlHandler;
import org.apache.jackrabbit.server.io.ZipHandler;
import org.apache.jackrabbit.webdav.property.PropEntry;

public class PropertyManagerImpl
implements PropertyManager {
    private static PropertyManager DEFAULT_MANAGER;
    private final List<PropertyHandler> propertyHandlers = new ArrayList<PropertyHandler>();

    @Override
    public boolean exportProperties(PropertyExportContext context, boolean isCollection) throws RepositoryException {
        boolean success = false;
        PropertyHandler[] propertyHandlers = this.getPropertyHandlers();
        for (int i = 0; i < propertyHandlers.length && !success; ++i) {
            PropertyHandler ph = propertyHandlers[i];
            if (!ph.canExport(context, isCollection)) continue;
            success = ph.exportProperties(context, isCollection);
        }
        context.informCompleted(success);
        return success;
    }

    @Override
    public Map<? extends PropEntry, ?> alterProperties(PropertyImportContext context, boolean isCollection) throws RepositoryException {
        Map<PropEntry, ?> failures = null;
        for (PropertyHandler ph : this.getPropertyHandlers()) {
            if (!ph.canImport(context, isCollection)) continue;
            failures = ph.importProperties(context, isCollection);
            break;
        }
        if (failures == null) {
            throw new RepositoryException("Unable to alter properties: No matching handler found.");
        }
        context.informCompleted(failures.isEmpty());
        return failures;
    }

    @Override
    public void addPropertyHandler(PropertyHandler propertyHandler) {
        if (propertyHandler == null) {
            throw new IllegalArgumentException("'null' is not a valid IOHandler.");
        }
        this.propertyHandlers.add(propertyHandler);
    }

    @Override
    public PropertyHandler[] getPropertyHandlers() {
        return this.propertyHandlers.toArray(new PropertyHandler[this.propertyHandlers.size()]);
    }

    public static PropertyManager getDefaultManager() {
        if (DEFAULT_MANAGER == null) {
            PropertyManagerImpl manager = new PropertyManagerImpl();
            manager.addPropertyHandler(new ZipHandler());
            manager.addPropertyHandler(new XmlHandler());
            manager.addPropertyHandler(new DefaultHandler());
            DEFAULT_MANAGER = manager;
        }
        return DEFAULT_MANAGER;
    }
}

