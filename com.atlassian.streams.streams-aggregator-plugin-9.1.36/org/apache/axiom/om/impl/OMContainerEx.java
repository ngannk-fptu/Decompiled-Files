/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;

public interface OMContainerEx
extends OMContainer {
    public void setComplete(boolean var1);

    public void discarded();

    public void addChild(OMNode var1, boolean var2);
}

