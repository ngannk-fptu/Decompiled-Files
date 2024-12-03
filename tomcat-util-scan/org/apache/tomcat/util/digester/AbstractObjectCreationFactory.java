/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.ObjectCreationFactory;
import org.xml.sax.Attributes;

public abstract class AbstractObjectCreationFactory
implements ObjectCreationFactory {
    private Digester digester = null;

    @Override
    public abstract Object createObject(Attributes var1) throws Exception;

    @Override
    public Digester getDigester() {
        return this.digester;
    }

    @Override
    public void setDigester(Digester digester) {
        this.digester = digester;
    }
}

