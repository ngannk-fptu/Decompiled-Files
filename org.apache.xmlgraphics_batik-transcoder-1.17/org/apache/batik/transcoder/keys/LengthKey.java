/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder.keys;

import org.apache.batik.transcoder.TranscodingHints;

public class LengthKey
extends TranscodingHints.Key {
    @Override
    public boolean isCompatibleValue(Object v) {
        return v instanceof Float && ((Float)v).floatValue() > 0.0f;
    }
}

