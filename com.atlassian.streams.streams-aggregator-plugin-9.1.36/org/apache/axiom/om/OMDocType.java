/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import org.apache.axiom.om.OMNode;

public interface OMDocType
extends OMNode {
    public String getRootName();

    public String getPublicId();

    public String getSystemId();

    public String getInternalSubset();
}

