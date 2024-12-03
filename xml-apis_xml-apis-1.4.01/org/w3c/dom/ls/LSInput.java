/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.ls;

import java.io.InputStream;
import java.io.Reader;

public interface LSInput {
    public Reader getCharacterStream();

    public void setCharacterStream(Reader var1);

    public InputStream getByteStream();

    public void setByteStream(InputStream var1);

    public String getStringData();

    public void setStringData(String var1);

    public String getSystemId();

    public void setSystemId(String var1);

    public String getPublicId();

    public void setPublicId(String var1);

    public String getBaseURI();

    public void setBaseURI(String var1);

    public String getEncoding();

    public void setEncoding(String var1);

    public boolean getCertifiedText();

    public void setCertifiedText(boolean var1);
}

