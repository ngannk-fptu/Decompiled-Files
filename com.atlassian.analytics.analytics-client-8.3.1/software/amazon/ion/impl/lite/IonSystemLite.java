/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonContainer;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonException;
import software.amazon.ion.IonLoader;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonTimestamp;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.UnexpectedEofException;
import software.amazon.ion.UnsupportedIonVersionException;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.PrivateIonBinaryWriterBuilder;
import software.amazon.ion.impl.PrivateIonReaderFactory;
import software.amazon.ion.impl.PrivateIonSystem;
import software.amazon.ion.impl.PrivateIonWriterFactory;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.lite.IonContainerLite;
import software.amazon.ion.impl.lite.IonDatagramLite;
import software.amazon.ion.impl.lite.IonLoaderLite;
import software.amazon.ion.impl.lite.IonSymbolLite;
import software.amazon.ion.impl.lite.IonTimestampLite;
import software.amazon.ion.impl.lite.IonValueLite;
import software.amazon.ion.impl.lite.ValueFactoryLite;
import software.amazon.ion.system.IonTextWriterBuilder;
import software.amazon.ion.util.IonTextUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class IonSystemLite
extends ValueFactoryLite
implements PrivateIonSystem {
    private final SymbolTable _system_symbol_table;
    private final IonCatalog _catalog;
    private ValueFactoryLite _value_factory;
    private final IonLoader _loader;
    private final IonTextWriterBuilder myTextWriterBuilder;
    private final PrivateIonBinaryWriterBuilder myBinaryWriterBuilder;

    public IonSystemLite(IonTextWriterBuilder twb, PrivateIonBinaryWriterBuilder bwb) {
        IonCatalog catalog = twb.getCatalog();
        assert (catalog != null);
        assert (catalog == bwb.getCatalog());
        this._catalog = catalog;
        this._loader = new IonLoaderLite(this, catalog);
        this._system_symbol_table = bwb.getInitialSymbolTable();
        assert (this._system_symbol_table.isSystemTable());
        this.myTextWriterBuilder = twb.immutable();
        this._value_factory = this;
        this._value_factory.set_system(this);
        bwb.setSymtabValueFactory(this._value_factory);
        this.myBinaryWriterBuilder = bwb.immutable();
    }

    @Override
    public boolean isStreamCopyOptimized() {
        return this.myBinaryWriterBuilder.isStreamCopyOptimized();
    }

    @Override
    public <T extends IonValue> T clone(T value) throws IonException {
        if (value.getSystem() == this) {
            return (T)value.clone();
        }
        if (value instanceof IonDatagram) {
            IonDatagram datagram = this.newDatagram();
            IonWriter writer = PrivateIonWriterFactory.makeWriter(datagram);
            IonReader reader = PrivateIonReaderFactory.makeSystemReader(value.getSystem(), value);
            try {
                writer.writeValues(reader);
            }
            catch (IOException e) {
                throw new IonException(e);
            }
            return (T)datagram;
        }
        IonReader reader = this.newReader(value);
        reader.next();
        return (T)this.newValue(reader);
    }

    @Override
    public IonCatalog getCatalog() {
        return this._catalog;
    }

    @Override
    public synchronized IonLoader getLoader() {
        return this._loader;
    }

    @Override
    public IonLoader newLoader() {
        return new IonLoaderLite(this, this._catalog);
    }

    @Override
    public IonLoader newLoader(IonCatalog catalog) {
        if (catalog == null) {
            catalog = this.getCatalog();
        }
        return new IonLoaderLite(this, catalog);
    }

    @Override
    public final SymbolTable getSystemSymbolTable() {
        return this._system_symbol_table;
    }

    @Override
    public SymbolTable getSystemSymbolTable(String ionVersionId) throws UnsupportedIonVersionException {
        if (!"$ion_1_0".equals(ionVersionId)) {
            throw new UnsupportedIonVersionException(ionVersionId);
        }
        return this.getSystemSymbolTable();
    }

    @Override
    public Iterator<IonValue> iterate(Reader ionText) {
        IonReader reader = PrivateIonReaderFactory.makeReader((IonSystem)this, this._catalog, ionText);
        ReaderIterator iterator = new ReaderIterator(this, reader);
        return iterator;
    }

    @Override
    public Iterator<IonValue> iterate(InputStream ionData) {
        IonReader reader = this.newReader(ionData);
        ReaderIterator iterator = new ReaderIterator(this, reader);
        return iterator;
    }

    @Override
    public Iterator<IonValue> iterate(String ionText) {
        IonReader reader = PrivateIonReaderFactory.makeReader((IonSystem)this, this._catalog, ionText);
        ReaderIterator iterator = new ReaderIterator(this, reader);
        return iterator;
    }

    @Override
    public Iterator<IonValue> iterate(byte[] ionData) {
        IonReader reader = PrivateIonReaderFactory.makeReader((IonSystem)this, this._catalog, ionData);
        ReaderIterator iterator = new ReaderIterator(this, reader);
        return iterator;
    }

    @Override
    public IonWriter newBinaryWriter(OutputStream out, SymbolTable ... imports) {
        return this.myBinaryWriterBuilder.withImports(imports).build(out);
    }

    @Override
    public IonWriter newTextWriter(Appendable out) {
        return this.myTextWriterBuilder.build(out);
    }

    @Override
    public IonWriter newTextWriter(Appendable out, SymbolTable ... imports) throws IOException {
        return this.myTextWriterBuilder.withImports(imports).build(out);
    }

    @Override
    public IonWriter newTextWriter(OutputStream out) {
        return this.myTextWriterBuilder.build(out);
    }

    @Override
    public IonWriter newTextWriter(OutputStream out, SymbolTable ... imports) throws IOException {
        return this.myTextWriterBuilder.withImports(imports).build(out);
    }

    @Override
    public SymbolTable newLocalSymbolTable(SymbolTable ... imports) {
        return PrivateUtils.newLocalSymtab((ValueFactory)this, this.getSystemSymbolTable(), null, imports);
    }

    @Override
    public SymbolTable newSharedSymbolTable(IonStruct ionRep) {
        return PrivateUtils.newSharedSymtab(ionRep);
    }

    @Override
    public SymbolTable newSharedSymbolTable(IonReader reader) {
        return PrivateUtils.newSharedSymtab(reader, false);
    }

    @Override
    public SymbolTable newSharedSymbolTable(IonReader reader, boolean isOnStruct) {
        return PrivateUtils.newSharedSymtab(reader, isOnStruct);
    }

    @Override
    public SymbolTable newSharedSymbolTable(String name, int version, Iterator<String> newSymbols, SymbolTable ... imports) {
        int priorVersion;
        ArrayList syms = new ArrayList();
        SymbolTable prior = null;
        if (version > 1 && ((prior = this._catalog.getTable(name, priorVersion = version - 1)) == null || prior.getVersion() != priorVersion)) {
            String message = "Catalog does not contain symbol table " + IonTextUtils.printString(name) + " version " + priorVersion + " required to create version " + version;
            throw new IonException(message);
        }
        for (SymbolTable imported : imports) {
            PrivateUtils.addAllNonNull(syms, imported.iterateDeclaredSymbolNames());
        }
        PrivateUtils.addAllNonNull(syms, newSymbols);
        SymbolTable st = PrivateUtils.newSharedSymtab(name, version, prior, syms.iterator());
        return st;
    }

    @Override
    public IonValueLite newValue(IonReader reader) {
        IonValueLite value = this.load_value_helper(reader, true);
        if (value == null) {
            throw new IonException("No value available");
        }
        return value;
    }

    private IonValueLite load_value_helper(IonReader reader, boolean isTopLevel) {
        SymbolToken[] annotations;
        IonValueLite v;
        boolean symbol_is_present = false;
        IonType t = reader.getType();
        if (t == null) {
            return null;
        }
        if (reader.isNullValue()) {
            v = this.newNull(t);
        } else {
            switch (t) {
                case BOOL: {
                    v = this.newBool(reader.booleanValue());
                    break;
                }
                case INT: {
                    v = this.newInt(reader.bigIntegerValue());
                    break;
                }
                case FLOAT: {
                    v = this.newFloat(reader.doubleValue());
                    break;
                }
                case DECIMAL: {
                    v = this.newDecimal(reader.decimalValue());
                    break;
                }
                case TIMESTAMP: {
                    v = this.newTimestamp(reader.timestampValue());
                    break;
                }
                case SYMBOL: {
                    v = this.newSymbol(reader.symbolValue());
                    symbol_is_present = true;
                    break;
                }
                case STRING: {
                    v = this.newString(reader.stringValue());
                    break;
                }
                case CLOB: {
                    v = this.newClob(reader.newBytes());
                    break;
                }
                case BLOB: {
                    v = this.newBlob(reader.newBytes());
                    break;
                }
                case LIST: {
                    v = this.newEmptyList();
                    break;
                }
                case SEXP: {
                    v = this.newEmptySexp();
                    break;
                }
                case STRUCT: {
                    v = this.newEmptyStruct();
                    break;
                }
                default: {
                    throw new IonException("unexpected type encountered reading value: " + t.toString());
                }
            }
        }
        if (!isTopLevel && reader.isInStruct()) {
            SymbolToken token = reader.getFieldNameSymbol();
            String text = token.getText();
            if (text != null && token.getSid() != -1) {
                token = PrivateUtils.newSymbolToken(text, -1);
            }
            v.setFieldNameSymbol(token);
            symbol_is_present = true;
        }
        if ((annotations = reader.getTypeAnnotationSymbols()).length != 0) {
            for (int i = 0; i < annotations.length; ++i) {
                SymbolToken token = annotations[i];
                String text = token.getText();
                if (text == null || token.getSid() == -1) continue;
                annotations[i] = PrivateUtils.newSymbolToken(text, -1);
            }
            v.setTypeAnnotationSymbols(annotations);
            symbol_is_present = true;
        }
        if (!reader.isNullValue()) {
            switch (t) {
                case BOOL: 
                case INT: 
                case FLOAT: 
                case DECIMAL: 
                case TIMESTAMP: 
                case SYMBOL: 
                case STRING: 
                case CLOB: 
                case BLOB: {
                    break;
                }
                case LIST: 
                case SEXP: 
                case STRUCT: {
                    if (!this.load_children((IonContainerLite)v, reader)) break;
                    symbol_is_present = true;
                    break;
                }
                default: {
                    throw new IonException("unexpected type encountered reading value: " + t.toString());
                }
            }
        }
        if (symbol_is_present) {
            v._isSymbolPresent(true);
        }
        return v;
    }

    private boolean load_children(IonContainerLite container, IonReader reader) {
        IonType t;
        boolean symbol_is_present = false;
        reader.stepIn();
        while ((t = reader.next()) != null) {
            IonValueLite child = this.load_value_helper(reader, false);
            container.add(child);
            if (!child._isSymbolPresent()) continue;
            symbol_is_present = true;
        }
        reader.stepOut();
        return symbol_is_present;
    }

    IonValueLite newValue(IonType valueType) {
        IonValueLite v;
        if (valueType == null) {
            throw new IllegalArgumentException("the value type must be specified");
        }
        switch (valueType) {
            case NULL: {
                v = this.newNull();
                break;
            }
            case BOOL: {
                v = this.newNullBool();
                break;
            }
            case INT: {
                v = this.newNullInt();
                break;
            }
            case FLOAT: {
                v = this.newNullFloat();
                break;
            }
            case DECIMAL: {
                v = this.newNullDecimal();
                break;
            }
            case TIMESTAMP: {
                v = this.newNullTimestamp();
                break;
            }
            case SYMBOL: {
                v = this.newNullSymbol();
                break;
            }
            case STRING: {
                v = this.newNullString();
                break;
            }
            case CLOB: {
                v = this.newNullClob();
                break;
            }
            case BLOB: {
                v = this.newNullBlob();
                break;
            }
            case LIST: {
                v = this.newEmptyList();
                break;
            }
            case SEXP: {
                v = this.newEmptySexp();
                break;
            }
            case STRUCT: {
                v = this.newEmptyStruct();
                break;
            }
            default: {
                throw new IonException("unexpected type encountered reading value: " + (Object)((Object)valueType));
            }
        }
        return v;
    }

    @Override
    public IonWriter newWriter(IonContainer container) {
        IonWriter writer = PrivateIonWriterFactory.makeWriter(container);
        return writer;
    }

    private IonValue singleValue(Iterator<IonValue> it) {
        IonValue value;
        try {
            value = it.next();
        }
        catch (NoSuchElementException e) {
            throw new UnexpectedEofException("no value found on input stream");
        }
        if (it.hasNext()) {
            throw new IonException("not a single value");
        }
        return value;
    }

    @Override
    public IonValue singleValue(String ionText) {
        Iterator<IonValue> it = this.iterate(ionText);
        return this.singleValue(it);
    }

    @Override
    public IonValue singleValue(byte[] ionData) {
        Iterator<IonValue> it = this.iterate(ionData);
        return this.singleValue(it);
    }

    protected IonSymbolLite newSystemIdSymbol(String ionVersionMarker) {
        if (!"$ion_1_0".equals(ionVersionMarker)) {
            throw new IllegalArgumentException("name isn't an ion version marker");
        }
        IonSymbolLite ivm = this.newSymbol(ionVersionMarker);
        ivm.setIsIonVersionMarker(true);
        return ivm;
    }

    @Override
    public IonTimestamp newUtcTimestampFromMillis(long millis) {
        IonTimestampLite result = this.newNullTimestamp();
        result.setMillisUtc(millis);
        return result;
    }

    @Override
    public IonTimestamp newUtcTimestamp(Date utcDate) {
        IonTimestampLite result = this.newNullTimestamp();
        if (utcDate != null) {
            result.setMillisUtc(utcDate.getTime());
        }
        return result;
    }

    @Override
    public IonTimestamp newCurrentUtcTimestamp() {
        IonTimestampLite result = super.newNullTimestamp();
        result.setCurrentTimeUtc();
        return result;
    }

    @Override
    public IonDatagram newDatagram() {
        IonCatalog catalog = this.getCatalog();
        IonDatagramLite dg = this.newDatagram(catalog);
        return dg;
    }

    public IonDatagramLite newDatagram(IonCatalog catalog) {
        if (catalog == null) {
            catalog = this.getCatalog();
        }
        IonDatagramLite dg = new IonDatagramLite(this, catalog);
        return dg;
    }

    @Override
    public IonDatagram newDatagram(IonValue initialChild) {
        IonDatagram dg = this.newDatagram(null, initialChild);
        return dg;
    }

    public IonDatagram newDatagram(IonCatalog catalog, IonValue initialChild) {
        IonDatagramLite dg = this.newDatagram(catalog);
        if (initialChild != null) {
            if (initialChild.getSystem() != this) {
                throw new IonException("this Ion system can't mix with instances from other system impl's");
            }
            if (initialChild.getContainer() != null) {
                initialChild = this.clone(initialChild);
            }
            dg.add(initialChild);
        }
        assert (dg.getSystem() == this);
        return dg;
    }

    @Override
    public IonDatagram newDatagram(SymbolTable ... imports) {
        IonDatagram dg = this.newDatagram((IonCatalog)null, imports);
        return dg;
    }

    public IonDatagram newDatagram(IonCatalog catalog, SymbolTable ... imports) {
        SymbolTable defaultSystemSymtab = this.getSystemSymbolTable();
        SymbolTable symbols = PrivateUtils.initialSymtab(this, defaultSystemSymtab, imports);
        IonDatagramLite dg = this.newDatagram(catalog);
        dg.appendTrailingSymbolTable(symbols);
        return dg;
    }

    @Override
    public IonReader newReader(byte[] ionData) {
        return PrivateIonReaderFactory.makeReader((IonSystem)this, this._catalog, ionData);
    }

    @Override
    public IonReader newSystemReader(byte[] ionData) {
        return PrivateIonReaderFactory.makeSystemReader((IonSystem)this, ionData);
    }

    @Override
    public IonReader newReader(byte[] ionData, int offset, int len) {
        return PrivateIonReaderFactory.makeReader((IonSystem)this, this._catalog, ionData, offset, len);
    }

    @Override
    public IonReader newSystemReader(byte[] ionData, int offset, int len) {
        return PrivateIonReaderFactory.makeSystemReader((IonSystem)this, ionData, offset, len);
    }

    @Override
    public IonReader newReader(String ionText) {
        return PrivateIonReaderFactory.makeReader((IonSystem)this, this._catalog, ionText);
    }

    @Override
    public IonReader newSystemReader(String ionText) {
        return PrivateIonReaderFactory.makeSystemReader((IonSystem)this, ionText);
    }

    @Override
    public IonReader newReader(InputStream ionData) {
        return PrivateIonReaderFactory.makeReader((IonSystem)this, this._catalog, ionData);
    }

    @Override
    public IonReader newSystemReader(InputStream ionData) {
        return PrivateIonReaderFactory.makeSystemReader((IonSystem)this, ionData);
    }

    @Override
    public IonReader newReader(Reader ionText) {
        return PrivateIonReaderFactory.makeReader((IonSystem)this, this._catalog, ionText);
    }

    @Override
    public IonReader newReader(IonValue value) {
        return PrivateIonReaderFactory.makeReader((IonSystem)this, this._catalog, value);
    }

    @Override
    public IonReader newSystemReader(Reader ionText) {
        return PrivateIonReaderFactory.makeSystemReader((IonSystem)this, ionText);
    }

    @Override
    public IonReader newSystemReader(IonValue value) {
        return PrivateIonReaderFactory.makeSystemReader((IonSystem)this, value);
    }

    @Override
    public IonWriter newTreeSystemWriter(IonContainer container) {
        IonWriter writer = PrivateIonWriterFactory.makeSystemWriter(container);
        return writer;
    }

    @Override
    public IonWriter newTreeWriter(IonContainer container) {
        IonWriter writer = PrivateIonWriterFactory.makeWriter(container);
        return writer;
    }

    @Override
    public Iterator<IonValue> systemIterate(Reader ionText) {
        IonReader ir = this.newSystemReader(ionText);
        return PrivateUtils.iterate(this, ir);
    }

    @Override
    public Iterator<IonValue> systemIterate(String ionText) {
        IonReader ir = this.newSystemReader(ionText);
        return PrivateUtils.iterate(this, ir);
    }

    @Override
    public Iterator<IonValue> systemIterate(InputStream ionData) {
        IonReader ir = this.newSystemReader(ionData);
        return PrivateUtils.iterate(this, ir);
    }

    @Override
    public Iterator<IonValue> systemIterate(byte[] ionData) {
        IonReader ir = this.newSystemReader(ionData);
        return PrivateUtils.iterate(this, ir);
    }

    @Override
    public boolean valueIsSharedSymbolTable(IonValue value) {
        return value instanceof IonStruct && value.hasTypeAnnotation("$ion_symbol_table");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ReaderIterator
    implements Iterator<IonValue>,
    Closeable {
        private final IonReader _reader;
        private final IonSystemLite _system;
        private IonType _next;

        protected ReaderIterator(IonSystemLite system, IonReader reader) {
            this._reader = reader;
            this._system = system;
        }

        @Override
        public boolean hasNext() {
            if (this._next == null) {
                this._next = this._reader.next();
            }
            return this._next != null;
        }

        @Override
        public IonValue next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            SymbolTable symtab = this._reader.getSymbolTable();
            IonValueLite value = this._system.newValue(this._reader);
            this._next = null;
            value.setSymbolTable(symtab);
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws IOException {
        }
    }
}

