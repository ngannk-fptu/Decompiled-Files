/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.factory.spi;

import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.service.Service;

public interface MutableIdentifierGeneratorFactory
extends IdentifierGeneratorFactory,
Service {
    public void register(String var1, Class var2);
}

