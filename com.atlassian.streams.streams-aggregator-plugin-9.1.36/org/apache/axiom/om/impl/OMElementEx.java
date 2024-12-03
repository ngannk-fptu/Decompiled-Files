/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;

public interface OMElementEx
extends OMElement,
OMNodeEx,
OMContainerEx {
    public OMNamespace addNamespaceDeclaration(String var1, String var2);
}

