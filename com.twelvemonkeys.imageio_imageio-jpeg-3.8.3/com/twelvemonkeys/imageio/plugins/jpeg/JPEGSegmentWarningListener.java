/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

interface JPEGSegmentWarningListener {
    public static final JPEGSegmentWarningListener NULL_LISTENER = new JPEGSegmentWarningListener(){

        @Override
        public void warningOccurred(String string) {
        }
    };

    public void warningOccurred(String var1);
}

