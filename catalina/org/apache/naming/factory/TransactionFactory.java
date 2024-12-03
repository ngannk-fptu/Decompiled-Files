/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming.factory;

import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.TransactionRef;
import org.apache.naming.factory.FactoryBase;

public class TransactionFactory
extends FactoryBase {
    @Override
    protected boolean isReferenceTypeSupported(Object obj) {
        return obj instanceof TransactionRef;
    }

    @Override
    protected ObjectFactory getDefaultFactory(Reference ref) {
        return null;
    }

    @Override
    protected Object getLinked(Reference ref) {
        return null;
    }
}

