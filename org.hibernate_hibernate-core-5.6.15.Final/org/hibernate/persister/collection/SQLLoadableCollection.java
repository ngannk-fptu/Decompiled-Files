/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.collection;

import org.hibernate.persister.collection.QueryableCollection;

public interface SQLLoadableCollection
extends QueryableCollection {
    public String[] getCollectionPropertyColumnAliases(String var1, String var2);

    public String getIdentifierColumnName();
}

