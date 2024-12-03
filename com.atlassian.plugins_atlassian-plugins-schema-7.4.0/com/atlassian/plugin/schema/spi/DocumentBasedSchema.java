/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.resource.AlternativeResourceLoader
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.io.SAXReader
 */
package com.atlassian.plugin.schema.spi;

import com.atlassian.plugin.schema.spi.IdUtils;
import com.atlassian.plugin.schema.spi.Schema;
import com.atlassian.plugin.schema.spi.SchemaTransformer;
import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.atlassian.security.xml.SecureXmlParserFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.net.URL;
import java.util.Collections;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public final class DocumentBasedSchema
implements Schema {
    private final String elementName;
    private final String name;
    private final String description;
    private final String path;
    private final String fileName;
    private final String complexType;
    private final String maxOccurs;
    private final Iterable<String> requiredPermissions;
    private final Iterable<String> optionalPermissions;
    private final AlternativeResourceLoader resourceLoader;
    private final SchemaTransformer schemaTransformer;

    private DocumentBasedSchema(String elementName, String name, String description, String path, String fileName, String complexType, String maxOccurs, Iterable<String> requiredPermissions, Iterable<String> optionalPermissions, AlternativeResourceLoader resourceLoader, SchemaTransformer schemaTransformer) {
        this.elementName = elementName;
        this.name = name == null ? IdUtils.dashesToTitle(elementName) : name;
        this.description = description == null ? "A " + name + " module" : description;
        this.path = path;
        this.fileName = fileName;
        this.complexType = complexType;
        this.maxOccurs = maxOccurs;
        this.requiredPermissions = requiredPermissions;
        this.optionalPermissions = optionalPermissions;
        this.resourceLoader = resourceLoader;
        this.schemaTransformer = schemaTransformer;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public String getElementName() {
        return this.elementName;
    }

    @Override
    public String getComplexType() {
        return this.complexType;
    }

    @Override
    public String getMaxOccurs() {
        return this.maxOccurs;
    }

    @Override
    public Iterable<String> getRequiredPermissions() {
        return this.requiredPermissions;
    }

    @Override
    public Iterable<String> getOptionalPermissions() {
        return this.optionalPermissions;
    }

    @Override
    public Document getDocument() {
        return DocumentBasedSchema.getDocument(this.resourceLoader, this.path, this.schemaTransformer);
    }

    public static DynamicSchemaBuilder builder() {
        return new DynamicSchemaBuilder();
    }

    public static DynamicSchemaBuilder builder(String id) {
        return new DynamicSchemaBuilder(id);
    }

    private static Document getDocument(AlternativeResourceLoader resourceLoader, String path, SchemaTransformer transformer) {
        URL sourceUrl = resourceLoader.getResource(path);
        if (sourceUrl == null) {
            throw new IllegalStateException("Cannot find schema document " + path);
        }
        return transformer.transform(DocumentBasedSchema.parseDocument(sourceUrl));
    }

    private static Document parseDocument(URL xmlUrl) {
        Document source;
        try {
            source = DocumentBasedSchema.createSecureSaxReader().read(xmlUrl);
        }
        catch (DocumentException e) {
            throw new IllegalArgumentException("Unable to parse XML", e);
        }
        return source;
    }

    public static SAXReader createSecureSaxReader() {
        return new SAXReader(SecureXmlParserFactory.newXmlReader(), false);
    }

    public static class DynamicSchemaBuilder {
        private String name;
        private String description;
        private String path;
        private String fileName;
        private String elementName;
        private String complexType;
        private String maxOccurs = "unbounded";
        private Iterable<String> requiredPermissions = ImmutableSet.of((Object)"execute_java", (Object)"generate_any_html");
        private Iterable<String> optionalPermissions = Collections.emptySet();
        private AlternativeResourceLoader resourceLoader;
        private SchemaTransformer schemaTransformer = SchemaTransformer.IDENTITY;

        public DynamicSchemaBuilder() {
        }

        public DynamicSchemaBuilder(String elementName) {
            this.elementName = elementName;
            this.fileName = elementName + ".xsd";
            this.path = "/xsd/" + this.fileName;
            this.complexType = IdUtils.dashesToCamelCase(elementName) + "Type";
        }

        public DynamicSchemaBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public DynamicSchemaBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public DynamicSchemaBuilder setPath(String path) {
            this.path = path;
            return this;
        }

        public DynamicSchemaBuilder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public DynamicSchemaBuilder setElementName(String elementName) {
            this.elementName = elementName;
            return this;
        }

        public DynamicSchemaBuilder setRequiredPermissions(Iterable<String> permissions) {
            this.requiredPermissions = permissions;
            return this;
        }

        public DynamicSchemaBuilder setOptionalPermissions(Iterable<String> permissions) {
            this.optionalPermissions = permissions;
            return this;
        }

        public DynamicSchemaBuilder setComplexType(String complexType) {
            this.complexType = complexType;
            return this;
        }

        public DynamicSchemaBuilder setMaxOccurs(String maxOccurs) {
            this.maxOccurs = maxOccurs;
            return this;
        }

        public DynamicSchemaBuilder setResourceLoader(AlternativeResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
            return this;
        }

        public DynamicSchemaBuilder setTransformer(SchemaTransformer schemaTransformer) {
            this.schemaTransformer = schemaTransformer;
            return this;
        }

        public boolean validate() {
            return this.resourceLoader.getResource(this.path) != null;
        }

        public DocumentBasedSchema build() {
            Preconditions.checkNotNull((Object)this.elementName);
            Preconditions.checkNotNull((Object)this.fileName);
            Preconditions.checkNotNull((Object)this.complexType);
            Preconditions.checkNotNull((Object)this.resourceLoader);
            Preconditions.checkNotNull(this.requiredPermissions);
            Preconditions.checkNotNull(this.optionalPermissions);
            return new DocumentBasedSchema(this.elementName, this.name, this.description, this.path, this.fileName, this.complexType, this.maxOccurs, this.requiredPermissions, this.optionalPermissions, this.resourceLoader, this.schemaTransformer);
        }
    }
}

