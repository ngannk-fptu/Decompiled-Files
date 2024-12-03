/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.helpers;

import java.util.regex.Pattern;

public class TableAndFieldNameValidator {
    public static final Pattern TABLE_OR_FIELD_NAME_PATTERN = Pattern.compile("^[\"`\\[\\]a-zA-Z0-9_]+$");

    public static String checkNameDoesNotHaveSqlInjections(String tableOrFieldName) {
        if (TABLE_OR_FIELD_NAME_PATTERN.matcher(tableOrFieldName).find()) {
            return tableOrFieldName;
        }
        throw new IllegalArgumentException("Table or field name is not allowed: " + tableOrFieldName);
    }
}

