/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Appendable;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.ResourceDecorator;

public abstract class ContentTransformingResource
extends ResourceDecorator {
    private static final int BUFFER_SIZE = 8192;

    protected ContentTransformingResource() {
    }

    protected ContentTransformingResource(ResourceCollection other) {
        super(other);
    }

    @Override
    public long getSize() {
        if (this.isExists()) {
            long l;
            block10: {
                InputStream in = this.getInputStream();
                try {
                    int readNow;
                    byte[] buf = new byte[8192];
                    int size = 0;
                    while ((readNow = in.read(buf, 0, buf.length)) > 0) {
                        size += readNow;
                    }
                    l = size;
                    if (in == null) break block10;
                }
                catch (Throwable throwable) {
                    try {
                        if (in != null) {
                            try {
                                in.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException ex) {
                        throw new BuildException("caught exception while reading " + this.getName(), ex);
                    }
                }
                in.close();
            }
            return l;
        }
        return 0L;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream in = this.getResource().getInputStream();
        if (in != null) {
            in = this.wrapStream(in);
        }
        return in;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        OutputStream out = this.getResource().getOutputStream();
        if (out != null) {
            out = this.wrapStream(out);
        }
        return out;
    }

    @Override
    public <T> T as(Class<T> clazz) {
        if (Appendable.class.isAssignableFrom(clazz)) {
            Appendable a;
            if (this.isAppendSupported() && (a = this.getResource().as(Appendable.class)) != null) {
                return clazz.cast(() -> {
                    OutputStream out = a.getAppendOutputStream();
                    return out == null ? null : this.wrapStream(out);
                });
            }
            return null;
        }
        return FileProvider.class.isAssignableFrom(clazz) ? null : (T)this.getResource().as(clazz);
    }

    protected boolean isAppendSupported() {
        return false;
    }

    protected abstract InputStream wrapStream(InputStream var1) throws IOException;

    protected abstract OutputStream wrapStream(OutputStream var1) throws IOException;
}

