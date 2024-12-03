/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.ls;

import java.io.OutputStream;
import java.io.Writer;

public interface LSOutput {
    public Writer getCharacterStream();

    public void setCharacterStream(Writer var1);

    public OutputStream getByteStream();

    public void setByteStream(OutputStream var1);

    public String getSystemId();

    public void setSystemId(String var1);

    public String getEncoding();

    public void setEncoding(String var1);
}

