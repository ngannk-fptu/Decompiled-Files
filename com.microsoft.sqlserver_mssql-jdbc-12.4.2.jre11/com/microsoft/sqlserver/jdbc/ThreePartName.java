/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.JDBCSyntaxTranslator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ThreePartName {
    private static final Pattern THREE_PART_NAME = Pattern.compile(JDBCSyntaxTranslator.getSQLIdentifierWithGroups());
    private final String databasePart;
    private final String ownerPart;
    private final String procedurePart;

    private ThreePartName(String databasePart, String ownerPart, String procedurePart) {
        this.databasePart = databasePart;
        this.ownerPart = ownerPart;
        this.procedurePart = procedurePart;
    }

    String getDatabasePart() {
        return this.databasePart;
    }

    String getOwnerPart() {
        return this.ownerPart;
    }

    String getProcedurePart() {
        return this.procedurePart;
    }

    static ThreePartName parse(String theProcName) {
        String procedurePart = null;
        String ownerPart = null;
        String databasePart = null;
        if (null != theProcName) {
            Matcher matcher = THREE_PART_NAME.matcher(theProcName);
            if (matcher.matches()) {
                if (matcher.group(2) != null) {
                    databasePart = matcher.group(1);
                    if ((matcher = THREE_PART_NAME.matcher(matcher.group(2))).matches()) {
                        if (null != matcher.group(2)) {
                            ownerPart = matcher.group(1);
                            procedurePart = matcher.group(2);
                        } else {
                            ownerPart = databasePart;
                            databasePart = null;
                            procedurePart = matcher.group(1);
                        }
                    }
                } else {
                    procedurePart = matcher.group(1);
                }
            } else {
                procedurePart = theProcName;
            }
        }
        return new ThreePartName(databasePart, ownerPart, procedurePart);
    }
}

