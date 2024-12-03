/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.util.Map;
import software.amazon.ion.ContainedValueException;
import software.amazon.ion.IonContainer;
import software.amazon.ion.IonValue;
import software.amazon.ion.NullValueException;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.UnknownSymbolException;
import software.amazon.ion.ValueFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface IonStruct
extends IonContainer {
    @Override
    public int size() throws NullValueException;

    public boolean containsKey(Object var1);

    public boolean containsValue(Object var1);

    public IonValue get(String var1);

    public void put(String var1, IonValue var2) throws ContainedValueException;

    public ValueFactory put(String var1);

    public void putAll(Map<? extends String, ? extends IonValue> var1);

    public void add(String var1, IonValue var2) throws ContainedValueException;

    public void add(SymbolToken var1, IonValue var2) throws ContainedValueException;

    public ValueFactory add(String var1);

    public IonValue remove(String var1);

    public boolean removeAll(String ... var1);

    public boolean retainAll(String ... var1);

    @Override
    public IonStruct clone() throws UnknownSymbolException;

    public IonStruct cloneAndRemove(String ... var1) throws UnknownSymbolException;

    public IonStruct cloneAndRetain(String ... var1) throws UnknownSymbolException;
}

