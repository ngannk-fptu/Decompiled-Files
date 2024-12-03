/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.exporter.support;

import java.util.EventObject;
import java.util.Map;

public class ServicePropertiesChangeEvent
extends EventObject {
    public ServicePropertiesChangeEvent(Map<?, ?> properties) {
        super(properties);
    }

    public Map<?, ?> getServiceProperties() {
        return (Map)this.getSource();
    }
}

