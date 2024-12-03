/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import java.util.HashMap;
import java.util.Map;

public class ImageEffectAppliedEvent
extends ConfluenceEvent {
    private static Map<String, String> effectMap = new HashMap<String, String>();

    public ImageEffectAppliedEvent(String src) {
        super((Object)src);
    }

    public String getEffectName() {
        String effectStr = this.getRawEffectList();
        String name = effectMap.get(effectStr);
        if (name != null) {
            return name;
        }
        return "Unknown";
    }

    public String getRawEffectList() {
        return (String)this.source;
    }

    static {
        effectMap.put("border-simple,blur-border,tape", "Taped");
        effectMap.put("border-polaroid,blur-border", "Instant Camera");
        effectMap.put("border-simple,shadow-kn", "Curl Shadow");
        effectMap.put("border-simple,blur-border", "Snapshot");
        effectMap.put("drop-shadow", "Drop Shadow");
    }
}

