/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.inline;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Emoticon {
    SMILE("smile"),
    SAD("sad"),
    CHEEKY("cheeky"),
    LAUGH("laugh"),
    WINK("wink"),
    THUMBS_UP("thumbs-up"),
    THUMBS_DOWN("thumbs-down"),
    INFORMATION("information"),
    TICK("tick"),
    CROSS("cross"),
    WARNING("warning"),
    PLUS("plus"),
    MINUS("minus"),
    QUESTION("question"),
    LIGHT_ON("light-on"),
    LIGHT_OFF("light-off"),
    YELLOW_STAR("yellow-star"),
    RED_STAR("red-star"),
    GREEN_STAR("green-star"),
    BLUE_STAR("blue-star"),
    HEART("heart", true),
    BROKEN_HEART("broken-heart", true);

    public static final String IDENTIFYING_ATTRIBUTE_NAME = "data-emoticon-name";
    private static final Map<String, Emoticon> lookup;
    private static final List<Emoticon> nonSecret;
    private final String type;
    private final boolean secret;

    private Emoticon(String type) {
        this(type, false);
    }

    private Emoticon(String type, boolean secret) {
        this.type = type;
        this.secret = secret;
    }

    public String getType() {
        return this.type;
    }

    public boolean isSecret() {
        return this.secret;
    }

    public static Emoticon get(String type) {
        return lookup.get(type);
    }

    public static Emoticon[] notSecretValues() {
        return nonSecret.toArray(new Emoticon[nonSecret.size()]);
    }

    static {
        lookup = new HashMap<String, Emoticon>(Emoticon.values().length);
        nonSecret = new ArrayList<Emoticon>();
        for (Emoticon emoticon : EnumSet.allOf(Emoticon.class)) {
            lookup.put(emoticon.getType(), emoticon);
            if (emoticon.isSecret()) continue;
            nonSecret.add(emoticon);
        }
    }
}

