/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.server.RMIClassLoader;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.lang.Nullable;

public class CodebaseAwareObjectInputStream
extends ConfigurableObjectInputStream {
    private final String codebaseUrl;

    public CodebaseAwareObjectInputStream(InputStream in, String codebaseUrl) throws IOException {
        this(in, null, codebaseUrl);
    }

    public CodebaseAwareObjectInputStream(InputStream in, @Nullable ClassLoader classLoader, String codebaseUrl) throws IOException {
        super(in, classLoader);
        this.codebaseUrl = codebaseUrl;
    }

    public CodebaseAwareObjectInputStream(InputStream in, @Nullable ClassLoader classLoader, boolean acceptProxyClasses) throws IOException {
        super(in, classLoader, acceptProxyClasses);
        this.codebaseUrl = null;
    }

    @Override
    protected Class<?> resolveFallbackIfPossible(String className, ClassNotFoundException ex) throws IOException, ClassNotFoundException {
        if (this.codebaseUrl == null) {
            throw ex;
        }
        return RMIClassLoader.loadClass(this.codebaseUrl, className);
    }

    @Override
    protected ClassLoader getFallbackClassLoader() throws IOException {
        return RMIClassLoader.getClassLoader(this.codebaseUrl);
    }
}

