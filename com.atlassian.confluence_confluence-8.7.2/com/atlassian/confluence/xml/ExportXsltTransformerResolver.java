/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.xml;

import com.atlassian.confluence.xml.XsltTransformerResolver;
import com.atlassian.core.util.ClassLoaderUtils;
import javax.xml.transform.TransformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExportXsltTransformerResolver
implements XsltTransformerResolver {
    private static final String DEFAULT_XSLT_PARSER_CLASS_NAME = "org.apache.xalan.processor.TransformerFactoryImpl";
    private final String xsltParserClassName;
    private static final String XLST_FACTORY_OVERRIDE_PROPERTY_NAME = "com.atlassian.confluence.export.xslt.implementation";
    private final Class<TransformerFactory> xsltParserClass;
    private static final Logger LOG = LoggerFactory.getLogger(ExportXsltTransformerResolver.class);
    private static final ExportXsltTransformerResolver INSTANCE = new ExportXsltTransformerResolver();

    private ExportXsltTransformerResolver() {
        this.xsltParserClassName = System.getProperty(XLST_FACTORY_OVERRIDE_PROPERTY_NAME, DEFAULT_XSLT_PARSER_CLASS_NAME);
        this.xsltParserClass = this.initializeParserClass();
    }

    ExportXsltTransformerResolver(String parserClassName) {
        this.xsltParserClassName = parserClassName;
        this.xsltParserClass = this.initializeParserClass();
    }

    private Class<TransformerFactory> initializeParserClass() {
        try {
            return ClassLoaderUtils.loadClass((String)this.xsltParserClassName, ExportXsltTransformerResolver.class);
        }
        catch (ClassNotFoundException e) {
            if (!this.xsltParserClassName.equals(DEFAULT_XSLT_PARSER_CLASS_NAME)) {
                LOG.warn("Unable to find XSLT Factory of class : [ " + this.xsltParserClassName + " ], falling back to default");
                try {
                    return ClassLoaderUtils.loadClass((String)DEFAULT_XSLT_PARSER_CLASS_NAME, ExportXsltTransformerResolver.class);
                }
                catch (ClassNotFoundException e1) {
                    throw this.translateException(e);
                }
            }
            throw this.translateException(e);
        }
    }

    private IllegalStateException translateException(ClassNotFoundException e) {
        return new IllegalStateException("Unable to load Xerces parser due to " + e.getMessage(), e);
    }

    public static ExportXsltTransformerResolver getInstance() {
        return INSTANCE;
    }

    public static TransformerFactory getResolvedXsltTransformerFactory() {
        return INSTANCE.resolveTransformerFactory();
    }

    @Override
    public TransformerFactory resolveTransformerFactory() {
        try {
            return this.xsltParserClass.newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw this.translateException(e);
        }
    }

    private IllegalStateException translateException(Exception e) {
        return new IllegalStateException("Can not initialize the XSL Transformer Factory due to " + e.getMessage(), e);
    }
}

