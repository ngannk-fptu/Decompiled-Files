/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.identifier;

import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.identifier.AbstractIdFactory;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;

public final class IdFactoryImpl
extends AbstractIdFactory {
    private static IdFactory INSTANCE;

    private IdFactoryImpl() {
    }

    public static IdFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IdFactoryImpl();
        }
        return INSTANCE;
    }

    @Override
    protected PathFactory getPathFactory() {
        return PathFactoryImpl.getInstance();
    }
}

