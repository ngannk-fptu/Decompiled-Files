/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import java.math.BigDecimal;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.impl.PrivateReaderWriter;

@Deprecated
public abstract class PrivateIonWriterBase
implements IonWriter,
PrivateReaderWriter {
    protected static final String ERROR_MISSING_FIELD_NAME = "IonWriter.setFieldName() must be called before writing a value into a struct.";
    static final String ERROR_FINISH_NOT_AT_TOP_LEVEL = "IonWriter.finish() can only be called at top-level.";
    private static final boolean _debug_on = false;
    private int _symbol_table_top = 0;
    private SymbolTable[] _symbol_table_stack = new SymbolTable[3];

    protected abstract int getDepth();

    abstract void writeIonVersionMarker() throws IOException;

    public abstract void setSymbolTable(SymbolTable var1) throws IOException;

    abstract String assumeKnownSymbol(int var1);

    public abstract boolean isFieldNameSet();

    abstract boolean has_annotation(String var1, int var2);

    abstract String[] getTypeAnnotations();

    abstract int[] getTypeAnnotationIds();

    abstract void writeSymbol(int var1) throws IOException;

    public void writeBlob(byte[] value) throws IOException {
        if (value == null) {
            this.writeNull(IonType.BLOB);
        } else {
            this.writeBlob(value, 0, value.length);
        }
    }

    public void writeClob(byte[] value) throws IOException {
        if (value == null) {
            this.writeNull(IonType.CLOB);
        } else {
            this.writeClob(value, 0, value.length);
        }
    }

    public abstract void writeDecimal(BigDecimal var1) throws IOException;

    public void writeFloat(float value) throws IOException {
        this.writeFloat((double)value);
    }

    public void writeNull() throws IOException {
        this.writeNull(IonType.NULL);
    }

    public final void writeSymbolToken(SymbolToken tok) throws IOException {
        if (tok == null) {
            this.writeNull(IonType.SYMBOL);
            return;
        }
        String text = tok.getText();
        if (text != null) {
            this.writeSymbol(text);
        } else {
            int sid = tok.getSid();
            this.writeSymbol(sid);
        }
    }

    public void writeValues(IonReader reader) throws IOException {
        if (reader.getDepth() == 0) {
            this.clear_system_value_stack();
        }
        if (reader.getType() == null) {
            reader.next();
        }
        if (this.getDepth() == 0 && reader instanceof PrivateReaderWriter) {
            PrivateReaderWriter private_reader = (PrivateReaderWriter)((Object)reader);
            while (reader.getType() != null) {
                this.transfer_symbol_tables(private_reader);
                this.writeValue(reader);
                reader.next();
            }
        } else {
            while (reader.getType() != null) {
                this.writeValue(reader);
                reader.next();
            }
        }
    }

    private final void transfer_symbol_tables(PrivateReaderWriter reader) throws IOException {
        SymbolTable reader_symbols = reader.pop_passed_symbol_table();
        if (reader_symbols != null) {
            this.clear_system_value_stack();
            this.setSymbolTable(reader_symbols);
            while (reader_symbols != null) {
                this.push_symbol_table(reader_symbols);
                reader_symbols = reader.pop_passed_symbol_table();
            }
        }
    }

    private final void write_value_field_name_helper(IonReader reader) {
        if (this.isInStruct() && !this.isFieldNameSet()) {
            SymbolToken tok = reader.getFieldNameSymbol();
            if (tok == null) {
                throw new IllegalStateException("Field name not set");
            }
            this.setFieldNameSymbol(tok);
        }
    }

    private final void write_value_annotations_helper(IonReader reader) {
        SymbolToken[] a = reader.getTypeAnnotationSymbols();
        this.setTypeAnnotationSymbols(a);
    }

    public boolean isStreamCopyOptimized() {
        return false;
    }

    public void writeValue(IonReader reader) throws IOException {
        IonType type = reader.getType();
        this.writeValueRecursively(type, reader);
    }

    final void writeValueRecursively(IonType type, IonReader reader) throws IOException {
        this.write_value_field_name_helper(reader);
        this.write_value_annotations_helper(reader);
        if (reader.isNullValue()) {
            this.writeNull(type);
        } else {
            switch (type) {
                case NULL: {
                    this.writeNull();
                    break;
                }
                case BOOL: {
                    this.writeBool(reader.booleanValue());
                    break;
                }
                case INT: {
                    this.writeInt(reader.bigIntegerValue());
                    break;
                }
                case FLOAT: {
                    this.writeFloat(reader.doubleValue());
                    break;
                }
                case DECIMAL: {
                    this.writeDecimal(reader.decimalValue());
                    break;
                }
                case TIMESTAMP: {
                    this.writeTimestamp(reader.timestampValue());
                    break;
                }
                case STRING: {
                    this.writeString(reader.stringValue());
                    break;
                }
                case SYMBOL: {
                    this.writeSymbolToken(reader.symbolValue());
                    break;
                }
                case BLOB: {
                    this.writeBlob(reader.newBytes());
                    break;
                }
                case CLOB: {
                    this.writeClob(reader.newBytes());
                    break;
                }
                case STRUCT: {
                    this.writeContainerRecursively(IonType.STRUCT, reader);
                    break;
                }
                case LIST: {
                    this.writeContainerRecursively(IonType.LIST, reader);
                    break;
                }
                case SEXP: {
                    this.writeContainerRecursively(IonType.SEXP, reader);
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }

    private void writeContainerRecursively(IonType type, IonReader reader) throws IOException {
        this.stepIn(type);
        reader.stepIn();
        while ((type = reader.next()) != null) {
            this.writeValueRecursively(type, reader);
        }
        reader.stepOut();
        this.stepOut();
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

    public final SymbolTable pop_passed_symbol_table() {
        if (this._symbol_table_top <= 0) {
            return null;
        }
        --this._symbol_table_top;
        SymbolTable symbols = this._symbol_table_stack[this._symbol_table_top];
        this._symbol_table_stack[this._symbol_table_top] = null;
        return symbols;
    }
}

