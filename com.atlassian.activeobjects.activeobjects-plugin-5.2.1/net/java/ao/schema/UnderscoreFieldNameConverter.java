/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.util.List;
import java.util.Objects;
import net.java.ao.schema.AbstractFieldNameConverter;
import net.java.ao.schema.Case;
import net.java.ao.schema.FieldNameResolver;
import net.java.ao.schema.UnderScoreUtils;

public final class UnderscoreFieldNameConverter
extends AbstractFieldNameConverter {
    private final Case fieldNameCase;

    public UnderscoreFieldNameConverter(Case fieldNameCase) {
        this.fieldNameCase = Objects.requireNonNull(fieldNameCase, "fieldNameCase can't be null");
    }

    public UnderscoreFieldNameConverter(Case fieldNameCase, List<FieldNameResolver> fieldNameResolvers) {
        super(fieldNameResolvers);
        this.fieldNameCase = Objects.requireNonNull(fieldNameCase, "fieldNameCase can't be null");
    }

    @Override
    public String convertName(String name) {
        return this.fieldNameCase.apply(UnderScoreUtils.camelCaseToUnderScore(name));
    }
}

