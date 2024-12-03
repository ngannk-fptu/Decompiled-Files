/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import org.apache.poi.util.LittleEndianOutput;

public interface DelayableLittleEndianOutput
extends LittleEndianOutput {
    public LittleEndianOutput createDelayedOutput(int var1);
}

