/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader;

import org.hibernate.persister.entity.Loadable;

public interface EntityAliases {
    public String[] getSuffixedKeyAliases();

    public String getSuffixedDiscriminatorAlias();

    public String[] getSuffixedVersionAliases();

    public String[][] getSuffixedPropertyAliases();

    public String[][] getSuffixedPropertyAliases(Loadable var1);

    public String getRowIdAlias();

    public String getSuffix();
}

