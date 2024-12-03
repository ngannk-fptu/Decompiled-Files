/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import java.util.Map;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.SQLLoadableCollection;

public class ColumnCollectionAliases
implements CollectionAliases {
    private final String[] keyAliases;
    private final String[] indexAliases;
    private final String[] elementAliases;
    private final String identifierAlias;
    private Map userProvidedAliases;

    public ColumnCollectionAliases(Map userProvidedAliases, SQLLoadableCollection persister) {
        this.userProvidedAliases = userProvidedAliases;
        this.keyAliases = this.getUserProvidedAliases("key", persister.getKeyColumnNames());
        this.indexAliases = this.getUserProvidedAliases("index", persister.getIndexColumnNames());
        this.elementAliases = this.getUserProvidedAliases("element", persister.getElementColumnNames());
        this.identifierAlias = this.getUserProvidedAlias("id", persister.getIdentifierColumnName());
    }

    @Override
    public String[] getSuffixedKeyAliases() {
        return this.keyAliases;
    }

    @Override
    public String[] getSuffixedIndexAliases() {
        return this.indexAliases;
    }

    @Override
    public String[] getSuffixedElementAliases() {
        return this.elementAliases;
    }

    @Override
    public String getSuffixedIdentifierAlias() {
        return this.identifierAlias;
    }

    @Override
    public String getSuffix() {
        return "";
    }

    public String toString() {
        return super.toString() + " [ suffixedKeyAliases=[" + this.join(this.keyAliases) + "], suffixedIndexAliases=[" + this.join(this.indexAliases) + "], suffixedElementAliases=[" + this.join(this.elementAliases) + "], suffixedIdentifierAlias=[" + this.identifierAlias + "]]";
    }

    private String join(String[] aliases) {
        if (aliases == null) {
            return null;
        }
        return String.join((CharSequence)", ", aliases);
    }

    private String[] getUserProvidedAliases(String propertyPath, String[] defaultAliases) {
        String[] result = (String[])this.userProvidedAliases.get(propertyPath);
        if (result == null) {
            return defaultAliases;
        }
        return result;
    }

    private String getUserProvidedAlias(String propertyPath, String defaultAlias) {
        String[] columns = (String[])this.userProvidedAliases.get(propertyPath);
        if (columns == null) {
            return defaultAlias;
        }
        return columns[0];
    }
}

