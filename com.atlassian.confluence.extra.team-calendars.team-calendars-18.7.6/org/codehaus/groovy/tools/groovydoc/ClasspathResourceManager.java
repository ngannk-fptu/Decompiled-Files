/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.tools.groovydoc.ResourceManager;

public class ClasspathResourceManager
implements ResourceManager {
    ClassLoader classLoader;

    public ClasspathResourceManager() {
        this.classLoader = this.getClass().getClassLoader();
    }

    public ClasspathResourceManager(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public InputStream getInputStream(String resourceName) {
        return this.classLoader.getResourceAsStream(resourceName);
    }

    @Override
    public Reader getReader(String resourceName) throws IOException {
        return IOGroovyMethods.newReader(this.getInputStream(resourceName));
    }
}

