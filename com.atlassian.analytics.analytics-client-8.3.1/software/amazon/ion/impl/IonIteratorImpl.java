/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import software.amazon.ion.IonLob;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonSequence;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.PrivateIonValue;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class IonIteratorImpl
implements Iterator<IonValue> {
    private final ValueFactory _valueFactory;
    private final IonReader _reader;
    private boolean _at_eof;
    private IonValue _curr;
    private IonValue _next;

    public IonIteratorImpl(ValueFactory valueFactory, IonReader input) {
        if (valueFactory == null || input == null) {
            throw new NullPointerException();
        }
        this._valueFactory = valueFactory;
        this._reader = input;
    }

    @Override
    public boolean hasNext() {
        if (this._at_eof) {
            return false;
        }
        if (this._next != null) {
            return true;
        }
        return this.prefetch() != null;
    }

    private IonValue prefetch() {
        assert (!this._at_eof && this._next == null);
        IonType type = this._reader.next();
        if (type == null) {
            this._at_eof = true;
        } else {
            this._next = this.readValue();
        }
        return this._next;
    }

    private IonValue readValue() {
        IonValue v;
        IonType type = this._reader.getType();
        SymbolToken[] annotations = this._reader.getTypeAnnotationSymbols();
        if (this._reader.isNullValue()) {
            v = this._valueFactory.newNull(type);
        } else {
            switch (type) {
                case NULL: {
                    throw new IllegalStateException();
                }
                case BOOL: {
                    v = this._valueFactory.newBool(this._reader.booleanValue());
                    break;
                }
                case INT: {
                    v = this._valueFactory.newInt(this._reader.bigIntegerValue());
                    break;
                }
                case FLOAT: {
                    v = this._valueFactory.newFloat(this._reader.doubleValue());
                    break;
                }
                case DECIMAL: {
                    v = this._valueFactory.newDecimal(this._reader.decimalValue());
                    break;
                }
                case TIMESTAMP: {
                    v = this._valueFactory.newTimestamp(this._reader.timestampValue());
                    break;
                }
                case STRING: {
                    v = this._valueFactory.newString(this._reader.stringValue());
                    break;
                }
                case SYMBOL: {
                    v = this._valueFactory.newSymbol(this._reader.symbolValue());
                    break;
                }
                case BLOB: {
                    IonLob lob = this._valueFactory.newNullBlob();
                    lob.setBytes(this._reader.newBytes());
                    v = lob;
                    break;
                }
                case CLOB: {
                    IonLob lob = this._valueFactory.newNullClob();
                    lob.setBytes(this._reader.newBytes());
                    v = lob;
                    break;
                }
                case STRUCT: {
                    IonStruct struct = this._valueFactory.newEmptyStruct();
                    this._reader.stepIn();
                    while (this._reader.next() != null) {
                        SymbolToken name = this._reader.getFieldNameSymbol();
                        IonValue child = this.readValue();
                        struct.add(name, child);
                    }
                    this._reader.stepOut();
                    v = struct;
                    break;
                }
                case LIST: {
                    IonSequence seq = this._valueFactory.newEmptyList();
                    this._reader.stepIn();
                    while (this._reader.next() != null) {
                        IonValue child = this.readValue();
                        seq.add(child);
                    }
                    this._reader.stepOut();
                    v = seq;
                    break;
                }
                case SEXP: {
                    IonSequence seq = this._valueFactory.newEmptySexp();
                    this._reader.stepIn();
                    while (this._reader.next() != null) {
                        IonValue child = this.readValue();
                        seq.add(child);
                    }
                    this._reader.stepOut();
                    v = seq;
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        SymbolTable symtab = this._reader.getSymbolTable();
        ((PrivateIonValue)v).setSymbolTable(symtab);
        if (annotations.length != 0) {
            ((PrivateIonValue)v).setTypeAnnotationSymbols(annotations);
        }
        return v;
    }

    @Override
    public IonValue next() {
        if (!this._at_eof) {
            this._curr = null;
            if (this._next == null) {
                this.prefetch();
            }
            if (this._next != null) {
                this._curr = this._next;
                this._next = null;
                return this._curr;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

