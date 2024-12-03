/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.commons.logging.AbstractLogger;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;

public class NameFactoryLogger
extends AbstractLogger
implements NameFactory {
    private final NameFactory nameFactory;

    public NameFactoryLogger(NameFactory nameFactory, LogWriter writer) {
        super(writer);
        this.nameFactory = nameFactory;
    }

    public NameFactory getNameFactory() {
        return this.nameFactory;
    }

    @Override
    public Name create(final String namespaceURI, final String localName) {
        return (Name)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return NameFactoryLogger.this.nameFactory.create(namespaceURI, localName);
            }
        }, "create(String, String)", new Object[]{namespaceURI, localName});
    }

    @Override
    public Name create(final String nameString) {
        return (Name)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return NameFactoryLogger.this.nameFactory.create(nameString);
            }
        }, "create(String)", new Object[]{nameString});
    }
}

