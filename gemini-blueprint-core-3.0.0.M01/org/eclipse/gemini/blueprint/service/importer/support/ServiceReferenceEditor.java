/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import java.beans.PropertyEditorSupport;
import org.eclipse.gemini.blueprint.service.importer.ImportedOsgiServiceProxy;
import org.osgi.framework.ServiceReference;

public class ServiceReferenceEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        throw new IllegalArgumentException("this property editor works only with " + ImportedOsgiServiceProxy.class.getName());
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            super.setValue(null);
            return;
        }
        if (value instanceof ImportedOsgiServiceProxy) {
            ImportedOsgiServiceProxy serviceProxy = (ImportedOsgiServiceProxy)value;
            super.setValue(serviceProxy.getServiceReference());
            return;
        }
        if (value instanceof ServiceReference) {
            super.setValue(value);
            return;
        }
        throw new IllegalArgumentException("Expected a service of type " + ImportedOsgiServiceProxy.class.getName() + " but received type " + value.getClass());
    }

    @Override
    public String getAsText() {
        return null;
    }
}

