/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;

public class EmbeddedFlash
extends EmbeddedObject {
    public static String RESOURCE_TYPE = "application/x-shockwave-flash";

    public EmbeddedFlash(String string) {
        this(new EmbeddedResourceParser(string));
    }

    public EmbeddedFlash(EmbeddedResourceParser parser) {
        super(parser);
        if (!this.properties.containsKey("classid")) {
            this.properties.put("classid", "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000");
        }
        if (!this.properties.containsKey("codebase")) {
            this.properties.put("codebase", "https://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0");
        }
        if (!this.properties.containsKey("pluginspage")) {
            this.properties.put("pluginspage", "https://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash");
        }
        if (!this.properties.containsKey("type")) {
            this.properties.put("type", "application/x-shockwave-flash");
        }
        if (!this.properties.containsKey("quality")) {
            this.properties.put("quality", "high");
        }
        if (!this.properties.containsKey("loop")) {
            this.properties.put("loop", "false");
        }
        if (!this.properties.containsKey("menu")) {
            this.properties.put("menu", "false");
        }
        if (!this.properties.containsKey("scale")) {
            this.properties.put("scale", "exactfit");
        }
        if (!this.properties.containsKey("wmode")) {
            this.properties.put("wmode", "transparent");
        }
    }

    public static boolean matchesType(EmbeddedResourceParser parser) {
        return parser.getType().startsWith(RESOURCE_TYPE);
    }
}

