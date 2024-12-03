/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.graphql.GraphQL
 *  com.atlassian.graphql.types.DynamicType
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.graphql;

import com.atlassian.confluence.rest.api.graphql.GraphQL;
import com.atlassian.graphql.types.DynamicType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.lang.reflect.Type;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={GraphQL.class})
public class DefaultGraphQL
implements GraphQL {
    public boolean isDynamicType(Type type) {
        return type instanceof DynamicType;
    }

    public Type createDynamicType(String typeName, Map<String, Type> fieldTypes) {
        return new DynamicType(typeName, fieldTypes);
    }

    public Map<String, Type> getDynamicTypeFields(Type type) {
        if (!(type instanceof DynamicType)) {
            throw new IllegalArgumentException("Not a DynamicType");
        }
        return ((DynamicType)type).getFieldTypes();
    }
}

