/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;

public class EmbeddedRealMedia
extends EmbeddedObject {
    public static String RESOURCE_TYPE = "application/vnd.rn-realmedia";
    public static String FILE_EXT_1 = ".rm";
    public static String FILE_EXT_2 = ".ram";

    public EmbeddedRealMedia(String string) {
        this(new EmbeddedResourceParser(string));
    }

    public EmbeddedRealMedia(EmbeddedResourceParser parser) {
        super(parser);
        if (!this.properties.containsKey("classid")) {
            this.properties.put("classid", "clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA");
        }
    }

    public static boolean matchesType(EmbeddedResourceParser parser) {
        return parser.getType().startsWith(RESOURCE_TYPE) || parser.getFilename().endsWith(FILE_EXT_1) || parser.getFilename().endsWith(FILE_EXT_2);
    }
}

