/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.transform;

import org.jdom2.JDOMException;

public class XSLTransformException
extends JDOMException {
    private static final long serialVersionUID = 200L;

    public XSLTransformException() {
    }

    public XSLTransformException(String message) {
        super(message);
    }

    public XSLTransformException(String message, Exception cause) {
        super(message, cause);
    }
}

