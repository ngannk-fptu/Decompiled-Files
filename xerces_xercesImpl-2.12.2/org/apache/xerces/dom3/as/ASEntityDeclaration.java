/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASObject;

public interface ASEntityDeclaration
extends ASObject {
    public static final short INTERNAL_ENTITY = 1;
    public static final short EXTERNAL_ENTITY = 2;

    public short getEntityType();

    public void setEntityType(short var1);

    public String getEntityValue();

    public void setEntityValue(String var1);

    public String getSystemId();

    public void setSystemId(String var1);

    public String getPublicId();

    public void setPublicId(String var1);
}

