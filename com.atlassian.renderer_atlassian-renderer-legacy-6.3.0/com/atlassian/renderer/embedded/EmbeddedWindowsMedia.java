/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;

public class EmbeddedWindowsMedia
extends EmbeddedObject {
    public static String RESOURCE_TYPE = "application/x-oleobject";
    public static String FILE_EXT_1 = ".wmv";
    public static String FILE_EXT_2 = ".wma";
    public static String FILE_EXT_3 = ".mpeg";

    public EmbeddedWindowsMedia(String string) {
        this(new EmbeddedResourceParser(string));
    }

    public EmbeddedWindowsMedia(EmbeddedResourceParser parser) {
        super(parser);
        if (!this.properties.containsKey("classid")) {
            this.properties.put("classid", "CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95");
        }
        if (!this.properties.containsKey("codebase")) {
            this.properties.put("codebase", "http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701");
        }
        if (!this.properties.containsKey("pluginspage")) {
            this.properties.put("pluginspage", "http://microsoft.com/windows/mediaplayer/en/download/");
        }
        if (!this.properties.containsKey("id")) {
            this.properties.put("id", "mediaPlayer");
        }
        if (!this.properties.containsKey("name")) {
            this.properties.put("name", "mediaPlayer");
        }
    }

    public static boolean matchesType(EmbeddedResourceParser parser) {
        return parser.getType().startsWith(RESOURCE_TYPE) || parser.getFilename().endsWith(FILE_EXT_1) || parser.getFilename().endsWith(FILE_EXT_2) || parser.getFilename().endsWith(FILE_EXT_3);
    }
}

