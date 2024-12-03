/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import org.hibernate.persister.entity.Loadable;
import org.hibernate.type.Type;

public interface SQLLoadable
extends Loadable {
    public String[] getSubclassPropertyColumnAliases(String var1, String var2);

    public String[] getSubclassPropertyColumnNames(String var1);

    public String selectFragment(String var1, String var2);

    public Type getType();
}

