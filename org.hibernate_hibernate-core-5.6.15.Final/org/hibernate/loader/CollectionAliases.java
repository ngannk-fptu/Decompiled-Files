/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader;

public interface CollectionAliases {
    public String[] getSuffixedKeyAliases();

    public String[] getSuffixedIndexAliases();

    public String[] getSuffixedElementAliases();

    public String getSuffixedIdentifierAlias();

    public String getSuffix();
}

