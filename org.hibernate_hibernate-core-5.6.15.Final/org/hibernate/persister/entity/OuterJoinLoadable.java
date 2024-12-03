/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import org.hibernate.FetchMode;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public interface OuterJoinLoadable
extends Loadable,
Joinable {
    public String selectFragment(String var1, String var2);

    public int countSubclassProperties();

    public FetchMode getFetchMode(int var1);

    public CascadeStyle getCascadeStyle(int var1);

    public boolean isDefinedOnSubclass(int var1);

    public Type getSubclassPropertyType(int var1);

    public String getSubclassPropertyName(int var1);

    public boolean isSubclassPropertyNullable(int var1);

    public String[] getSubclassPropertyColumnNames(int var1);

    public String getSubclassPropertyTableName(int var1);

    public String[] toColumns(String var1, int var2);

    public String fromTableFragment(String var1);

    public String[] getPropertyColumnNames(String var1);

    public String getPropertyTableName(String var1);

    public EntityType getEntityType();
}

