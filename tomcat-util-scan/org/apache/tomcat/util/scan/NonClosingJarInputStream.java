/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;

public class NonClosingJarInputStream
extends JarInputStream {
    public NonClosingJarInputStream(InputStream in, boolean verify) throws IOException {
        super(in, verify);
    }

    public NonClosingJarInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    public void close() throws IOException {
    }

    public void reallyClose() throws IOException {
        super.close();
    }
}

