/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.InputStream;
import software.amazon.ion.IonValue;
import software.amazon.ion.UnknownSymbolException;

public interface IonLob
extends IonValue {
    public InputStream newInputStream();

    public byte[] getBytes();

    public void setBytes(byte[] var1);

    public void setBytes(byte[] var1, int var2, int var3);

    public int byteSize();

    public IonLob clone() throws UnknownSymbolException;
}

