/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import org.apache.axiom.om.OMDataSource;

public interface QNameAwareOMDataSource
extends OMDataSource {
    public String getLocalName();

    public String getNamespaceURI();

    public String getPrefix();
}

