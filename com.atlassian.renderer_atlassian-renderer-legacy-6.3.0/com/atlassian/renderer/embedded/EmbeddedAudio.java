/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;

public class EmbeddedAudio
extends EmbeddedObject {
    public static String RESOURCE_TYPE = "audio/";

    public EmbeddedAudio(String string) {
        this(new EmbeddedResourceParser(string));
    }

    public EmbeddedAudio(EmbeddedResourceParser parser) {
        super(parser);
        if (!this.properties.containsKey("width")) {
            this.properties.put("width", "300");
        }
        if (!this.properties.containsKey("height")) {
            this.properties.put("height", "42");
        }
    }

    public static boolean matchesType(EmbeddedResourceParser parser) {
        return parser.getType().startsWith(RESOURCE_TYPE);
    }
}

