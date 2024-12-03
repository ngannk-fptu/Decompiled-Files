/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import org.apache.axiom.om.OMNode;

public interface OMProcessingInstruction
extends OMNode {
    public void setTarget(String var1);

    public String getTarget();

    public void setValue(String var1);

    public String getValue();
}

