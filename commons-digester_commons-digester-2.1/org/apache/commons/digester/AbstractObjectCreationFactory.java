/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.xml.sax.Attributes;

public abstract class AbstractObjectCreationFactory
implements ObjectCreationFactory {
    protected Digester digester = null;

    public abstract Object createObject(Attributes var1) throws Exception;

    public Digester getDigester() {
        return this.digester;
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
    }
}

