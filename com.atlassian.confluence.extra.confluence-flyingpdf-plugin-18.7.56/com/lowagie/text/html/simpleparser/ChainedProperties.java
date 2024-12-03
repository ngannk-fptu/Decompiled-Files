/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.lowagie.text.html.simpleparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChainedProperties {
    public static final int[] fontSizes = new int[]{8, 10, 12, 14, 18, 24, 36};
    @Deprecated
    public ArrayList chain = new ArrayList();

    @Nullable
    public String getProperty(String key) {
        return this.findProperty(key).orElse(null);
    }

    @Nonnull
    public Optional<String> findProperty(String key) {
        for (int k = this.chain.size() - 1; k >= 0; --k) {
            Object[] obj = (Object[])this.chain.get(k);
            HashMap prop = (HashMap)obj[1];
            String ret = (String)prop.get(key);
            if (ret == null) continue;
            return Optional.of(ret);
        }
        return Optional.empty();
    }

    @Nonnull
    public String getOrDefault(String key, String defaultValue) {
        return this.findProperty(key).orElse(defaultValue);
    }

    public boolean hasProperty(String key) {
        for (int k = this.chain.size() - 1; k >= 0; --k) {
            Object[] obj = (Object[])this.chain.get(k);
            HashMap prop = (HashMap)obj[1];
            if (!prop.containsKey(key)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public void addToChain(String key, HashMap prop) {
        this.addToChain(key, (Map<String, String>)prop);
    }

    public void addToChain(String key, Map<String, String> prop) {
        String value = prop.get("size");
        if (value != null) {
            if (value.endsWith("pt")) {
                prop.put("size", value.substring(0, value.length() - 2));
            } else {
                int s = 0;
                if (value.startsWith("+") || value.startsWith("-")) {
                    String old = this.getOrDefault("basefontsize", "12");
                    float f = Float.parseFloat(old);
                    int c = (int)f;
                    for (int k = fontSizes.length - 1; k >= 0; --k) {
                        if (c < fontSizes[k]) continue;
                        s = k;
                        break;
                    }
                    int inc = Integer.parseInt(value.startsWith("+") ? value.substring(1) : value);
                    s += inc;
                } else {
                    try {
                        s = Integer.parseInt(value) - 1;
                    }
                    catch (NumberFormatException nfe) {
                        s = 0;
                    }
                }
                if (s < 0) {
                    s = 0;
                } else if (s >= fontSizes.length) {
                    s = fontSizes.length - 1;
                }
                prop.put("size", Integer.toString(fontSizes[s]));
            }
        }
        this.chain.add(new Object[]{key, prop});
    }

    public void removeChain(String key) {
        for (int k = this.chain.size() - 1; k >= 0; --k) {
            if (!key.equals(((Object[])this.chain.get(k))[0])) continue;
            this.chain.remove(k);
            return;
        }
    }
}

