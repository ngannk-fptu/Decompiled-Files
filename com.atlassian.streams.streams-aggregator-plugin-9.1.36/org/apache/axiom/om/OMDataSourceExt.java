/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.axiom.om.OMDataSource;

public interface OMDataSourceExt
extends OMDataSource {
    public static final String LOSSY_PREFIX = "lossyPrefix";

    public Object getObject();

    public boolean isDestructiveRead();

    public boolean isDestructiveWrite();

    public InputStream getXMLInputStream(String var1) throws UnsupportedEncodingException;

    public byte[] getXMLBytes(String var1) throws UnsupportedEncodingException;

    public void close();

    public OMDataSourceExt copy();

    public boolean hasProperty(String var1);

    public Object getProperty(String var1);

    public Object setProperty(String var1, Object var2);
}

