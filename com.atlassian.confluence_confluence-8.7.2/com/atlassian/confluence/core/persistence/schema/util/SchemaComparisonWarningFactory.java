/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableCollection$Builder
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.persistence.schema.util;

import com.atlassian.confluence.core.persistence.schema.api.SchemaElementComparison;
import com.atlassian.confluence.core.persistence.schema.api.TableSchemaComparison;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaComparisonWarningFactory {
    private static final Logger log = LoggerFactory.getLogger(SchemaComparisonWarningFactory.class);
    private static final String SUPPRESSION_FILE = "com/atlassian/confluence/core/persistence/schema/util/schema-comparison-suppressions.txt";
    private static final String SPACE_LOWERSPACEKEY = "spaces.lowerspacekey";
    private static final Set<String> suppressions = new HashSet<String>(Arrays.asList("spaces.lowerspacekey"));

    public static Collection<String> buildWarnings(Iterable<? extends TableSchemaComparison> tables) {
        SchemaComparisonWarningFactory.loadSuppressions();
        ImmutableList.Builder warnings = ImmutableList.builder();
        for (TableSchemaComparison tableSchemaComparison : tables) {
            SchemaComparisonWarningFactory.checkTable(tableSchemaComparison, (ImmutableCollection.Builder<String>)warnings);
        }
        return warnings.build();
    }

    private static void checkTable(TableSchemaComparison table, ImmutableCollection.Builder<String> warnings) {
        log.debug("Verifying table [{}]", (Object)table.getTableName());
        for (SchemaElementComparison.ColumnComparison columnComparison : table.getColumns()) {
            if (suppressions.contains(SchemaComparisonWarningFactory.columnFullName(table, columnComparison).toLowerCase())) continue;
            SchemaComparisonWarningFactory.verifySchemaElement(columnComparison, SchemaComparisonWarningFactory.columnPrefix(table, columnComparison), warnings);
        }
        for (SchemaElementComparison.IndexComparison indexComparison : table.getIndexes()) {
            if (suppressions.contains(SchemaComparisonWarningFactory.indexFullName(table, indexComparison).toLowerCase())) continue;
            SchemaComparisonWarningFactory.verifySchemaElement(indexComparison, SchemaComparisonWarningFactory.indexPrefix(table, indexComparison), warnings);
        }
    }

    private static void verifySchemaElement(SchemaElementComparison<?> column, String prefix, ImmutableCollection.Builder<String> warnings) {
        switch (column.getResult()) {
            case MISMATCH: {
                warnings.add((Object)String.format("%s has mismatched definitions for expected [%s] and actual [%s]", prefix, column.expected().orElse(null), column.actual().orElse(null)));
                break;
            }
            case ACTUAL_ELEMENT_MISSING: {
                warnings.add((Object)String.format("%s is missing; expected [%s]", prefix, column.expected().orElse(null)));
                break;
            }
            case EXPECTED_ELEMENT_MISSING: {
                warnings.add((Object)String.format("%s is unexpected; [%s]", prefix, column.actual().orElse(null)));
                break;
            }
        }
    }

    private static void loadSuppressions() {
        InputStream stream = SchemaComparisonWarningFactory.class.getClassLoader().getResourceAsStream(SUPPRESSION_FILE);
        if (stream != null) {
            IOUtils.readLines((InputStream)stream, (Charset)Charset.defaultCharset()).stream().map(String::trim).filter(line -> !line.isEmpty() && !line.startsWith("#")).forEach(suppressions::add);
        }
    }

    private static String columnFullName(TableSchemaComparison table, SchemaElementComparison.ColumnComparison column) {
        return String.format("%s.%s", table.getTableName(), column.getColumnName());
    }

    private static String indexFullName(TableSchemaComparison table, SchemaElementComparison.IndexComparison index) {
        return String.format("%s.%s", table.getTableName(), index.getIndexName());
    }

    private static String columnPrefix(TableSchemaComparison table, SchemaElementComparison.ColumnComparison column) {
        return String.format("Column [%s] of table [%s]", column.getColumnName(), table.getTableName());
    }

    private static String indexPrefix(TableSchemaComparison table, SchemaElementComparison.IndexComparison index) {
        return String.format("Index [%s] of table [%s]", index.getIndexName(), table.getTableName());
    }
}

