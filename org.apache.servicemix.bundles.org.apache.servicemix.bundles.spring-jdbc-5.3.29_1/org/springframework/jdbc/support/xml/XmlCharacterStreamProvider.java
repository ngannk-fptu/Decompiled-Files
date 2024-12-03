/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.xml;

import java.io.IOException;
import java.io.Writer;

public interface XmlCharacterStreamProvider {
    public void provideXml(Writer var1) throws IOException;
}

