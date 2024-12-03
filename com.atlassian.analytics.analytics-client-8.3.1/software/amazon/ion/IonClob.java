/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.Reader;
import java.nio.charset.Charset;
import software.amazon.ion.IonLob;
import software.amazon.ion.IonValue;
import software.amazon.ion.UnknownSymbolException;

public interface IonClob
extends IonValue,
IonLob {
    public Reader newReader(Charset var1);

    public String stringValue(Charset var1);

    public IonClob clone() throws UnknownSymbolException;
}

