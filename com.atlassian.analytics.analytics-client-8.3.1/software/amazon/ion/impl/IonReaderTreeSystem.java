/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import software.amazon.ion.Decimal;
import software.amazon.ion.IntegerSize;
import software.amazon.ion.IonBool;
import software.amazon.ion.IonContainer;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonDecimal;
import software.amazon.ion.IonException;
import software.amazon.ion.IonFloat;
import software.amazon.ion.IonInt;
import software.amazon.ion.IonLob;
import software.amazon.ion.IonNull;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSymbol;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonText;
import software.amazon.ion.IonTimestamp;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.impl.PrivateIonContainer;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.PrivateReaderWriter;
import software.amazon.ion.impl.PrivateUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class IonReaderTreeSystem
implements IonReader,
PrivateReaderWriter {
    protected IonSystem _system;
    protected SymbolTable _symbols;
    protected Iterator<IonValue> _iter;
    protected IonValue _parent;
    protected PrivateIonValue _next;
    protected PrivateIonValue _curr;
    protected boolean _eof;
    private Object[] _stack = new Object[10];
    protected int _top;
    private boolean _hoisted;
    private final PrivateIonValue.SymbolTableProvider _symbolTableAccessor;

    public IonReaderTreeSystem(IonValue value) {
        if (value == null) {
            this._symbolTableAccessor = null;
        } else {
            this._system = value.getSystem();
            this.re_init(value, false);
            this._symbolTableAccessor = new PrivateIonValue.SymbolTableProvider(){

                public SymbolTable getSymbolTable() {
                    return null == IonReaderTreeSystem.this._symbols ? IonReaderTreeSystem.this._system.getSystemSymbolTable() : IonReaderTreeSystem.this._symbols;
                }
            };
        }
    }

    @Override
    public <T> T asFacet(Class<T> facetType) {
        return null;
    }

    void re_init(IonValue value, boolean hoisted) {
        this._symbols = null;
        this._curr = null;
        this._eof = false;
        this._top = 0;
        this._hoisted = hoisted;
        if (value instanceof IonDatagram) {
            assert (value instanceof PrivateIonContainer);
            IonDatagram dg = (IonDatagram)value;
            this._parent = dg;
            this._next = null;
            this._iter = dg.systemIterator();
        } else {
            this._parent = hoisted ? null : value.getContainer();
            this._next = (PrivateIonValue)value;
        }
    }

    @Override
    public void close() {
        this._eof = true;
    }

    protected void set_symbol_table(SymbolTable symtab) {
        this._symbols = symtab;
    }

    private void push() {
        int oldlen = this._stack.length;
        if (this._top + 1 >= oldlen) {
            int newlen = oldlen * 2;
            Object[] temp = new Object[newlen];
            System.arraycopy(this._stack, 0, temp, 0, oldlen);
            this._stack = temp;
        }
        this._stack[this._top++] = this._parent;
        this._stack[this._top++] = this._iter;
    }

    private void pop() {
        assert (this._top >= 2);
        --this._top;
        this._iter = (Iterator)this._stack[this._top];
        this._stack[this._top] = null;
        --this._top;
        this._parent = (IonValue)this._stack[this._top];
        this._stack[this._top] = null;
        this._eof = false;
    }

    @Override
    public IonType next() {
        if (this._next == null && this.next_helper_system() == null) {
            this._curr = null;
            return null;
        }
        this._curr = this._next;
        this._next = null;
        return this._curr.getType();
    }

    IonType next_helper_system() {
        if (this._eof) {
            return null;
        }
        if (this._next != null) {
            return this._next.getType();
        }
        if (this._iter != null && this._iter.hasNext()) {
            this._next = (PrivateIonValue)this._iter.next();
        }
        if (this._eof = this._next == null) {
            return null;
        }
        return this._next.getType();
    }

    @Override
    public final void stepIn() {
        if (!(this._curr instanceof IonContainer)) {
            throw new IllegalStateException("current value must be a container");
        }
        this.push();
        this._parent = this._curr;
        this._iter = new Children((IonContainer)((Object)this._curr));
        this._curr = null;
    }

    @Override
    public final void stepOut() {
        if (this._top < 1) {
            throw new IllegalStateException("Cannot stepOut any further, already at top level.");
        }
        this.pop();
        this._curr = null;
    }

    @Override
    public final int getDepth() {
        return this._top / 2;
    }

    @Override
    public SymbolTable getSymbolTable() {
        SymbolTable symboltable = null;
        if (this._curr != null) {
            symboltable = this._curr.getSymbolTable();
        } else if (this._parent != null) {
            symboltable = this._parent.getSymbolTable();
        }
        return symboltable;
    }

    @Override
    public IonType getType() {
        return this._curr == null ? null : this._curr.getType();
    }

    @Override
    public final String[] getTypeAnnotations() {
        if (this._curr == null) {
            throw new IllegalStateException();
        }
        return this._curr.getTypeAnnotations();
    }

    @Override
    public final SymbolToken[] getTypeAnnotationSymbols() {
        if (this._curr == null) {
            throw new IllegalStateException();
        }
        return this._curr.getTypeAnnotationSymbols(this._symbolTableAccessor);
    }

    @Override
    public final Iterator<String> iterateTypeAnnotations() {
        String[] annotations = this.getTypeAnnotations();
        return PrivateUtils.stringIterator(annotations);
    }

    @Override
    public boolean isInStruct() {
        return this._parent instanceof IonStruct;
    }

    @Override
    public boolean isNullValue() {
        if (this._curr instanceof IonNull) {
            return true;
        }
        if (this._curr == null) {
            throw new IllegalStateException("must call next() before isNullValue()");
        }
        return this._curr.isNullValue();
    }

    @Override
    public String getFieldName() {
        return this._curr == null || this._hoisted && this._top == 0 ? null : this._curr.getFieldName();
    }

    @Override
    public final SymbolToken getFieldNameSymbol() {
        if (this._curr == null || this._hoisted && this._top == 0) {
            return null;
        }
        return this._curr.getFieldNameSymbol(this._symbolTableAccessor);
    }

    @Override
    public boolean booleanValue() {
        if (this._curr instanceof IonBool) {
            return ((IonBool)((Object)this._curr)).booleanValue();
        }
        throw new IllegalStateException("current value is not a boolean");
    }

    @Override
    public int intValue() {
        if (this._curr instanceof IonInt) {
            return ((IonInt)((Object)this._curr)).intValue();
        }
        if (this._curr instanceof IonFloat) {
            return (int)((IonFloat)((Object)this._curr)).doubleValue();
        }
        if (this._curr instanceof IonDecimal) {
            return (int)((IonDecimal)((Object)this._curr)).doubleValue();
        }
        throw new IllegalStateException("current value is not an ion int, float, or decimal");
    }

    @Override
    public long longValue() {
        if (this._curr instanceof IonInt) {
            return ((IonInt)((Object)this._curr)).longValue();
        }
        if (this._curr instanceof IonFloat) {
            return (long)((IonFloat)((Object)this._curr)).doubleValue();
        }
        if (this._curr instanceof IonDecimal) {
            return (long)((IonDecimal)((Object)this._curr)).doubleValue();
        }
        throw new IllegalStateException("current value is not an ion int, float, or decimal");
    }

    @Override
    public BigInteger bigIntegerValue() {
        if (this._curr instanceof IonInt) {
            return ((IonInt)((Object)this._curr)).bigIntegerValue();
        }
        if (this._curr instanceof IonFloat) {
            BigDecimal bd = ((IonFloat)((Object)this._curr)).bigDecimalValue();
            return bd == null ? null : bd.toBigInteger();
        }
        if (this._curr instanceof IonDecimal) {
            BigDecimal bd = ((IonDecimal)((Object)this._curr)).bigDecimalValue();
            return bd == null ? null : bd.toBigInteger();
        }
        throw new IllegalStateException("current value is not an ion int, float, or decimal");
    }

    @Override
    public double doubleValue() {
        if (this._curr instanceof IonFloat) {
            return ((IonFloat)((Object)this._curr)).doubleValue();
        }
        if (this._curr instanceof IonDecimal) {
            return ((IonDecimal)((Object)this._curr)).doubleValue();
        }
        throw new IllegalStateException("current value is not an ion float or decimal");
    }

    @Override
    public BigDecimal bigDecimalValue() {
        if (this._curr instanceof IonDecimal) {
            return ((IonDecimal)((Object)this._curr)).bigDecimalValue();
        }
        throw new IllegalStateException("current value is not an ion decimal");
    }

    @Override
    public Decimal decimalValue() {
        if (this._curr instanceof IonDecimal) {
            return ((IonDecimal)((Object)this._curr)).decimalValue();
        }
        throw new IllegalStateException("current value is not an ion decimal");
    }

    @Override
    public Timestamp timestampValue() {
        if (this._curr instanceof IonTimestamp) {
            return ((IonTimestamp)((Object)this._curr)).timestampValue();
        }
        throw new IllegalStateException("current value is not a timestamp");
    }

    @Override
    public Date dateValue() {
        if (this._curr instanceof IonTimestamp) {
            return ((IonTimestamp)((Object)this._curr)).dateValue();
        }
        throw new IllegalStateException("current value is not an ion timestamp");
    }

    @Override
    public String stringValue() {
        if (this._curr instanceof IonText) {
            return ((IonText)((Object)this._curr)).stringValue();
        }
        throw new IllegalStateException("current value is not a symbol or string");
    }

    @Override
    public SymbolToken symbolValue() {
        if (!(this._curr instanceof IonSymbol)) {
            throw new IllegalStateException();
        }
        if (this._curr.isNullValue()) {
            return null;
        }
        return ((IonSymbol)((Object)this._curr)).symbolValue();
    }

    @Override
    public int byteSize() {
        if (this._curr instanceof IonLob) {
            IonLob lob = (IonLob)((Object)this._curr);
            return lob.byteSize();
        }
        throw new IllegalStateException("current value is not an ion blob or clob");
    }

    @Override
    public byte[] newBytes() {
        if (this._curr instanceof IonLob) {
            int retlen;
            IonLob lob = (IonLob)((Object)this._curr);
            int loblen = lob.byteSize();
            byte[] buffer = new byte[loblen];
            InputStream is = lob.newInputStream();
            try {
                retlen = PrivateUtils.readFully(is, buffer, 0, loblen);
                is.close();
            }
            catch (IOException e) {
                throw new IonException(e);
            }
            assert (retlen != -1 ? retlen == loblen : loblen == 0);
            return buffer;
        }
        throw new IllegalStateException("current value is not an ion blob or clob");
    }

    @Override
    public int getBytes(byte[] buffer, int offset, int len) {
        if (this._curr instanceof IonLob) {
            int retlen;
            IonLob lob = (IonLob)((Object)this._curr);
            int loblen = lob.byteSize();
            if (loblen > len) {
                throw new IllegalArgumentException("insufficient space in buffer for this value");
            }
            InputStream is = lob.newInputStream();
            try {
                retlen = PrivateUtils.readFully(is, buffer, 0, loblen);
                is.close();
            }
            catch (IOException e) {
                throw new IonException(e);
            }
            assert (retlen == loblen);
            return retlen;
        }
        throw new IllegalStateException("current value is not an ion blob or clob");
    }

    public IonValue getIonValue(IonSystem sys) {
        return this._curr;
    }

    public String valueToString() {
        return this._curr == null ? null : this._curr.toString();
    }

    @Override
    public SymbolTable pop_passed_symbol_table() {
        return null;
    }

    @Override
    public IntegerSize getIntegerSize() {
        if (this._curr instanceof IonInt) {
            return ((IonInt)((Object)this._curr)).getIntegerSize();
        }
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class Children
    implements Iterator<IonValue> {
        boolean _eof;
        int _next_idx;
        PrivateIonContainer _parent;
        IonValue _curr;

        Children(IonContainer parent) {
            if (parent instanceof PrivateIonContainer) {
                this._parent = (PrivateIonContainer)parent;
                this._next_idx = 0;
                this._curr = null;
                if (this._parent.isNullValue()) {
                    this._eof = true;
                }
            } else {
                throw new UnsupportedOperationException("this only supports IonContainerImpl instances");
            }
        }

        @Override
        public boolean hasNext() {
            if (this._eof) {
                return false;
            }
            int len = this._parent.get_child_count();
            if (this._next_idx > 0) {
                int ii = this._next_idx - 1;
                this._next_idx = len;
                while (ii < len) {
                    if (this._curr != this._parent.get_child(ii)) continue;
                    this._next_idx = ii + 1;
                    break;
                }
            }
            if (this._next_idx >= this._parent.get_child_count()) {
                this._eof = true;
            }
            return !this._eof;
        }

        @Override
        public IonValue next() {
            if (!this.hasNext()) {
                this._curr = null;
            } else {
                this._curr = this._parent.get_child(this._next_idx);
                ++this._next_idx;
            }
            return this._curr;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

