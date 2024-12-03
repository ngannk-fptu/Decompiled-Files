/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.jvnet.staxex;

import javax.activation.DataHandler;
import org.jvnet.staxex.BinaryText;

public interface MtomEnabled {
    public BinaryText addBinaryText(byte[] var1);

    public BinaryText addBinaryText(String var1, byte[] var2);

    public BinaryText addBinaryText(String var1, DataHandler var2);
}

