/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.jdom;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.jdom.DocumentNavigator;

public class JDOMXPath
extends BaseXPath {
    private static final long serialVersionUID = 6426091824802286928L;

    public JDOMXPath(String xpathExpr) throws JaxenException {
        super(xpathExpr, DocumentNavigator.getInstance());
    }
}

