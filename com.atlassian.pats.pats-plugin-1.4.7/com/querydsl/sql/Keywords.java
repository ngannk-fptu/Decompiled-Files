/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.google.common.io.LineProcessor
 *  com.google.common.io.Resources
 */
package com.querydsl.sql;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Set;

final class Keywords {
    public static final Set<String> DEFAULT = Keywords.readLines("default");
    public static final Set<String> CUBRID = Keywords.readLines("cubrid");
    public static final Set<String> DB2 = Keywords.readLines("db2");
    public static final Set<String> DERBY = Keywords.readLines("derby");
    public static final Set<String> FIREBIRD = Keywords.readLines("firebird");
    public static final Set<String> H2 = Keywords.readLines("h2");
    public static final Set<String> HSQLDB = Keywords.readLines("hsqldb");
    public static final Set<String> MYSQL = Keywords.readLines("mysql");
    public static final Set<String> ORACLE = Keywords.readLines("oracle");
    public static final Set<String> POSTGRESQL = Keywords.readLines("postgresql");
    public static final Set<String> SQLITE = Keywords.readLines("sqlite");
    public static final Set<String> SQLSERVER2005 = Keywords.readLines("sqlserver2005");
    public static final Set<String> SQLSERVER2008 = Keywords.readLines("sqlserver2008");
    public static final Set<String> SQLSERVER2012 = Keywords.readLines("sqlserver2012");

    private Keywords() {
    }

    private static Set<String> readLines(String path) {
        try {
            return ImmutableSet.copyOf((Collection)((Collection)Resources.readLines((URL)Keywords.class.getResource("/keywords/" + path), (Charset)Charsets.UTF_8, (LineProcessor)new CommentDiscardingLineProcessor())));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class CommentDiscardingLineProcessor
    implements LineProcessor<Collection<String>> {
        private final Collection<String> result = Sets.newHashSet();

        private CommentDiscardingLineProcessor() {
        }

        public boolean processLine(String line) throws IOException {
            if (!line.isEmpty() && !line.startsWith("#")) {
                this.result.add(line);
            }
            return true;
        }

        public Collection<String> getResult() {
            return this.result;
        }
    }
}

