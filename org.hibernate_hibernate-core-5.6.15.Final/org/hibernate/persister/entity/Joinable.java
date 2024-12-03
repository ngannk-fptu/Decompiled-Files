/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.util.Map;
import java.util.Set;
import org.hibernate.MappingException;

public interface Joinable {
    public String getName();

    public String getTableName();

    public String selectFragment(Joinable var1, String var2, String var3, String var4, String var5, boolean var6);

    public String whereJoinFragment(String var1, boolean var2, boolean var3);

    public String whereJoinFragment(String var1, boolean var2, boolean var3, Set<String> var4);

    public String fromJoinFragment(String var1, boolean var2, boolean var3);

    public String fromJoinFragment(String var1, boolean var2, boolean var3, Set<String> var4);

    default public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations, Set<String> referencedTables) {
        return this.fromJoinFragment(alias, innerJoin, includeSubclasses, treatAsDeclarations);
    }

    public String[] getKeyColumnNames();

    public String filterFragment(String var1, Map var2) throws MappingException;

    public String filterFragment(String var1, Map var2, Set<String> var3) throws MappingException;

    public String oneToManyFilterFragment(String var1) throws MappingException;

    public String oneToManyFilterFragment(String var1, Set<String> var2);

    public boolean isCollection();

    public boolean consumesEntityAlias();

    public boolean consumesCollectionAlias();
}

