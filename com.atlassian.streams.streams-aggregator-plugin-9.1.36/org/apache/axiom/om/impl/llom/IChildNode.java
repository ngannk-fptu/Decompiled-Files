/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.llom.IParentNode;

public interface IChildNode
extends OMNodeEx {
    public IParentNode getIParentNode();
}

