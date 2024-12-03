/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.util.Iterator;
import software.amazon.ion.IonValue;
import software.amazon.ion.NullValueException;
import software.amazon.ion.UnknownSymbolException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface IonContainer
extends IonValue,
Iterable<IonValue> {
    public int size();

    @Override
    public Iterator<IonValue> iterator();

    public boolean remove(IonValue var1);

    public boolean isEmpty() throws NullValueException;

    public void clear();

    public void makeNull();

    @Override
    public IonContainer clone() throws UnknownSymbolException;
}

