/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.CqlValueTypeFactory
 *  com.atlassian.fugue.Option
 *  com.atlassian.querylang.fields.UISupport
 *  com.atlassian.querylang.fields.ValueTypeFactory
 *  com.atlassian.querylang.lib.plugins.UISupportFactory
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.contentproperty.index.config;

import com.atlassian.confluence.plugins.contentproperty.index.config.InvalidSchemaDefinitionException;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertyIndexSchema;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.contentproperty.index.schema.SchemaFieldType;
import com.atlassian.confluence.plugins.cql.spi.fields.CqlValueTypeFactory;
import com.atlassian.fugue.Option;
import com.atlassian.querylang.fields.UISupport;
import com.atlassian.querylang.fields.ValueTypeFactory;
import com.atlassian.querylang.lib.plugins.UISupportFactory;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlDescriptorSchemaReader {
    private static final Logger log = LoggerFactory.getLogger(XmlDescriptorSchemaReader.class);
    private static final ValueTypeFactory typeFactory = new CqlValueTypeFactory();
    private static final String PROPERTY_ELEMENT_NAME = "key";
    private static final String PROPERTY_CONTENT_PROPERTY_KEY_ATTRIBUTE_NAME = "property-key";
    private static final String PROPERTY_EXTRACT_ELEMENT_NAME = "extract";
    private static final String PROPERTY_EXTRACT_PATH_ATTRIBUTE_NAME = "path";
    private static final String PROPERTY_EXTRACT_TYPE_ATTRIBUTE_NAME = "type";
    private static final String PROPERTY_EXTRACT_ALIAS_ATTRIBUTE_NAME = "alias";

    public ContentPropertyIndexSchema read(Element element, String pluginName, String moduleName) {
        ContentPropertySchemaBuilder schemaBuilder = ContentPropertySchemaBuilder.builder();
        for (Element propertyElement : this.getChildrenElementsByName(element, PROPERTY_ELEMENT_NAME)) {
            this.readSchemaForSingleProperty(propertyElement, pluginName, moduleName, schemaBuilder);
        }
        return new ContentPropertyIndexSchema(schemaBuilder.build());
    }

    private void readSchemaForSingleProperty(Element propertyRootElement, String pluginName, String moduleName, ContentPropertySchemaBuilder builder) {
        String contentPropertyKey = this.getRequiredAttributeValue(propertyRootElement, PROPERTY_CONTENT_PROPERTY_KEY_ATTRIBUTE_NAME);
        for (Element extractElement : this.getChildrenElementsByName(propertyRootElement, PROPERTY_EXTRACT_ELEMENT_NAME)) {
            String jsonExpression = this.getRequiredAttributeValue(extractElement, PROPERTY_EXTRACT_PATH_ATTRIBUTE_NAME);
            String type = this.getRequiredAttributeValue(extractElement, PROPERTY_EXTRACT_TYPE_ATTRIBUTE_NAME);
            String alias = this.getOptionalAttributeValue(extractElement, PROPERTY_EXTRACT_ALIAS_ATTRIBUTE_NAME);
            Option<UISupport> uiSupport = Option.none();
            if (alias != null) {
                try {
                    uiSupport = XmlDescriptorSchemaReader.extractUISupport(extractElement.element("ui-support"));
                }
                catch (NullPointerException e) {
                    throw new InvalidSchemaDefinitionException("invalid ui-support");
                }
            } else if (extractElement.element("ui-support") != null) {
                throw new InvalidSchemaDefinitionException("ui-support requires a field alias to be set");
            }
            builder.addField(contentPropertyKey, type, jsonExpression, pluginName, moduleName, alias, uiSupport);
        }
    }

    public static Option<UISupport> extractUISupport(Element element) {
        if (element == null) {
            return Option.none();
        }
        return Option.some((Object)UISupportFactory.extractUISupport((Element)element, (ValueTypeFactory)typeFactory));
    }

    private String getOptionalAttributeValue(Element element, String attributeName) {
        Attribute attribute = element.attribute(attributeName);
        return attribute == null ? null : attribute.getValue();
    }

    private String getRequiredAttributeValue(Element element, String attributeName) {
        Attribute attribute = element.attribute(attributeName);
        if (attribute == null) {
            throw new InvalidSchemaDefinitionException(String.format("Missing required attribute '%s' for element '%s'", attributeName, element.getName()));
        }
        return attribute.getValue();
    }

    private List<Element> getChildrenElementsByName(Element parent, String name) {
        ArrayList children = Lists.newArrayList((Iterable)parent.elements(name));
        if (children.isEmpty()) {
            log.warn("Element '{}' doesn't contain any child element named '{}'", (Object)parent.getName(), (Object)name);
        }
        return children;
    }

    private static final class ContentPropertySchemaBuilder {
        private final Multimap<String, ContentPropertySchemaField> schema = HashMultimap.create();

        private ContentPropertySchemaBuilder() {
        }

        public static ContentPropertySchemaBuilder builder() {
            return new ContentPropertySchemaBuilder();
        }

        public ContentPropertySchemaBuilder addField(String contentPropertyKey, String type, String jsonExpression, String pluginName, String moduleName, String alias, Option<UISupport> uiSupport) {
            this.schema.put((Object)contentPropertyKey, (Object)new ContentPropertySchemaField(jsonExpression, this.findSchemaFieldTypeByName(type), this.buildFieldName(contentPropertyKey, jsonExpression), pluginName, moduleName, alias, uiSupport));
            return this;
        }

        private String buildFieldName(String contentPropertyKey, String jsonExpression) {
            return String.format("content-property-%s-%s", contentPropertyKey, jsonExpression);
        }

        private SchemaFieldType findSchemaFieldTypeByName(String fieldType) {
            for (SchemaFieldType schemaFieldType : SchemaFieldType.values()) {
                if (!schemaFieldType.name().equalsIgnoreCase(fieldType)) continue;
                return schemaFieldType;
            }
            throw new InvalidSchemaDefinitionException(String.format("Unsupported schema field type - '%s'", fieldType));
        }

        public Multimap<String, ContentPropertySchemaField> build() {
            return Multimaps.unmodifiableMultimap(this.schema);
        }
    }
}

