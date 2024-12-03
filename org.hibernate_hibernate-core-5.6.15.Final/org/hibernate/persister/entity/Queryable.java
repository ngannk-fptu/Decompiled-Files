/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import org.hibernate.persister.entity.DiscriminatorMetadata;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.sql.SelectFragment;

public interface Queryable
extends Loadable,
PropertyMapping,
Joinable {
    @Override
    public boolean isAbstract();

    public boolean isExplicitPolymorphism();

    public String getMappedSuperclass();

    public String getDiscriminatorSQLValue();

    public String identifierSelectFragment(String var1, String var2);

    public String propertySelectFragment(String var1, String var2, boolean var3);

    public SelectFragment propertySelectFragmentFragment(String var1, String var2, boolean var3);

    @Override
    public String[] getIdentifierColumnNames();

    public boolean isMultiTable();

    public String[] getConstraintOrderedTableNameClosure();

    public String[][] getContraintOrderedTableKeyColumnClosure();

    public int getSubclassPropertyTableNumber(String var1);

    public Declarer getSubclassPropertyDeclarer(String var1);

    public String getSubclassTableName(int var1);

    public boolean isVersionPropertyInsertable();

    public String generateFilterConditionAlias(String var1);

    public DiscriminatorMetadata getTypeDiscriminatorMetadata();

    public String[][] getSubclassPropertyFormulaTemplateClosure();

    public static class Declarer {
        public static final Declarer CLASS = new Declarer("class");
        public static final Declarer SUBCLASS = new Declarer("subclass");
        public static final Declarer SUPERCLASS = new Declarer("superclass");
        private final String name;

        public Declarer(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

