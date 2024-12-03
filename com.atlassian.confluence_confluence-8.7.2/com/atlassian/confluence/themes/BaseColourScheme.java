/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.themes.AbstractColourScheme;
import com.atlassian.confluence.themes.ColourScheme;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseColourScheme
extends AbstractColourScheme
implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(BaseColourScheme.class);
    protected Map<String, String> colours = new HashMap<String, String>();

    public BaseColourScheme() {
    }

    public BaseColourScheme(ColourScheme colourScheme) {
        for (String key : ORDERED_KEYS) {
            this.colours.put(key, colourScheme.get(key));
        }
    }

    public BaseColourScheme(Map colours) {
        if (colours != null) {
            this.colours = colours;
        }
    }

    public void set(String colourName, String value) {
        if (!ORDERED_KEYS.contains(colourName)) {
            if (!DEPRECATED_KEYS.contains(colourName)) {
                throw new IllegalArgumentException(colourName + " is not a valid colour name");
            }
            log.warn("The key {} has been deprecated and could be removed in a future version.", (Object)colourName);
        }
        if (value == null || value.length() == 0) {
            this.colours.remove(colourName);
        } else {
            this.colours.put(colourName, value);
        }
    }

    @Override
    public String get(String colourName) {
        return this.colours.get(colourName);
    }
}

