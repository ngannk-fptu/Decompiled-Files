/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.factory.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class MutableIdentifierGeneratorFactoryInitiator
implements StandardServiceInitiator<MutableIdentifierGeneratorFactory> {
    public static final MutableIdentifierGeneratorFactoryInitiator INSTANCE = new MutableIdentifierGeneratorFactoryInitiator();

    @Override
    public Class<MutableIdentifierGeneratorFactory> getServiceInitiated() {
        return MutableIdentifierGeneratorFactory.class;
    }

    @Override
    public MutableIdentifierGeneratorFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new DefaultIdentifierGeneratorFactory();
    }
}

