/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.Icon;
import com.atlassian.renderer.IconManager;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

final class ImageConverter
implements Converter {
    static ImageConverter INSTANCE = new ImageConverter();
    private volatile Map<String, String> emoticonMap = null;

    private ImageConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("img");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        String src;
        if (nodeContext.getAttribute("imagetext") != null) {
            String imagetext = nodeContext.getAttribute("imagetext");
            if (nodeContext.getAttribute("width") != null) {
                String params = "";
                if (imagetext.contains("width=")) {
                    imagetext = imagetext.replaceAll("width=\\d*", "width=" + nodeContext.getAttribute("width"));
                } else {
                    params = params + "width=" + nodeContext.getAttribute("width");
                }
                if (nodeContext.getAttribute("height") != null) {
                    if (imagetext.contains("height=")) {
                        imagetext = imagetext.replaceAll("height=\\d*", "height=" + nodeContext.getAttribute("height"));
                    } else {
                        params = params + ",height=" + nodeContext.getAttribute("height");
                    }
                }
                if (StringUtils.isNotEmpty((String)params)) {
                    imagetext = !imagetext.contains("|") ? imagetext + "|" + params : imagetext + "," + params;
                }
            }
            String separator = wysiwygConverter.getSeparator("imagelink", nodeContext);
            return separator + "TEXTSEP" + "!" + imagetext + "!";
        }
        if (nodeContext.getAttribute("src") != null && (src = nodeContext.getAttribute("src")).indexOf("images/icons/emoticons/") != -1) {
            return this.lookupEmoticonString(src.substring(src.indexOf("icons/emoticons/")), wysiwygConverter.getIconManager());
        }
        return "";
    }

    private void initEmoticonMap(IconManager iconManager) {
        HashMap<String, String> newEmoticonMap = new HashMap<String, String>();
        ArrayList<String> symbolList = new ArrayList<String>(Arrays.asList(iconManager.getEmoticonSymbols()));
        Collections.sort(symbolList);
        for (Object e : symbolList) {
            String symbol = (String)e;
            Icon icon = iconManager.getEmoticon(symbol);
            newEmoticonMap.put(icon.getPath(), symbol);
        }
        this.emoticonMap = newEmoticonMap;
    }

    private String lookupEmoticonString(String fileName, IconManager iconManager) {
        String symbol;
        if (this.emoticonMap == null) {
            this.initEmoticonMap(iconManager);
        }
        if ((symbol = this.emoticonMap.get(fileName)) == null) {
            throw new RuntimeException("unrecognised emoticon " + fileName);
        }
        return symbol;
    }
}

