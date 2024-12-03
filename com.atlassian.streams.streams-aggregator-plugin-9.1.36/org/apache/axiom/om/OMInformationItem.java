/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMFactory;

public interface OMInformationItem {
    public OMFactory getOMFactory();

    public OMInformationItem clone(OMCloneOptions var1);
}

