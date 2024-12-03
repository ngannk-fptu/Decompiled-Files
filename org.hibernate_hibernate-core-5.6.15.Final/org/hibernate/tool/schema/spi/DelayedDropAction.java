/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.service.ServiceRegistry;

public interface DelayedDropAction {
    public void perform(ServiceRegistry var1);
}

