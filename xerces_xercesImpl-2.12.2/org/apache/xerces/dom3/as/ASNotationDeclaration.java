/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASObject;

public interface ASNotationDeclaration
extends ASObject {
    public String getSystemId();

    public void setSystemId(String var1);

    public String getPublicId();

    public void setPublicId(String var1);
}

