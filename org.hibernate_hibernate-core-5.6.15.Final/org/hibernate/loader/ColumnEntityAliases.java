/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader;

import java.util.Map;
import org.hibernate.loader.DefaultEntityAliases;
import org.hibernate.persister.entity.Loadable;

public class ColumnEntityAliases
extends DefaultEntityAliases {
    public ColumnEntityAliases(Map returnProperties, Loadable persister, String suffix) {
        super(returnProperties, persister, suffix);
    }

    @Override
    protected String[] getIdentifierAliases(Loadable persister, String suffix) {
        return persister.getIdentifierColumnNames();
    }

    @Override
    protected String getDiscriminatorAlias(Loadable persister, String suffix) {
        return persister.getDiscriminatorColumnName();
    }

    @Override
    protected String[] getPropertyAliases(Loadable persister, int j) {
        String[] propertyColumnNames = persister.getPropertyColumnNames(j);
        if (propertyColumnNames.length == 1 && propertyColumnNames[0] == null) {
            return new String[]{persister.getPropertyNames()[j]};
        }
        return propertyColumnNames;
    }
}

