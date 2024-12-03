/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;

public class EmbeddedQuicktime
extends EmbeddedObject {
    public static String RESOURCE_TYPE = "video/quicktime";
    public static String FILE_EXT_1 = ".mp4";

    public EmbeddedQuicktime(String string) {
        this(new EmbeddedResourceParser(string));
    }

    public EmbeddedQuicktime(EmbeddedResourceParser parser) {
        super(parser);
        if (!this.properties.containsKey("classid")) {
            this.properties.put("classid", "clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B");
        }
        if (!this.properties.containsKey("codebase")) {
            this.properties.put("codebase", "https://www.apple.com/qtactivex/qtplugin.cab");
        }
        if (!this.properties.containsKey("pluginspage")) {
            this.properties.put("pluginspage", "https://www.apple.com/quicktime/download/");
        }
        if (!this.properties.containsKey("width")) {
            this.properties.put("width", "480");
        }
        if (!this.properties.containsKey("height")) {
            this.properties.put("height", "380");
        }
    }

    public static boolean matchesType(EmbeddedResourceParser parser) {
        return parser.getType().startsWith(RESOURCE_TYPE) || parser.getFilename().endsWith(FILE_EXT_1);
    }
}

