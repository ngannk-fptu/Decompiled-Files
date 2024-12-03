/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.CQLValueTypes
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Option
 *  com.atlassian.querylang.antlrgen.AqlParser$MapExprValueContext
 *  com.atlassian.querylang.antlrgen.AqlParser$MapKeyContext
 *  com.atlassian.querylang.antlrgen.AqlParser$MapPathContext
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.FieldHandler
 *  com.atlassian.querylang.fields.FieldMetaData
 *  com.atlassian.querylang.fields.ValueType
 *  com.atlassian.querylang.lib.fields.MapFieldHandler
 *  com.atlassian.querylang.lib.fields.MapFieldHandler$ValidationResult
 *  com.atlassian.querylang.lib.fields.MapFieldHandler$ValueType
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Multimap
 */
package com.atlassian.confluence.plugins.contentproperty.search.fields;

import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertyIndexSchemaManager;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.contentproperty.search.fields.ContentPropertyValueExpressionValidator;
import com.atlassian.confluence.plugins.contentproperty.search.fields.DateTimeContentPropertyAliasFieldHandler;
import com.atlassian.confluence.plugins.contentproperty.search.fields.NumericContentPropertyAliasFieldHandler;
import com.atlassian.confluence.plugins.contentproperty.search.fields.SchemaFieldToContentPropertyValueMapping;
import com.atlassian.confluence.plugins.contentproperty.search.fields.StringContentPropertyAliasFieldHandler;
import com.atlassian.confluence.plugins.contentproperty.search.fields.TextContentPropertyAliasFieldHandler;
import com.atlassian.confluence.plugins.contentproperty.search.query.ContentPropertySearchQueryFactory;
import com.atlassian.confluence.plugins.cql.spi.fields.CQLValueTypes;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.FieldHandler;
import com.atlassian.querylang.fields.FieldMetaData;
import com.atlassian.querylang.fields.ValueType;
import com.atlassian.querylang.lib.fields.MapFieldHandler;
import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ContentPropertyFieldHandler
extends BaseFieldHandler
implements MapFieldHandler<V2SearchQueryWrapper> {
    private static final String FIELD_NAME = "content.property";
    private final ContentPropertyValueExpressionValidator contentPropertyValueExpressionValidator;
    private final ContentPropertyIndexSchemaManager contentPropertyIndexSchemaManager;
    private final ContentPropertySearchQueryFactory contentPropertySearchQueryFactory;
    private final SchemaFieldToContentPropertyValueMapping schemaFieldToContentPropertyValueMapping;

    public ContentPropertyFieldHandler(ContentPropertyIndexSchemaManager contentPropertyIndexSchemaManager) {
        super(FIELD_NAME, FieldMetaData.createForType((ValueType)CQLValueTypes.DYNAMIC_TYPE));
        this.contentPropertyIndexSchemaManager = contentPropertyIndexSchemaManager;
        this.contentPropertySearchQueryFactory = new ContentPropertySearchQueryFactory();
        this.contentPropertyValueExpressionValidator = new ContentPropertyValueExpressionValidator();
        this.schemaFieldToContentPropertyValueMapping = new SchemaFieldToContentPropertyValueMapping();
    }

    public MapFieldHandler.ValidationResult validate(AqlParser.MapKeyContext mapKeyContext, AqlParser.MapPathContext mapPathContext, AqlParser.MapExprValueContext mapExprValueContext) {
        ValidationResultBuilder validationResultBuilder = ValidationResultBuilder.builder();
        Multimap<String, ContentPropertySchemaField> indexSchema = this.contentPropertyIndexSchemaManager.getIndexSchema();
        String contentPropertyKey = mapKeyContext.getText();
        if (!this.doesContentPropertyKeyExistInSchema(mapKeyContext.getText(), indexSchema)) {
            return validationResultBuilder.setHasValidKey(false).build();
        }
        Option<ContentPropertySchemaField> schemaFieldOption = this.findContentPropertySchemaFieldByJsonExpression(indexSchema.get((Object)contentPropertyKey), mapPathContext.getText());
        if (!schemaFieldOption.isDefined()) {
            return validationResultBuilder.setHasValidPath(false).build();
        }
        return validationResultBuilder.setHasValidOperator(this.contentPropertyValueExpressionValidator.isOperatorValidForFieldType(((ContentPropertySchemaField)schemaFieldOption.get()).getFieldType(), mapExprValueContext)).build();
    }

    public MapFieldHandler.ValueType getValueType(AqlParser.MapKeyContext mapKeyContext, AqlParser.MapPathContext mapPathContext) {
        ContentPropertySchemaField schemaField = this.getContentPropertySchemaField(mapKeyContext, mapPathContext);
        return this.schemaFieldToContentPropertyValueMapping.getValueTypeFor(schemaField.getFieldType());
    }

    public V2SearchQueryWrapper build(AqlParser.MapKeyContext mapKeyContext, AqlParser.MapPathContext mapPathContext, AqlParser.MapExprValueContext mapExprValueContext, Object value) {
        ContentPropertySchemaField schemaField = this.getContentPropertySchemaField(mapKeyContext, mapPathContext);
        return this.contentPropertySearchQueryFactory.create(schemaField, value, mapExprValueContext);
    }

    private ContentPropertySchemaField getContentPropertySchemaField(AqlParser.MapKeyContext mapKeyContext, AqlParser.MapPathContext mapPathContext) {
        Multimap<String, ContentPropertySchemaField> indexSchema = this.contentPropertyIndexSchemaManager.getIndexSchema();
        return (ContentPropertySchemaField)this.findContentPropertySchemaFieldByJsonExpression(indexSchema.get((Object)mapKeyContext.getText()), mapPathContext.getText()).get();
    }

    private Option<ContentPropertySchemaField> findContentPropertySchemaFieldByJsonExpression(Collection<ContentPropertySchemaField> contentPropertySchemaFields, String jsonExpression) {
        return Iterables.findFirst(contentPropertySchemaFields, input -> jsonExpression.equals("." + input.getJsonExpression()));
    }

    private boolean doesContentPropertyKeyExistInSchema(String key, Multimap<String, ContentPropertySchemaField> indexSchema) {
        return indexSchema.containsKey((Object)key);
    }

    public Iterable<? extends FieldHandler> getAliasHandlers() {
        ArrayList<Object> handlers = new ArrayList<Object>();
        Multimap<String, ContentPropertySchemaField> indexSchema = this.contentPropertyIndexSchemaManager.getIndexSchema();
        if (indexSchema != null) {
            for (Map.Entry e : indexSchema.entries()) {
                ContentPropertySchemaField field = (ContentPropertySchemaField)e.getValue();
                String alias = field.getAlias();
                if (alias == null) continue;
                switch (field.getFieldType()) {
                    case NUMBER: {
                        handlers.add((Object)new NumericContentPropertyAliasFieldHandler(alias, field));
                        break;
                    }
                    case STRING: {
                        handlers.add((Object)new StringContentPropertyAliasFieldHandler(alias, field, true));
                        break;
                    }
                    case TEXT: {
                        handlers.add((Object)new TextContentPropertyAliasFieldHandler(alias, field, true));
                        break;
                    }
                    case DATE: {
                        handlers.add((Object)new DateTimeContentPropertyAliasFieldHandler(alias, field));
                    }
                }
            }
        }
        return handlers;
    }

    private static final class ValidationResultBuilder {
        private boolean hasValidKey = true;
        private boolean hasValidPath = true;
        private boolean hasValidOperator = true;

        private ValidationResultBuilder() {
        }

        private static ValidationResultBuilder builder() {
            return new ValidationResultBuilder();
        }

        private ValidationResultBuilder setHasValidKey(boolean hasValidKey) {
            this.hasValidKey = hasValidKey;
            return this;
        }

        private ValidationResultBuilder setHasValidPath(boolean hasValidPath) {
            this.hasValidPath = hasValidPath;
            return this;
        }

        private ValidationResultBuilder setHasValidOperator(boolean hasValidOperator) {
            this.hasValidOperator = hasValidOperator;
            return this;
        }

        private MapFieldHandler.ValidationResult build() {
            return new ImmutableValidationResult(this);
        }

        private static final class ImmutableValidationResult
        implements MapFieldHandler.ValidationResult {
            private static final Joiner CSV_JOINER = Joiner.on((String)", ").skipNulls();
            private final boolean hasValidKey;
            private final boolean hasValidPath;
            private final boolean hasValidOperator;

            private ImmutableValidationResult(ValidationResultBuilder builder) {
                this.hasValidKey = builder.hasValidKey;
                this.hasValidPath = builder.hasValidPath;
                this.hasValidOperator = builder.hasValidOperator;
            }

            public boolean isValid() {
                return this.hasValidKey && this.hasValidPath && this.hasValidOperator;
            }

            public boolean isValidKey() {
                return this.hasValidKey;
            }

            public boolean isValidPath() {
                return this.hasValidPath;
            }

            public boolean isValidOperator() {
                return this.hasValidOperator;
            }

            public String getMessage() {
                if (this.isValid()) {
                    return "Content properties query is valid.";
                }
                ArrayList<String> failedTokens = new ArrayList<String>();
                if (!this.isValidKey()) {
                    failedTokens.add("key");
                }
                if (!this.isValidOperator()) {
                    failedTokens.add("operator");
                }
                if (!this.isValidPath()) {
                    failedTokens.add("path");
                }
                return String.format("Content properties query is invalid, verification failed for tokens: %s", CSV_JOINER.join(failedTokens));
            }
        }
    }
}

