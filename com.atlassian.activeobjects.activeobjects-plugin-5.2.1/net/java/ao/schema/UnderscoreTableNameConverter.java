/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.util.Objects;
import net.java.ao.Common;
import net.java.ao.schema.CanonicalClassNameTableNameConverter;
import net.java.ao.schema.Case;
import net.java.ao.schema.UnderScoreUtils;

public final class UnderscoreTableNameConverter
extends CanonicalClassNameTableNameConverter {
    private final Case tableNameCase;

    public UnderscoreTableNameConverter(Case tableNameCase) {
        this.tableNameCase = Objects.requireNonNull(tableNameCase, "tableNameCase can't be null");
    }

    @Override
    protected String getName(String entityClassCanonicalName) {
        return this.tableNameCase.apply(UnderScoreUtils.camelCaseToUnderScore(Common.convertSimpleClassName(entityClassCanonicalName)));
    }
}

