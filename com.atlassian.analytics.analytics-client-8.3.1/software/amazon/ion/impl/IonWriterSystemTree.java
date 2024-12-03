/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.amazon.ion.IonBlob;
import software.amazon.ion.IonBool;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonClob;
import software.amazon.ion.IonContainer;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonDecimal;
import software.amazon.ion.IonException;
import software.amazon.ion.IonFloat;
import software.amazon.ion.IonInt;
import software.amazon.ion.IonSequence;
import software.amazon.ion.IonString;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSymbol;
import software.amazon.ion.IonTimestamp;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.IonWriterSystem;
import software.amazon.ion.impl.PrivateIonDatagram;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.SymbolTokenImpl;
import software.amazon.ion.system.IonWriterBuilder;

final class IonWriterSystemTree
extends IonWriterSystem {
    private final ValueFactory _factory;
    private final IonCatalog _catalog;
    private final int _initialDepth;
    private boolean _in_struct;
    private IonContainer _current_parent;
    private int _parent_stack_top = 0;
    private IonContainer[] _parent_stack = new IonContainer[10];

    protected IonWriterSystemTree(SymbolTable defaultSystemSymbolTable, IonCatalog catalog, IonContainer rootContainer, IonWriterBuilder.InitialIvmHandling initialIvmHandling) {
        super(defaultSystemSymbolTable, initialIvmHandling, IonWriterBuilder.IvmMinimizing.ADJACENT);
        if (rootContainer == null) {
            throw new NullPointerException();
        }
        this._factory = rootContainer.getSystem();
        this._catalog = catalog;
        this._current_parent = rootContainer;
        this._in_struct = this._current_parent instanceof IonStruct;
        int depth = 0;
        if (!(rootContainer instanceof IonDatagram)) {
            IonContainer c = rootContainer;
            do {
                ++depth;
            } while ((c = c.getContainer()) != null);
        }
        this._initialDepth = depth;
    }

    public int getDepth() {
        return this._parent_stack_top + this._initialDepth;
    }

    protected IonType getContainer() {
        IonType containerType = this._parent_stack_top > 0 ? this._parent_stack[this._parent_stack_top - 1].getType() : IonType.DATAGRAM;
        return containerType;
    }

    public boolean isInStruct() {
        return this._in_struct;
    }

    protected IonValue get_root() {
        IonContainer container = this._parent_stack_top > 0 ? this._parent_stack[0] : this._current_parent;
        return container;
    }

    private void pushParent(IonContainer newParent) {
        int oldlen = this._parent_stack.length;
        if (this._parent_stack_top >= oldlen) {
            int newlen = oldlen * 2;
            IonContainer[] temp = new IonContainer[newlen];
            System.arraycopy(this._parent_stack, 0, temp, 0, oldlen);
            this._parent_stack = temp;
        }
        this._parent_stack[this._parent_stack_top++] = this._current_parent;
        this._current_parent = newParent;
        this._in_struct = this._current_parent instanceof IonStruct;
    }

    private void popParent() {
        if (this._parent_stack_top < 1) {
            throw new IllegalStateException("Cannot stepOut any further, already at top level.");
        }
        --this._parent_stack_top;
        this._current_parent = this._parent_stack[this._parent_stack_top];
        this._in_struct = this._current_parent instanceof IonStruct;
    }

    private void append(IonValue value) {
        try {
            super.startValue();
        }
        catch (IOException e) {
            throw new IonException(e);
        }
        if (this.hasAnnotations()) {
            SymbolToken[] annotations = this.getTypeAnnotationSymbols();
            ((PrivateIonValue)value).setTypeAnnotationSymbols(annotations);
            this.clearAnnotations();
        }
        if (this._in_struct) {
            SymbolToken sym = this.assumeFieldNameSymbol();
            IonStruct struct = (IonStruct)this._current_parent;
            struct.add(sym, value);
            this.clearFieldName();
        } else {
            ((IonSequence)this._current_parent).add(value);
        }
    }

    public void stepIn(IonType containerType) throws IOException {
        IonContainer v;
        switch (containerType) {
            case LIST: {
                v = this._factory.newEmptyList();
                break;
            }
            case SEXP: {
                v = this._factory.newEmptySexp();
                break;
            }
            case STRUCT: {
                v = this._factory.newEmptyStruct();
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        this.append(v);
        this.pushParent(v);
    }

    public void stepOut() throws IOException {
        IonContainer prior = this._current_parent;
        this.popParent();
        if (this._current_parent instanceof IonDatagram && PrivateUtils.valueIsLocalSymbolTable(prior)) {
            SymbolTable symbol_table = PrivateUtils.newLocalSymtab(this._default_system_symbol_table, this._catalog, (IonStruct)prior);
            this.setSymbolTable(symbol_table);
        }
    }

    void writeIonVersionMarkerAsIs(SymbolTable systemSymtab) throws IOException {
        this.startValue();
        IonValue root = this.get_root();
        ((PrivateIonDatagram)root).appendTrailingSymbolTable(systemSymtab);
        this.endValue();
    }

    void writeLocalSymtab(SymbolTable symtab) throws IOException {
        IonValue root = this.get_root();
        ((PrivateIonDatagram)root).appendTrailingSymbolTable(symtab);
        super.writeLocalSymtab(symtab);
    }

    final SymbolTable inject_local_symbol_table() throws IOException {
        return PrivateUtils.newLocalSymtab(this._factory, this.getSymbolTable(), new SymbolTable[0]);
    }

    public void writeNull(IonType type) throws IOException {
        IonValue v = this._factory.newNull(type);
        this.append(v);
    }

    public void writeBool(boolean value) throws IOException {
        IonBool v = this._factory.newBool(value);
        this.append(v);
    }

    public void writeInt(int value) throws IOException {
        IonInt v = this._factory.newInt(value);
        this.append(v);
    }

    public void writeInt(long value) throws IOException {
        IonInt v = this._factory.newInt(value);
        this.append(v);
    }

    public void writeInt(BigInteger value) throws IOException {
        IonInt v = this._factory.newInt(value);
        this.append(v);
    }

    public void writeFloat(double value) throws IOException {
        IonFloat v = this._factory.newNullFloat();
        v.setValue(value);
        this.append(v);
    }

    public void writeDecimal(BigDecimal value) throws IOException {
        IonDecimal v = this._factory.newNullDecimal();
        v.setValue(value);
        this.append(v);
    }

    public void writeTimestamp(Timestamp value) throws IOException {
        IonTimestamp v = this._factory.newTimestamp(value);
        this.append(v);
    }

    public void writeString(String value) throws IOException {
        IonString v = this._factory.newString(value);
        this.append(v);
    }

    void writeSymbolAsIs(int symbolId) {
        String name = this.getSymbolTable().findKnownSymbol(symbolId);
        SymbolTokenImpl is = new SymbolTokenImpl(name, symbolId);
        IonSymbol v = this._factory.newSymbol(is);
        this.append(v);
    }

    public void writeSymbolAsIs(String value) {
        IonSymbol v = this._factory.newSymbol(value);
        this.append(v);
    }

    public void writeClob(byte[] value, int start, int len) throws IOException {
        IonClob v = this._factory.newClob(value, start, len);
        this.append(v);
    }

    public void writeBlob(byte[] value, int start, int len) throws IOException {
        IonBlob v = this._factory.newBlob(value, start, len);
        this.append(v);
    }

    public void flush() {
    }

    public void close() {
    }
}

