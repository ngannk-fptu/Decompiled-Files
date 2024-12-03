/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.spi;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.service.Service;

public interface DialectResolver
extends Service {
    public Dialect resolveDialect(DialectResolutionInfo var1);
}

