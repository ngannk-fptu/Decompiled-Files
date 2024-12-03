/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;

public interface OMSourcedElement
extends OMElement {
    public boolean isExpanded();

    public OMDataSource getDataSource();

    public OMDataSource setDataSource(OMDataSource var1);

    public Object getObject(Class var1);
}

