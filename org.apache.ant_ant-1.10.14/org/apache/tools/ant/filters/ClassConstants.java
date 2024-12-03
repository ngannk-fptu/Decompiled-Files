/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.filters.ChainableReader;

public final class ClassConstants
extends BaseFilterReader
implements ChainableReader {
    private String queuedData = null;
    private static final String JAVA_CLASS_HELPER = "org.apache.tools.ant.filters.util.JavaClassHelper";

    public ClassConstants() {
    }

    public ClassConstants(Reader in) {
        super(in);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public int read() throws IOException {
        int ch = -1;
        if (this.queuedData != null && this.queuedData.isEmpty()) {
            this.queuedData = null;
        }
        if (this.queuedData == null) {
            String clazz = this.readFully();
            if (clazz == null) return -1;
            if (clazz.isEmpty()) {
                return -1;
            }
            byte[] bytes = clazz.getBytes(StandardCharsets.ISO_8859_1);
            try {
                Class<?> javaClassHelper = Class.forName(JAVA_CLASS_HELPER);
                if (javaClassHelper == null) return ch;
                Method getConstants = javaClassHelper.getMethod("getConstants", byte[].class);
                StringBuffer sb = (StringBuffer)getConstants.invoke(null, new Object[]{bytes});
                if (sb.length() <= 0) return ch;
                this.queuedData = sb.toString();
                return this.read();
            }
            catch (NoClassDefFoundError | RuntimeException ex) {
                throw ex;
            }
            catch (InvocationTargetException ex) {
                Throwable t = ex.getTargetException();
                if (t instanceof NoClassDefFoundError) {
                    throw (NoClassDefFoundError)t;
                }
                if (!(t instanceof RuntimeException)) throw new BuildException(t);
                throw (RuntimeException)t;
            }
            catch (Exception ex) {
                throw new BuildException(ex);
            }
        }
        ch = this.queuedData.charAt(0);
        this.queuedData = this.queuedData.substring(1);
        if (!this.queuedData.isEmpty()) return ch;
        this.queuedData = null;
        return ch;
    }

    @Override
    public Reader chain(Reader rdr) {
        return new ClassConstants(rdr);
    }
}

