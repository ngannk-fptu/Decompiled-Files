/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;
import java.util.Map;
import java.util.Properties;

public class EmbeddedObject
extends EmbeddedResource {
    public EmbeddedObject(String string) {
        this(new EmbeddedResourceParser(string));
    }

    public EmbeddedObject(EmbeddedResourceParser parser) {
        super(parser);
        this.properties = new Properties();
        this.properties.putAll((Map<?, ?>)parser.getProperties());
    }
}

