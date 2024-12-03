/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.sling.commons.classloader.ClassLoaderWriter
 *  org.apache.sling.commons.compiler.JavaCompiler
 */
package org.apache.sling.scripting.jsp.jasper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.sling.commons.classloader.ClassLoaderWriter;
import org.apache.sling.commons.compiler.JavaCompiler;

public interface IOProvider {
    public OutputStream getOutputStream(String var1) throws IOException;

    public InputStream getInputStream(String var1) throws FileNotFoundException, IOException;

    public boolean delete(String var1);

    public boolean rename(String var1, String var2);

    public boolean mkdirs(String var1);

    public long lastModified(String var1);

    public ClassLoader getClassLoader();

    public JavaCompiler getJavaCompiler();

    public ClassLoaderWriter getClassLoaderWriter();
}

