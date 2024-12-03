/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import software.amazon.ion.ContainedValueException;
import software.amazon.ion.IonContainer;
import software.amazon.ion.IonException;
import software.amazon.ion.IonSequence;
import software.amazon.ion.IonValue;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.UnknownSymbolException;
import software.amazon.ion.ValueFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface IonDatagram
extends IonSequence {
    @Override
    public void add(int var1, IonValue var2) throws ContainedValueException, NullPointerException;

    @Override
    public ValueFactory add(int var1) throws ContainedValueException, NullPointerException;

    @Override
    public boolean addAll(int var1, Collection<? extends IonValue> var2);

    @Override
    public IonValue set(int var1, IonValue var2);

    @Override
    public boolean isNullValue();

    @Override
    public IonContainer getContainer();

    @Override
    public int size();

    public int systemSize();

    @Override
    public IonValue get(int var1) throws IndexOutOfBoundsException;

    public IonValue systemGet(int var1) throws IndexOutOfBoundsException;

    @Override
    public Iterator<IonValue> iterator();

    public ListIterator<IonValue> systemIterator();

    public int byteSize() throws IonException;

    public byte[] getBytes() throws IonException;

    public int getBytes(OutputStream var1) throws IOException, IonException;

    @Override
    public SymbolTable getSymbolTable();

    @Override
    public void addTypeAnnotation(String var1);

    @Override
    public void makeNull();

    @Override
    public boolean retainAll(Collection<?> var1);

    @Override
    public IonDatagram clone() throws UnknownSymbolException;
}

