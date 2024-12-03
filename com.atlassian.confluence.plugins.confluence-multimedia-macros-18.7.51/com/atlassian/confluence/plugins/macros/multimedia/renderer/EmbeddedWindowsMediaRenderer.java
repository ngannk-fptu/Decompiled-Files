/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.renderer.embedded.EmbeddedObject
 */
package com.atlassian.confluence.plugins.macros.multimedia.renderer;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.macros.multimedia.renderer.AbstractEmbeddedResourceRenderer;
import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import java.util.Map;

public class EmbeddedWindowsMediaRenderer
extends AbstractEmbeddedResourceRenderer {
    private static final String RESOURCE_TYPE = "application/x-oleobject";
    private static final String FILE_EXT_1 = ".wmv";
    private static final String FILE_EXT_2 = ".wma";

    @Override
    protected Map<String, Object> refineParams(Attachment attachment, Map<String, Object> properties) {
        if (!properties.containsKey("classid")) {
            properties.put("classid", "CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95");
        }
        if (!properties.containsKey("codebase")) {
            properties.put("codebase", "http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701");
        }
        if (!properties.containsKey("pluginspage")) {
            properties.put("pluginspage", "http://microsoft.com/windows/mediaplayer/en/download/");
        }
        if (!properties.containsKey("id")) {
            properties.put("id", "mediaPlayer");
        }
        if (!properties.containsKey("name")) {
            properties.put("name", "mediaPlayer");
        }
        if (properties.containsKey("autostart")) {
            properties.put("autostart", properties.get("autostart"));
            properties.put("autoplay", properties.get("autostart"));
        }
        return properties;
    }

    public boolean matchesType(EmbeddedObject resource) {
        return resource.getContentType().startsWith(RESOURCE_TYPE) || resource.getFileExtension().endsWith(FILE_EXT_1) || resource.getFileExtension().endsWith(FILE_EXT_2);
    }
}

