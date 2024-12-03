/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.IOException;
import software.amazon.ion.IonLob;
import software.amazon.ion.NullValueException;
import software.amazon.ion.UnknownSymbolException;

public interface IonBlob
extends IonLob {
    public void printBase64(Appendable var1) throws NullValueException, IOException;

    public IonBlob clone() throws UnknownSymbolException;
}

