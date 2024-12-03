/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class EmbeddedImage
extends EmbeddedResource {
    private boolean isThumbnail;
    private static final Set VALID_PROPERTIES = EmbeddedImage.createValidProperties();

    private static Set createValidProperties() {
        HashSet<String> result = new HashSet<String>();
        result.add("align");
        result.add("border");
        result.add("bordercolor");
        result.add("alt");
        result.add("title");
        result.add("longdesc");
        result.add("height");
        result.add("width");
        result.add("src");
        result.add("lang");
        result.add("dir");
        result.add("hspace");
        result.add("vspace");
        result.add("ismap");
        result.add("usemap");
        result.add("id");
        result.add("class");
        return result;
    }

    public EmbeddedImage(String originalText) {
        this(new EmbeddedResourceParser(originalText));
    }

    public EmbeddedImage(EmbeddedResourceParser parser) {
        super(parser);
        this.properties = new Properties();
        this.properties.putAll((Map<?, ?>)parser.getProperties());
        if (!this.properties.containsKey("align")) {
            this.properties.put("align", "absmiddle");
        }
        if (!this.properties.containsKey("border")) {
            this.properties.put("border", "0");
        }
        this.isThumbnail = this.properties.containsKey("thumbnail");
        if (this.isThumbnail) {
            this.properties.remove("thumbnail");
        }
        Enumeration<Object> e = this.properties.keys();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            if (VALID_PROPERTIES.contains(key.toLowerCase())) continue;
            this.properties.remove(key);
        }
    }

    public static boolean matchesType(EmbeddedResourceParser parser) {
        return parser.getType().startsWith("image");
    }

    public boolean isThumbNail() {
        return this.isThumbnail;
    }
}

