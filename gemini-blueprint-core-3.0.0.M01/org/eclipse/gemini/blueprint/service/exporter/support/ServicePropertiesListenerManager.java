/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.exporter.support;

import org.eclipse.gemini.blueprint.service.exporter.support.ServicePropertiesChangeListener;

public interface ServicePropertiesListenerManager {
    public void addListener(ServicePropertiesChangeListener var1);

    public void removeListener(ServicePropertiesChangeListener var1);
}

