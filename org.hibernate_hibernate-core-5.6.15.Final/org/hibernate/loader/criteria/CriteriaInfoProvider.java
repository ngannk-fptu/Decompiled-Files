/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.criteria;

import java.io.Serializable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.Type;

interface CriteriaInfoProvider {
    public String getName();

    public Serializable[] getSpaces();

    public PropertyMapping getPropertyMapping();

    public Type getType(String var1);
}

