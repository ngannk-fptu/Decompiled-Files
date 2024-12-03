/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.Map;
import org.hibernate.mapping.MetaAttribute;

public interface MetaAttributable {
    public Map getMetaAttributes();

    public void setMetaAttributes(Map var1);

    public MetaAttribute getMetaAttribute(String var1);
}

