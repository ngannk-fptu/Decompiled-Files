/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.cfg.Environment;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public final class BytecodeProviderInitiator
implements StandardServiceInitiator<BytecodeProvider> {
    public static final StandardServiceInitiator<BytecodeProvider> INSTANCE = new BytecodeProviderInitiator();

    @Override
    public BytecodeProvider initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return Environment.getBytecodeProvider();
    }

    @Override
    public Class<BytecodeProvider> getServiceInitiated() {
        return BytecodeProvider.class;
    }
}

