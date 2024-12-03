/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  io.atlassian.fugue.Pair
 */
package com.atlassian.confluence.impl.core.persistence.hibernate.schema;

import com.atlassian.confluence.upgrade.ddl.AddUniqueConstraintCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableExecutor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.atlassian.fugue.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public class RelationConstraintsSchemaHelper {
    private static final Map<String, Pair<String, List<String>>> RELATION_CONSTRAINTS = ImmutableMap.of((Object)"USERCONTENT_RELATION", (Object)Pair.pair((Object)"u2c_relation_unique", (Object)ImmutableList.of((Object)"TARGETCONTENTID", (Object)"SOURCEUSER", (Object)"RELATIONNAME")), (Object)"CONTENT_RELATION", (Object)Pair.pair((Object)"c2c_relation_unique", (Object)ImmutableList.of((Object)"TARGETCONTENTID", (Object)"SOURCECONTENTID", (Object)"RELATIONNAME")), (Object)"USER_RELATION", (Object)Pair.pair((Object)"u2u_relation_unique", (Object)ImmutableList.of((Object)"SOURCEUSER", (Object)"TARGETUSER", (Object)"RELATIONNAME")));

    static List<String> getRelationUniqueConstraintSqlStatements(AlterTableExecutor executor) {
        return RELATION_CONSTRAINTS.entrySet().stream().map(entry -> {
            String tableName = (String)entry.getKey();
            Pair constraint = (Pair)entry.getValue();
            String constraintName = (String)constraint.left();
            List columns = (List)constraint.right();
            return executor.getAlterTableStatements(tableName, Lists.newArrayList((Object[])new AddUniqueConstraintCommand[]{new AddUniqueConstraintCommand(constraintName, columns)}));
        }).reduce(new ArrayList(), RelationConstraintsSchemaHelper.listConcat());
    }

    private static BinaryOperator<List<String>> listConcat() {
        return (a, b) -> {
            a.addAll(b);
            return a;
        };
    }
}

