/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.html.simpleparser;

import java.util.HashMap;
import java.util.Map;

public class StyleSheet {
    public HashMap classMap = new HashMap();
    public HashMap tagMap = new HashMap();

    @Deprecated
    public void applyStyle(String tag, HashMap props) {
        this.applyStyle(tag, (Map<String, String>)props);
    }

    public void applyStyle(String tag, Map<String, String> props) {
        String cm;
        HashMap map = (HashMap)this.tagMap.get(tag.toLowerCase());
        if (map != null) {
            HashMap<String, String> temp = new HashMap<String, String>(map);
            temp.putAll(props);
            props.putAll(temp);
        }
        if ((cm = props.get("class")) == null) {
            return;
        }
        map = (HashMap)this.classMap.get(cm.toLowerCase());
        if (map == null) {
            return;
        }
        props.remove("class");
        HashMap<String, String> temp = new HashMap<String, String>(map);
        temp.putAll(props);
        props.putAll(temp);
    }

    public void loadStyle(String style, Map<String, String> props) {
        this.classMap.put(style.toLowerCase(), props);
    }

    public void loadStyle(String style, String key, String value) {
        style = style.toLowerCase();
        HashMap props = (HashMap)this.classMap.computeIfAbsent(style, k -> new HashMap());
        props.put(key, value);
    }

    public void loadTagStyle(String tag, Map<String, String> props) {
        this.tagMap.put(tag.toLowerCase(), props);
    }

    public void loadTagStyle(String tag, String key, String value) {
        tag = tag.toLowerCase();
        Map props = (Map)this.tagMap.computeIfAbsent(tag, k -> new HashMap());
        props.put(key, value);
    }
}

