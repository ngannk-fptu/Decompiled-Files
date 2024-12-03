/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader;

import java.util.Collections;
import java.util.Map;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.EntityAliases;
import org.hibernate.persister.entity.Loadable;

public class DefaultEntityAliases
implements EntityAliases {
    private static final String[][] EMPTY_ARRAY_OF_ARRAY_OF_STRINGS = new String[0][];
    private final String[] suffixedKeyColumns;
    private final String[] suffixedVersionColumn;
    private final String[][] suffixedPropertyColumns;
    private final String suffixedDiscriminatorColumn;
    private final String suffix;
    private final String rowIdAlias;
    private final Map userProvidedAliases;

    public DefaultEntityAliases(Map userProvidedAliases, Loadable persister, String suffix) {
        this(userProvidedAliases, persister, suffix, false);
    }

    public DefaultEntityAliases(Loadable persister, String suffix) {
        this(Collections.EMPTY_MAP, persister, suffix, true);
    }

    private DefaultEntityAliases(Map userProvidedAliases, Loadable persister, String suffix, boolean interns) {
        if (interns) {
            this.suffix = suffix.intern();
            this.rowIdAlias = ("rowid_" + suffix).intern();
        } else {
            this.suffix = suffix;
            this.rowIdAlias = "rowid_" + suffix;
        }
        this.userProvidedAliases = userProvidedAliases;
        this.suffixedKeyColumns = this.determineKeyAlias(persister, suffix);
        this.suffixedPropertyColumns = this.determinePropertyAliases(persister);
        this.suffixedDiscriminatorColumn = this.determineDiscriminatorAlias(persister, suffix);
        this.suffixedVersionColumn = this.determineVersionAlias(persister);
    }

    private String[] determineKeyAlias(Loadable persister, String suffix) {
        String[] keyColumnsCandidates = this.getUserProvidedAliases(persister.getIdentifierPropertyName(), null);
        String[] aliases = keyColumnsCandidates == null ? this.getUserProvidedAliases("id", this.getIdentifierAliases(persister, suffix)) : keyColumnsCandidates;
        return StringHelper.unquote(aliases, persister.getFactory().getDialect());
    }

    private String[][] determinePropertyAliases(Loadable persister) {
        return this.getSuffixedPropertyAliases(persister);
    }

    private String determineDiscriminatorAlias(Loadable persister, String suffix) {
        String alias = this.getUserProvidedAlias("class", this.getDiscriminatorAlias(persister, suffix));
        return StringHelper.unquote(alias, persister.getFactory().getDialect());
    }

    private String[] determineVersionAlias(Loadable persister) {
        return persister.isVersioned() ? this.suffixedPropertyColumns[persister.getVersionProperty()] : null;
    }

    protected String getDiscriminatorAlias(Loadable persister, String suffix) {
        return persister.getDiscriminatorAlias(suffix);
    }

    protected String[] getIdentifierAliases(Loadable persister, String suffix) {
        return persister.getIdentifierAliases(suffix);
    }

    protected String[] getPropertyAliases(Loadable persister, int j) {
        return persister.getPropertyAliases(this.suffix, j);
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

    @Override
    public String[][] getSuffixedPropertyAliases(Loadable persister) {
        String[][] suffixedPropertyAliases;
        String[] propertyNames = persister.getPropertyNames();
        int size = propertyNames.length;
        if (size > 0) {
            suffixedPropertyAliases = new String[size][];
            Dialect dialect = persister.getFactory().getDialect();
            for (int j = 0; j < size; ++j) {
                suffixedPropertyAliases[j] = this.getUserProvidedAliases(propertyNames[j], this.getPropertyAliases(persister, j));
                suffixedPropertyAliases[j] = StringHelper.unquote(suffixedPropertyAliases[j], dialect);
            }
        } else {
            suffixedPropertyAliases = EMPTY_ARRAY_OF_ARRAY_OF_STRINGS;
        }
        return suffixedPropertyAliases;
    }

    @Override
    public String[] getSuffixedVersionAliases() {
        return this.suffixedVersionColumn;
    }

    @Override
    public String[][] getSuffixedPropertyAliases() {
        return this.suffixedPropertyColumns;
    }

    @Override
    public String getSuffixedDiscriminatorAlias() {
        return this.suffixedDiscriminatorColumn;
    }

    @Override
    public String[] getSuffixedKeyAliases() {
        return this.suffixedKeyColumns;
    }

    @Override
    public String getRowIdAlias() {
        return this.rowIdAlias;
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }
}

