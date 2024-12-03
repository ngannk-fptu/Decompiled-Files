/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder.keys;

import org.apache.batik.transcoder.TranscodingHints;

public class FloatKey
extends TranscodingHints.Key {
    @Override
    public boolean isCompatibleValue(Object v) {
        return v instanceof Float;
    }
}

