/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.util.Collection;
import software.amazon.ion.IonSequence;
import software.amazon.ion.IonValue;
import software.amazon.ion.UnknownSymbolException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface IonSexp
extends IonValue,
IonSequence,
Collection<IonValue> {
    @Override
    public IonSexp clone() throws UnknownSymbolException;
}

