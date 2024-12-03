/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.icc;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;

interface IccTagDataType {
    public String getName();

    public int getSignature();

    public void dump(String var1, byte[] var2) throws ImageReadException, IOException;
}

