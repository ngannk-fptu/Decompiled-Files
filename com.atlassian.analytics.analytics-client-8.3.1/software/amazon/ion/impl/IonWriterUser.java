/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonException;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonType;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.IonWriterSystem;
import software.amazon.ion.impl.IonWriterSystemTree;
import software.amazon.ion.impl.PrivateIonWriter;
import software.amazon.ion.impl.PrivateIonWriterBase;
import software.amazon.ion.impl.PrivateUtils;

class IonWriterUser
extends PrivateIonWriterBase
implements PrivateIonWriter {
    private final ValueFactory _symtab_value_factory;
    private final IonCatalog _catalog;
    final IonWriterSystem _system_writer;
    IonWriterSystem _current_writer;
    private IonStruct _symbol_table_value;

    IonWriterUser(IonCatalog catalog, ValueFactory symtabValueFactory, IonWriterSystem systemWriter) {
        this._symtab_value_factory = symtabValueFactory;
        this._catalog = catalog;
        assert (systemWriter != null);
        this._system_writer = systemWriter;
        this._current_writer = systemWriter;
    }

    IonWriterUser(IonCatalog catalog, ValueFactory symtabValueFactory, IonWriterSystem systemWriter, SymbolTable symtab) {
        this(catalog, symtabValueFactory, systemWriter);
        SymbolTable defaultSystemSymtab = systemWriter.getDefaultSystemSymtab();
        if (symtab.isLocalTable() || symtab != defaultSystemSymtab) {
            try {
                this.setSymbolTable(symtab);
            }
            catch (IOException e) {
                throw new IonException(e);
            }
        }
        assert (this._system_writer == this._current_writer && this._system_writer == systemWriter);
    }

    public IonCatalog getCatalog() {
        return this._catalog;
    }

    boolean has_annotation(String name, int id) {
        return this._current_writer.has_annotation(name, id);
    }

    public int getDepth() {
        return this._current_writer.getDepth();
    }

    public boolean isInStruct() {
        return this._current_writer.isInStruct();
    }

    public void flush() throws IOException {
        this._current_writer.flush();
    }

    public void close() throws IOException {
        try {
            try {
                if (this.getDepth() == 0) {
                    assert (this._current_writer == this._system_writer);
                    this.finish();
                }
            }
            finally {
                this._current_writer.close();
            }
        }
        finally {
            this._system_writer.close();
        }
    }

    public final void finish() throws IOException {
        if (this.symbol_table_being_collected()) {
            throw new IllegalStateException("IonWriter.finish() can only be called at top-level.");
        }
        this._system_writer.finish();
    }

    SymbolTable activeSystemSymbolTable() {
        return this.getSymbolTable().getSystemSymbolTable();
    }

    private boolean symbol_table_being_collected() {
        return this._current_writer != this._system_writer;
    }

    private void open_local_symbol_table_copy() {
        assert (!this.symbol_table_being_collected());
        this._symbol_table_value = this._symtab_value_factory.newEmptyStruct();
        SymbolToken[] anns = this._system_writer.getTypeAnnotationSymbols();
        this._system_writer.clearAnnotations();
        this._symbol_table_value.setTypeAnnotationSymbols(anns);
        this._current_writer = new IonWriterSystemTree(this.activeSystemSymbolTable(), this._catalog, this._symbol_table_value, null);
    }

    private void close_local_symbol_table_copy() throws IOException {
        assert (this.symbol_table_being_collected());
        SymbolTable symtab = PrivateUtils.newLocalSymtab(this.activeSystemSymbolTable(), this._catalog, this._symbol_table_value);
        this._symbol_table_value = null;
        this._current_writer = this._system_writer;
        this.setSymbolTable(symtab);
    }

    public final void setSymbolTable(SymbolTable symbols) throws IOException {
        if (symbols == null || PrivateUtils.symtabIsSharedNotSystem(symbols)) {
            String message = "symbol table must be local or system to be set, or reset";
            throw new IllegalArgumentException(message);
        }
        if (this.getDepth() > 0) {
            String message = "the symbol table cannot be set, or reset, while a container is open";
            throw new IllegalStateException(message);
        }
        if (symbols.isSystemTable()) {
            this.writeIonVersionMarker(symbols);
        } else {
            this._system_writer.writeLocalSymtab(symbols);
        }
    }

    public final SymbolTable getSymbolTable() {
        SymbolTable symbols = this._system_writer.getSymbolTable();
        return symbols;
    }

    final String assumeKnownSymbol(int sid) {
        return this._system_writer.assumeKnownSymbol(sid);
    }

    public final void setFieldName(String name) {
        this._current_writer.setFieldName(name);
    }

    public final void setFieldNameSymbol(SymbolToken name) {
        this._current_writer.setFieldNameSymbol(name);
    }

    public final boolean isFieldNameSet() {
        return this._current_writer.isFieldNameSet();
    }

    public void addTypeAnnotation(String annotation) {
        this._current_writer.addTypeAnnotation(annotation);
    }

    public void setTypeAnnotations(String ... annotations) {
        this._current_writer.setTypeAnnotations(annotations);
    }

    public void setTypeAnnotationSymbols(SymbolToken ... annotations) {
        this._current_writer.setTypeAnnotationSymbols(annotations);
    }

    String[] getTypeAnnotations() {
        return this._current_writer.getTypeAnnotations();
    }

    int[] getTypeAnnotationIds() {
        return this._current_writer.getTypeAnnotationIds();
    }

    final SymbolToken[] getTypeAnnotationSymbols() {
        return this._current_writer.getTypeAnnotationSymbols();
    }

    public void stepIn(IonType containerType) throws IOException {
        if (containerType == IonType.STRUCT && this._current_writer.getDepth() == 0 && this.has_annotation("$ion_symbol_table", 3)) {
            this.open_local_symbol_table_copy();
        } else {
            this._current_writer.stepIn(containerType);
        }
    }

    public void stepOut() throws IOException {
        if (this.symbol_table_being_collected() && this._current_writer.getDepth() == 1) {
            this.close_local_symbol_table_copy();
        } else {
            this._current_writer.stepOut();
        }
    }

    public void writeBlob(byte[] value, int start, int len) throws IOException {
        this._current_writer.writeBlob(value, start, len);
    }

    public void writeBool(boolean value) throws IOException {
        this._current_writer.writeBool(value);
    }

    public void writeClob(byte[] value, int start, int len) throws IOException {
        this._current_writer.writeClob(value, start, len);
    }

    public void writeDecimal(BigDecimal value) throws IOException {
        this._current_writer.writeDecimal(value);
    }

    public void writeFloat(double value) throws IOException {
        this._current_writer.writeFloat(value);
    }

    public void writeInt(int value) throws IOException {
        this._current_writer.writeInt(value);
    }

    public void writeInt(long value) throws IOException {
        this._current_writer.writeInt(value);
    }

    public void writeInt(BigInteger value) throws IOException {
        this._current_writer.writeInt(value);
    }

    public void writeNull(IonType type) throws IOException {
        this._current_writer.writeNull(type);
    }

    public void writeString(String value) throws IOException {
        this._current_writer.writeString(value);
    }

    final void writeSymbol(int symbolId) throws IOException {
        this._current_writer.writeSymbol(symbolId);
    }

    public final void writeSymbol(String value) throws IOException {
        this._current_writer.writeSymbol(value);
    }

    final void writeIonVersionMarker(SymbolTable systemSymtab) throws IOException {
        this._current_writer.writeIonVersionMarker(systemSymtab);
    }

    public final void writeIonVersionMarker() throws IOException {
        this._current_writer.writeIonVersionMarker();
    }

    public void writeTimestamp(Timestamp value) throws IOException {
        this._current_writer.writeTimestamp(value);
    }
}

