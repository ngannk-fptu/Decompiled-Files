/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonValue;
import software.amazon.ion.UnknownSymbolException;

public interface IonNull
extends IonValue {
    public IonNull clone() throws UnknownSymbolException;
}

