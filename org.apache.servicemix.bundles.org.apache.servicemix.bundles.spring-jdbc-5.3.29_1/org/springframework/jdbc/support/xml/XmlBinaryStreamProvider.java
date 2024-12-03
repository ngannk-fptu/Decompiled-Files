/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.xml;

import java.io.IOException;
import java.io.OutputStream;

public interface XmlBinaryStreamProvider {
    public void provideXml(OutputStream var1) throws IOException;
}

