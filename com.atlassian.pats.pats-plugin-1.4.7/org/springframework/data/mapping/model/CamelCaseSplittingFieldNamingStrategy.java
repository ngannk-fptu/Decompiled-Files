/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.mapping.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.util.ParsingUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class CamelCaseSplittingFieldNamingStrategy
implements FieldNamingStrategy {
    private final String delimiter;

    public CamelCaseSplittingFieldNamingStrategy(String delimiter) {
        Assert.notNull((Object)delimiter, (String)"Delimiter must not be null!");
        this.delimiter = delimiter;
    }

    @Override
    public String getFieldName(PersistentProperty<?> property) {
        List<String> parts = ParsingUtils.splitCamelCaseToLower(property.getName());
        ArrayList<String> result = new ArrayList<String>();
        for (String part : parts) {
            String candidate = this.preparePart(part);
            if (!StringUtils.hasText((String)candidate)) continue;
            result.add(candidate);
        }
        return StringUtils.collectionToDelimitedString(result, (String)this.delimiter);
    }

    protected String preparePart(String part) {
        return part;
    }
}

