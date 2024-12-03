/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.ASModelImpl;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom3.as.ASModel;
import org.apache.xerces.dom3.as.DOMASBuilder;
import org.apache.xerces.dom3.as.DOMASWriter;
import org.apache.xerces.dom3.as.DOMImplementationAS;
import org.apache.xerces.parsers.DOMASBuilderImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;

public class ASDOMImplementationImpl
extends DOMImplementationImpl
implements DOMImplementationAS {
    static final ASDOMImplementationImpl singleton = new ASDOMImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    @Override
    public ASModel createAS(boolean bl) {
        return new ASModelImpl(bl);
    }

    @Override
    public DOMASBuilder createDOMASBuilder() {
        return new DOMASBuilderImpl();
    }

    @Override
    public DOMASWriter createDOMASWriter() {
        String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException(9, string);
    }
}

