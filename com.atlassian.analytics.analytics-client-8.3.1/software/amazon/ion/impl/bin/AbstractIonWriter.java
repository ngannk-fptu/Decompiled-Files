/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import java.io.IOException;
import java.math.BigInteger;
import software.amazon.ion.Decimal;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.impl.PrivateByteTransferReader;
import software.amazon.ion.impl.PrivateByteTransferSink;
import software.amazon.ion.impl.PrivateIonWriter;
import software.amazon.ion.impl.PrivateSymtabExtendsCache;
import software.amazon.ion.impl.PrivateUtils;

abstract class AbstractIonWriter
implements PrivateIonWriter,
PrivateByteTransferSink {
    private final PrivateSymtabExtendsCache symtabExtendsCache;

    AbstractIonWriter(WriteValueOptimization optimization) {
        this.symtabExtendsCache = optimization == WriteValueOptimization.COPY_OPTIMIZED ? new PrivateSymtabExtendsCache() : null;
    }

    public final void writeValue(IonValue value) throws IOException {
        if (value != null) {
            if (value instanceof IonDatagram) {
                this.finish();
            }
            value.writeTo(this);
        }
    }

    public final void writeValue(IonReader reader) throws IOException {
        PrivateByteTransferReader transferReader;
        IonType type = reader.getType();
        if (this.isStreamCopyOptimized() && (transferReader = reader.asFacet(PrivateByteTransferReader.class)) != null && (PrivateUtils.isNonSymbolScalar(type) || this.symtabExtendsCache.symtabsCompat(this.getSymbolTable(), reader.getSymbolTable()))) {
            transferReader.transferCurrentValue(this);
            return;
        }
        this.writeValueRecursive(reader);
    }

    public final void writeValueRecursive(IonReader reader) throws IOException {
        SymbolToken[] annotations;
        IonType type = reader.getType();
        SymbolToken fieldName = reader.getFieldNameSymbol();
        if (fieldName != null && !this.isFieldNameSet() && this.isInStruct()) {
            this.setFieldNameSymbol(fieldName);
        }
        if ((annotations = reader.getTypeAnnotationSymbols()).length > 0) {
            this.setTypeAnnotationSymbols(annotations);
        }
        if (reader.isNullValue()) {
            this.writeNull(type);
            return;
        }
        block0 : switch (type) {
            case BOOL: {
                boolean booleanValue = reader.booleanValue();
                this.writeBool(booleanValue);
                break;
            }
            case INT: {
                switch (reader.getIntegerSize()) {
                    case INT: {
                        int intValue = reader.intValue();
                        this.writeInt(intValue);
                        break block0;
                    }
                    case LONG: {
                        long longValue = reader.longValue();
                        this.writeInt(longValue);
                        break block0;
                    }
                    case BIG_INTEGER: {
                        BigInteger bigIntegerValue = reader.bigIntegerValue();
                        this.writeInt(bigIntegerValue);
                        break block0;
                    }
                }
                throw new IllegalStateException();
            }
            case FLOAT: {
                double doubleValue = reader.doubleValue();
                this.writeFloat(doubleValue);
                break;
            }
            case DECIMAL: {
                Decimal decimalValue = reader.decimalValue();
                this.writeDecimal(decimalValue);
                break;
            }
            case TIMESTAMP: {
                Timestamp timestampValue = reader.timestampValue();
                this.writeTimestamp(timestampValue);
                break;
            }
            case SYMBOL: {
                SymbolToken symbolValue = reader.symbolValue();
                this.writeSymbolToken(symbolValue);
                break;
            }
            case STRING: {
                String stringValue = reader.stringValue();
                this.writeString(stringValue);
                break;
            }
            case CLOB: {
                byte[] clobValue = reader.newBytes();
                this.writeClob(clobValue);
                break;
            }
            case BLOB: {
                byte[] blobValue = reader.newBytes();
                this.writeBlob(blobValue);
                break;
            }
            case LIST: 
            case SEXP: 
            case STRUCT: {
                reader.stepIn();
                this.stepIn(type);
                while (reader.next() != null) {
                    this.writeValue(reader);
                }
                this.stepOut();
                reader.stepOut();
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected type: " + (Object)((Object)type));
            }
        }
    }

    public final void writeValues(IonReader reader) throws IOException {
        if (reader.getType() != null) {
            this.writeValue(reader);
        }
        while (reader.next() != null) {
            this.writeValue(reader);
        }
    }

    public final boolean isStreamCopyOptimized() {
        return this.symtabExtendsCache != null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum WriteValueOptimization {
        NONE,
        COPY_OPTIMIZED;

    }
}

