/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.glassfish.ha.store.spi.ObjectInputOutputStreamFactory;
import org.glassfish.ha.store.spi.ObjectInputStreamWithLoader;

public class ObjectInputOutputStreamFactoryRegistry {
    private static ObjectInputOutputStreamFactory _factory = new DefaultObjectInputOutputStreamFactory();

    public static ObjectInputOutputStreamFactory getObjectInputOutputStreamFactory() {
        return _factory;
    }

    private static class DefaultObjectInputOutputStreamFactory
    implements ObjectInputOutputStreamFactory {
        private DefaultObjectInputOutputStreamFactory() {
        }

        @Override
        public ObjectOutputStream createObjectOutputStream(OutputStream os) throws IOException {
            return new ObjectOutputStream(os);
        }

        @Override
        public ObjectInputStream createObjectInputStream(InputStream is, ClassLoader loader) throws IOException {
            return new ObjectInputStreamWithLoader(is, loader);
        }
    }
}

