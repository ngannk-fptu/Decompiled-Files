/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;

interface Subsetter {
    public void addToSubset(int var1);

    public void subset() throws IOException;
}

