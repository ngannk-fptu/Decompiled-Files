/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.digester.Digester;
import org.xml.sax.Attributes;

public interface ObjectCreationFactory {
    public Object createObject(Attributes var1) throws Exception;

    public Digester getDigester();

    public void setDigester(Digester var1);
}

