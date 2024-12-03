/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.llom.IParentNode;

public interface IContainer
extends OMContainerEx,
IParentNode {
    public void setFirstChild(OMNode var1);

    public void setLastChild(OMNode var1);

    public OMNode getLastKnownOMChild();
}

