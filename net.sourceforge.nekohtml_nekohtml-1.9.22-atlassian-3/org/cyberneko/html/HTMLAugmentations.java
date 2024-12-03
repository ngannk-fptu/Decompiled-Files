/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 */
package org.cyberneko.html;

import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.xerces.xni.Augmentations;
import org.cyberneko.html.HTMLScanner;

public class HTMLAugmentations
implements Augmentations {
    protected final Hashtable fItems = new Hashtable();

    public HTMLAugmentations() {
    }

    HTMLAugmentations(Augmentations augs) {
        Enumeration keys = augs.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            Object value = augs.getItem(key);
            if (value instanceof HTMLScanner.LocationItem) {
                value = new HTMLScanner.LocationItem((HTMLScanner.LocationItem)value);
            }
            this.fItems.put(key, value);
        }
    }

    public void removeAllItems() {
        this.fItems.clear();
    }

    public void clear() {
        this.fItems.clear();
    }

    public Object putItem(String key, Object item) {
        return this.fItems.put(key, item);
    }

    public Object getItem(String key) {
        return this.fItems.get(key);
    }

    public Object removeItem(String key) {
        return this.fItems.remove(key);
    }

    public Enumeration keys() {
        return this.fItems.keys();
    }
}

