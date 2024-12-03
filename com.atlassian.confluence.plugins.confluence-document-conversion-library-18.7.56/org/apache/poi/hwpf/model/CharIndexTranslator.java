/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.util.Internal;

@Internal
public interface CharIndexTranslator {
    public int getByteIndex(int var1);

    public int[][] getCharIndexRanges(int var1, int var2);

    public boolean isIndexInTable(int var1);

    public int lookIndexForward(int var1);

    public int lookIndexBackward(int var1);
}

