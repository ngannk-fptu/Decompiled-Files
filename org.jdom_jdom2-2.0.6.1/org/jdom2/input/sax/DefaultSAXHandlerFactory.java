/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import org.jdom2.JDOMFactory;
import org.jdom2.input.sax.SAXHandler;
import org.jdom2.input.sax.SAXHandlerFactory;

public final class DefaultSAXHandlerFactory
implements SAXHandlerFactory {
    public SAXHandler createSAXHandler(JDOMFactory factory) {
        return new DefaultSAXHandler(factory);
    }

    private static final class DefaultSAXHandler
    extends SAXHandler {
        public DefaultSAXHandler(JDOMFactory factory) {
            super(factory);
        }
    }
}

