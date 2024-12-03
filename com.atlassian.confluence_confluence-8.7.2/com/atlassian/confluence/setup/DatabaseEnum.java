/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup;

import java.lang.invoke.LambdaMetafactory;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum DatabaseEnum {
    POSTGRESQL("postgresql", "PostgreSQL", 0),
    MYSQL("mysql", "MySQL", 0),
    ORACLE("oracle", "Oracle", 0),
    MSSQL("mssql", "Microsoft SQL Server", 0),
    OTHER("other", "", 0){

        @Override
        boolean productMatches(String productName) {
            return true;
        }

        @Override
        boolean versionMatches(int majorVersion) {
            return true;
        }
    };

    private final String type;
    private final String product;
    private final int version;

    private DatabaseEnum(String type, String product, int version) {
        this.type = type;
        this.product = product;
        this.version = version;
    }

    public static String getDatabaseType(String product, int majorVersion) {
        return Stream.of(DatabaseEnum.values()).filter((Predicate<DatabaseEnum>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$getDatabaseType$0(java.lang.String int com.atlassian.confluence.setup.DatabaseEnum ), (Lcom/atlassian/confluence/setup/DatabaseEnum;)Z)((String)product, (int)majorVersion)).findFirst().get().type;
    }

    public String getType() {
        return this.type;
    }

    boolean productMatches(String productName) {
        return this.product.equalsIgnoreCase(productName);
    }

    boolean versionMatches(int majorVersion) {
        return this.version == 0 || this.version == majorVersion;
    }

    private static /* synthetic */ boolean lambda$getDatabaseType$0(String product, int majorVersion, DatabaseEnum db) {
        return db.productMatches(product) && db.versionMatches(majorVersion);
    }
}

