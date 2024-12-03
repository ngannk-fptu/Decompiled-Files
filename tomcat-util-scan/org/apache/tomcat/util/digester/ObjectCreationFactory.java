/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.Attributes;

public interface ObjectCreationFactory {
    public Object createObject(Attributes var1) throws Exception;

    public Digester getDigester();

    public void setDigester(Digester var1);
}

