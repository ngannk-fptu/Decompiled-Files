/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

public interface FontReplacer {
    public Triplet update(Triplet var1);

    public static class Triplet {
        public String fontName;
        public boolean bold;
        public boolean italic;
    }
}

