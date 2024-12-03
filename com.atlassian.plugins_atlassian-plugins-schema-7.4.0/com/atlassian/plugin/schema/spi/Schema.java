/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Document
 */
package com.atlassian.plugin.schema.spi;

import com.atlassian.plugin.schema.spi.SchemaDocumented;
import org.dom4j.Document;

public interface Schema
extends SchemaDocumented {
    public String getFileName();

    public String getElementName();

    @Override
    public String getName();

    @Override
    public String getDescription();

    public String getComplexType();

    public String getMaxOccurs();

    public Iterable<String> getRequiredPermissions();

    public Iterable<String> getOptionalPermissions();

    public Document getDocument();
}

