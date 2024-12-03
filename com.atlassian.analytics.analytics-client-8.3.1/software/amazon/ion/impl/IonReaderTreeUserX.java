/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSymbol;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.SeekableReader;
import software.amazon.ion.Span;
import software.amazon.ion.SpanProvider;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.DowncastingFaceted;
import software.amazon.ion.impl.IonReaderTreeSystem;
import software.amazon.ion.impl.PrivateReaderWriter;
import software.amazon.ion.impl.PrivateUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class IonReaderTreeUserX
extends IonReaderTreeSystem
implements PrivateReaderWriter {
    IonCatalog _catalog;
    private int _symbol_table_top = 0;
    private SymbolTable[] _symbol_table_stack = new SymbolTable[3];

    public IonReaderTreeUserX(IonValue value, IonCatalog catalog) {
        super(value);
        this._catalog = catalog;
    }

    @Override
    public IonType next() {
        if (!this.next_helper_user()) {
            this._curr = null;
            return null;
        }
        this._curr = this._next;
        this._next = null;
        return this._curr.getType();
    }

    private boolean next_helper_user() {
        IonType next_type;
        if (this._eof) {
            return false;
        }
        if (this._next != null) {
            return true;
        }
        this.clear_system_value_stack();
        while (true) {
            next_type = this.next_helper_system();
            if (this._top != 0 || !(this._parent instanceof IonDatagram)) break;
            if (IonType.SYMBOL.equals((Object)next_type)) {
                String name;
                assert (this._next instanceof IonSymbol);
                IonSymbol sym = (IonSymbol)((Object)this._next);
                if (sym.isNullValue()) break;
                int sid = sym.symbolValue().getSid();
                if (sid == -1 && (name = sym.stringValue()) != null) {
                    sid = this._system.getSystemSymbolTable().findSymbol(name);
                }
                if (sid != 2 || this._next.getTypeAnnotationSymbols().length != 0) break;
                SymbolTable symbols = this._system.getSystemSymbolTable();
                this.set_symbol_table(symbols);
                this.push_symbol_table(symbols);
                this._next = null;
                continue;
            }
            if (!IonType.STRUCT.equals((Object)next_type) || !this._next.hasTypeAnnotation("$ion_symbol_table")) break;
            assert (this._next instanceof IonStruct);
            IonReaderTreeUserX reader = new IonReaderTreeUserX(this._next, this._catalog);
            SymbolTable symtab = PrivateUtils.newLocalSymtab((ValueFactory)this._system, this._system.getSystemSymbolTable(), this._system.getCatalog(), reader, false);
            this.set_symbol_table(symtab);
            this.push_symbol_table(symtab);
            this._next = null;
        }
        return next_type != null;
    }

    private void clear_system_value_stack() {
        while (this._symbol_table_top > 0) {
            --this._symbol_table_top;
            this._symbol_table_stack[this._symbol_table_top] = null;
        }
    }

    private void push_symbol_table(SymbolTable symbols) {
        assert (symbols != null);
        if (this._symbol_table_top >= this._symbol_table_stack.length) {
            int new_len = this._symbol_table_stack.length * 2;
            SymbolTable[] temp = new SymbolTable[new_len];
            System.arraycopy(this._symbol_table_stack, 0, temp, 0, this._symbol_table_stack.length);
            this._symbol_table_stack = temp;
        }
        this._symbol_table_stack[this._symbol_table_top++] = symbols;
    }

    @Override
    public SymbolTable pop_passed_symbol_table() {
        if (this._symbol_table_top <= 0) {
            return null;
        }
        --this._symbol_table_top;
        SymbolTable symbols = this._symbol_table_stack[this._symbol_table_top];
        this._symbol_table_stack[this._symbol_table_top] = null;
        return symbols;
    }

    private final Span currentSpanImpl() {
        if (this._curr == null) {
            throw new IllegalStateException("Reader has no current value");
        }
        TreeSpan span = new TreeSpan();
        span._value = this._curr;
        return span;
    }

    private void hoistImpl(Span span) {
        if (!(span instanceof TreeSpan)) {
            throw new IllegalArgumentException("Span not appropriate for this reader");
        }
        TreeSpan treeSpan = (TreeSpan)span;
        this.re_init(treeSpan._value, true);
    }

    @Override
    public <T> T asFacet(Class<T> facetType) {
        if (facetType == SeekableReader.class || facetType == SpanProvider.class) {
            return facetType.cast(new SeekableReaderFacet());
        }
        return super.asFacet(facetType);
    }

    private class SeekableReaderFacet
    implements SeekableReader {
        private SeekableReaderFacet() {
        }

        public Span currentSpan() {
            return IonReaderTreeUserX.this.currentSpanImpl();
        }

        public void hoist(Span span) {
            IonReaderTreeUserX.this.hoistImpl(span);
        }
    }

    private static final class TreeSpan
    extends DowncastingFaceted
    implements Span {
        IonValue _value;

        private TreeSpan() {
        }
    }
}

