/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;

public class UnembeddableObject
extends EmbeddedObject {
    public static String[] UNEMBEDDABLE_TYPES = new String[]{"application/octet-stream", "text/.*", "message/.*"};

    public static boolean matchesType(EmbeddedResourceParser parser) {
        for (int i = 0; i < UNEMBEDDABLE_TYPES.length; ++i) {
            String s = UNEMBEDDABLE_TYPES[i];
            if (!parser.getType().matches(s)) continue;
            return true;
        }
        return false;
    }

    public UnembeddableObject(String string) {
        super(string);
    }

    public UnembeddableObject(EmbeddedResourceParser parser) {
        super(parser);
    }
}

