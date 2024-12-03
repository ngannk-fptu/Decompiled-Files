/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.w3c.dom.DOMImplementation;

public class DeferredDOMImplementationImpl
extends DOMImplementationImpl {
    static final DeferredDOMImplementationImpl singleton = new DeferredDOMImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }
}

