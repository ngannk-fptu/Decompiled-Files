/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.xom;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.xom.DocumentNavigator;

public class XOMXPath
extends BaseXPath {
    private static final long serialVersionUID = -5332108546921857671L;

    public XOMXPath(String xpathExpr) throws JaxenException {
        super(xpathExpr, new DocumentNavigator());
    }
}

