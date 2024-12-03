/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.exporter.support;

import java.util.EventListener;
import org.eclipse.gemini.blueprint.service.exporter.support.ServicePropertiesChangeEvent;

public interface ServicePropertiesChangeListener
extends EventListener {
    public void propertiesChange(ServicePropertiesChangeEvent var1);
}

