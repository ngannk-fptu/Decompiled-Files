/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;

public interface FixedField {
    public void readFromBytes(byte[] var1) throws ArrayIndexOutOfBoundsException;

    public void readFromStream(InputStream var1) throws IOException;

    public void writeToBytes(byte[] var1) throws ArrayIndexOutOfBoundsException;

    public String toString();
}

