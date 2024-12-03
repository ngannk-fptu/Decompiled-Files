/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset.sax;

import org.xml.sax.Attributes;

public interface EncodingAlgorithmAttributes
extends Attributes {
    public String getAlgorithmURI(int var1);

    public int getAlgorithmIndex(int var1);

    public Object getAlgorithmData(int var1);

    public String getAlpababet(int var1);

    public boolean getToIndex(int var1);
}

