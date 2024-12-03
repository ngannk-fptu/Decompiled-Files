/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public interface ObjectInputOutputStreamFactory {
    public ObjectOutputStream createObjectOutputStream(OutputStream var1) throws IOException;

    public ObjectInputStream createObjectInputStream(InputStream var1, ClassLoader var2) throws IOException;
}

