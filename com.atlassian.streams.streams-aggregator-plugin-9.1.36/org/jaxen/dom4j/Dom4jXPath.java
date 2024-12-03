/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.dom4j;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.dom4j.DocumentNavigator;

public class Dom4jXPath
extends BaseXPath {
    private static final long serialVersionUID = -75510941087659775L;

    public Dom4jXPath(String xpathExpr) throws JaxenException {
        super(xpathExpr, DocumentNavigator.getInstance());
    }
}

