/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.sql.Keywords;
import com.querydsl.sql.SQLServer2005Templates;
import com.querydsl.sql.SQLTemplates;
import java.util.Set;

public class SQLServer2008Templates
extends SQLServer2005Templates {
    public static final SQLServer2008Templates DEFAULT = new SQLServer2008Templates();

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new SQLServer2008Templates(escape, quote);
            }
        };
    }

    public SQLServer2008Templates() {
        this(Keywords.SQLSERVER2008, '\\', false);
    }

    public SQLServer2008Templates(boolean quote) {
        this(Keywords.SQLSERVER2008, '\\', quote);
    }

    public SQLServer2008Templates(char escape, boolean quote) {
        this(Keywords.SQLSERVER2008, escape, quote);
    }

    protected SQLServer2008Templates(Set<String> keywords, char escape, boolean quote) {
        super(keywords, escape, quote);
    }
}

