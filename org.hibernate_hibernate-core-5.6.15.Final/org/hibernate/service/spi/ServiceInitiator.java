/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

import org.hibernate.service.Service;

public interface ServiceInitiator<R extends Service> {
    public Class<R> getServiceInitiated();
}

