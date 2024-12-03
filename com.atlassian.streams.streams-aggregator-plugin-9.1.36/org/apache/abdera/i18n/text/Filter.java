/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

public interface Filter {
    public static final Filter NONOPFILTER = new Filter(){

        public boolean accept(int c) {
            return true;
        }
    };

    public boolean accept(int var1);
}

