/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonType;
import software.amazon.ion.OffsetSpan;
import software.amazon.ion.SeekableReader;
import software.amazon.ion.Span;
import software.amazon.ion.SpanProvider;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.DowncastingFaceted;
import software.amazon.ion.impl.IonReaderBinarySystemX;
import software.amazon.ion.impl.PrivateByteTransferReader;
import software.amazon.ion.impl.PrivateByteTransferSink;
import software.amazon.ion.impl.PrivateReaderWriter;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.UnifiedInputStreamX;
import software.amazon.ion.impl.UnifiedSavePointManagerX;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class IonReaderBinaryUserX
extends IonReaderBinarySystemX
implements PrivateReaderWriter {
    private final int _physical_start_offset;
    IonCatalog _catalog;
    private int _symbol_table_top = 0;
    private SymbolTable[] _symbol_table_stack = new SymbolTable[3];

    public IonReaderBinaryUserX(IonSystem system, IonCatalog catalog, UnifiedInputStreamX userBytes, int physicalStartOffset) {
        super(system, userBytes);
        this._physical_start_offset = physicalStartOffset;
        this.init_user(catalog);
    }

    public IonReaderBinaryUserX(IonSystem system, IonCatalog catalog, UnifiedInputStreamX userBytes) {
        super(system, userBytes);
        this._physical_start_offset = 0;
        this.init_user(catalog);
    }

    final void init_user(IonCatalog catalog) {
        this._symbols = this._system.getSystemSymbolTable();
        this._catalog = catalog;
    }

    public Span getCurrentPosition() {
        IonReaderBinarySpan pos = new IonReaderBinarySpan();
        if (this.getType() == null) {
            String message = "IonReader isn't positioned on a value";
            throw new IllegalStateException(message);
        }
        if (this._position_start == -1L) {
            pos._offset = this._input._pos;
            pos._limit = this._input._limit;
            pos._symbol_table = this._symbols;
        } else {
            pos._offset = this._position_start - (long)this._physical_start_offset;
            pos._limit = pos._offset + this._position_len;
            pos._symbol_table = this._symbols;
        }
        return pos;
    }

    public void seek(IonReaderBinarySpan position) {
        UnifiedSavePointManagerX.SavePoint sp;
        IonReaderBinarySpan pos = position;
        if (pos == null) {
            throw new IllegalArgumentException("Position invalid for binary reader");
        }
        if (!(this._input instanceof UnifiedInputStreamX.FromByteArray)) {
            throw new UnsupportedOperationException("Binary seek not implemented for non-byte array backed sources");
        }
        UnifiedInputStreamX.FromByteArray input = (UnifiedInputStreamX.FromByteArray)this._input;
        input._pos = (int)(pos._offset + (long)this._physical_start_offset);
        input._limit = (int)(pos._limit + (long)this._physical_start_offset);
        input._eof = false;
        while ((sp = input._save_points._active_stack) != null) {
            input._save_points.savePointPopActive(sp);
            sp.free();
        }
        this.re_init_raw();
        this.init_user(this._catalog);
        this._symbols = pos._symbol_table;
    }

    @Override
    public IonType next() {
        IonType t = null;
        if (this.hasNext()) {
            this._has_next_needed = true;
            t = this._value_type;
        }
        return t;
    }

    @Override
    boolean hasNext() {
        if (!this._eof && this._has_next_needed) {
            this.clear_system_value_stack();
            try {
                while (!this._eof && this._has_next_needed) {
                    this.has_next_helper_user();
                }
            }
            catch (IOException e) {
                this.error(e);
            }
        }
        return !this._eof;
    }

    private final void has_next_helper_user() throws IOException {
        super.hasNext();
        if (this.getDepth() == 0 && !this._value_is_null) {
            if (this._value_tid == 7) {
                if (this.load_annotations() == 0) {
                    this.load_cached_value(3);
                    int sid = this._v.getInt();
                    if (sid == 2) {
                        this._symbols = this._system.getSystemSymbolTable();
                        this.push_symbol_table(this._symbols);
                        this._has_next_needed = true;
                    }
                }
            } else if (this._value_tid == 13) {
                int count = this.load_annotations();
                for (int ii = 0; ii < count; ++ii) {
                    if (this._annotation_ids[ii] != 3) continue;
                    this._symbols = PrivateUtils.newLocalSymtab((ValueFactory)this._system, this._system.getSystemSymbolTable(), this._catalog, this, false);
                    this.push_symbol_table(this._symbols);
                    this._has_next_needed = true;
                    break;
                }
            } else assert (this._value_tid != 14);
        }
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

    @Override
    public <T> T asFacet(Class<T> facetType) {
        if (facetType == SpanProvider.class) {
            return facetType.cast(new SpanProviderFacet());
        }
        if (this._input instanceof UnifiedInputStreamX.FromByteArray && facetType == SeekableReader.class) {
            return facetType.cast(new SeekableReaderFacet());
        }
        if (facetType == PrivateByteTransferReader.class && this._input instanceof UnifiedInputStreamX.FromByteArray && this.getTypeAnnotationSymbols().length == 0 && !this.isInStruct()) {
            return facetType.cast(new ByteTransferReaderFacet());
        }
        return super.asFacet(facetType);
    }

    private class ByteTransferReaderFacet
    implements PrivateByteTransferReader {
        private ByteTransferReaderFacet() {
        }

        public void transferCurrentValue(PrivateByteTransferSink sink) throws IOException {
            if (!(IonReaderBinaryUserX.this._input instanceof UnifiedInputStreamX.FromByteArray)) {
                throw new UnsupportedOperationException();
            }
            int inOffset = (int)IonReaderBinaryUserX.this._position_start;
            int inLen = (int)IonReaderBinaryUserX.this._position_len;
            sink.writeBytes(IonReaderBinaryUserX.this._input._bytes, inOffset, inLen);
        }
    }

    private class SeekableReaderFacet
    extends SpanProviderFacet
    implements SeekableReader {
        private SeekableReaderFacet() {
        }

        public void hoist(Span span) {
            if (!(span instanceof IonReaderBinarySpan)) {
                throw new IllegalArgumentException("Span isn't compatible with this reader.");
            }
            IonReaderBinaryUserX.this.seek((IonReaderBinarySpan)span);
        }
    }

    private class SpanProviderFacet
    implements SpanProvider {
        private SpanProviderFacet() {
        }

        public Span currentSpan() {
            return IonReaderBinaryUserX.this.getCurrentPosition();
        }
    }

    private static final class IonReaderBinarySpan
    extends DowncastingFaceted
    implements Span,
    OffsetSpan {
        long _offset;
        long _limit;
        SymbolTable _symbol_table;

        private IonReaderBinarySpan() {
        }

        public long getStartOffset() {
            return this._offset;
        }

        public long getFinishOffset() {
            return this._limit;
        }
    }
}

