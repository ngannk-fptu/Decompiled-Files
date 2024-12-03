/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Document
 */
package com.atlassian.plugin.schema.spi;

import org.dom4j.Document;

public interface SchemaTransformer {
    public static final SchemaTransformer IDENTITY = document -> document;

    public Document transform(Document var1);
}

