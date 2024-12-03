/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class PreferredLoaderObjectInputStream
extends ObjectInputStream {
    private final ClassLoader loader;

    public PreferredLoaderObjectInputStream(InputStream in, ClassLoader loader) throws IOException {
        super(in);
        this.loader = loader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        return Class.forName(desc.getName(), false, this.loader);
    }
}

