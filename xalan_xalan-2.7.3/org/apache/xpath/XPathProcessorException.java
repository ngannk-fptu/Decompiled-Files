/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath;

import org.apache.xpath.XPathException;

public class XPathProcessorException
extends XPathException {
    static final long serialVersionUID = 1215509418326642603L;

    public XPathProcessorException(String message) {
        super(message);
    }

    public XPathProcessorException(String message, Exception e) {
        super(message, e);
    }
}

