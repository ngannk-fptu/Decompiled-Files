/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.ion.EmptySymbolException;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonException;
import software.amazon.ion.IonType;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.bin.AbstractIonWriter;
import software.amazon.ion.impl.bin.AbstractSymbolTable;
import software.amazon.ion.impl.bin.IonRawBinaryWriter;
import software.amazon.ion.impl.bin.PrivateIonManagedBinaryWriterBuilder;
import software.amazon.ion.impl.bin.Symbols;

final class IonManagedBinaryWriter
extends AbstractIonWriter {
    static final ImportedSymbolContext ONLY_SYSTEM_IMPORTS = new ImportedSymbolContext(ImportedSymbolResolverMode.FLAT, Collections.<SymbolTable>emptyList());
    private static final SymbolTable[] EMPTY_SYMBOL_TABLE_ARRAY = new SymbolTable[0];
    private final IonCatalog catalog;
    private final ImportedSymbolContext bootstrapImports;
    private ImportedSymbolContext imports;
    private final Map<String, SymbolToken> locals;
    private boolean localsLocked;
    private SymbolTable localSymbolTableView;
    private final IonRawBinaryWriter symbols;
    private final IonRawBinaryWriter user;
    private UserState userState;
    private SymbolState symbolState;
    private long userSymbolTablePosition;
    private final List<SymbolTable> userImports;
    private final List<String> userSymbols;
    private final ImportDescriptor userCurrentImport;
    private boolean forceSystemOutput;
    private boolean closed;

    IonManagedBinaryWriter(PrivateIonManagedBinaryWriterBuilder builder, OutputStream out) throws IOException {
        super(builder.optimization);
        this.symbols = new IonRawBinaryWriter(builder.provider, builder.symbolsBlockSize, out, AbstractIonWriter.WriteValueOptimization.NONE, IonRawBinaryWriter.StreamCloseMode.NO_CLOSE, IonRawBinaryWriter.StreamFlushMode.NO_FLUSH, builder.preallocationMode, builder.isFloatBinary32Enabled);
        this.user = new IonRawBinaryWriter(builder.provider, builder.userBlockSize, out, AbstractIonWriter.WriteValueOptimization.NONE, IonRawBinaryWriter.StreamCloseMode.CLOSE, IonRawBinaryWriter.StreamFlushMode.FLUSH, builder.preallocationMode, builder.isFloatBinary32Enabled);
        this.catalog = builder.catalog;
        this.bootstrapImports = builder.imports;
        this.locals = new LinkedHashMap<String, SymbolToken>();
        this.localsLocked = false;
        this.localSymbolTableView = new LocalSymbolTableView();
        this.symbolState = SymbolState.SYSTEM_SYMBOLS;
        this.forceSystemOutput = false;
        this.closed = false;
        this.userState = UserState.NORMAL;
        this.userSymbolTablePosition = 0L;
        this.userImports = new ArrayList<SymbolTable>();
        this.userSymbols = new ArrayList<String>();
        this.userCurrentImport = new ImportDescriptor();
        SymbolTable lst = builder.initialSymbolTable;
        if (lst != null) {
            ImportedSymbolContext lstImports;
            List<SymbolTable> lstImportList = Arrays.asList(lst.getImportedTables());
            this.imports = lstImports = new ImportedSymbolContext(ImportedSymbolResolverMode.DELEGATE, lstImportList);
            Iterator<String> symbolIter = lst.iterateDeclaredSymbolNames();
            while (symbolIter.hasNext()) {
                String text = symbolIter.next();
                this.intern(text);
            }
            this.startLocalSymbolTableIfNeeded(true);
        } else {
            this.imports = builder.imports;
        }
    }

    public IonCatalog getCatalog() {
        return this.catalog;
    }

    public boolean isFieldNameSet() {
        return this.user.isFieldNameSet();
    }

    public void writeIonVersionMarker() throws IOException {
        this.finish();
    }

    public int getDepth() {
        return this.user.getDepth();
    }

    private void startLocalSymbolTableIfNeeded(boolean writeIVM) throws IOException {
        if (this.symbolState == SymbolState.SYSTEM_SYMBOLS) {
            if (writeIVM) {
                this.symbols.writeIonVersionMarker();
            }
            this.symbols.addTypeAnnotationSymbol(Symbols.systemSymbol(3));
            this.symbols.stepIn(IonType.STRUCT);
            if (this.imports.parents.size() > 0) {
                this.symbols.setFieldNameSymbol(Symbols.systemSymbol(6));
                this.symbols.stepIn(IonType.LIST);
                for (SymbolTable st : this.imports.parents) {
                    this.symbols.stepIn(IonType.STRUCT);
                    this.symbols.setFieldNameSymbol(Symbols.systemSymbol(4));
                    this.symbols.writeString(st.getName());
                    this.symbols.setFieldNameSymbol(Symbols.systemSymbol(5));
                    this.symbols.writeInt(st.getVersion());
                    this.symbols.setFieldNameSymbol(Symbols.systemSymbol(8));
                    this.symbols.writeInt(st.getMaxId());
                    this.symbols.stepOut();
                }
                this.symbols.stepOut();
            }
            this.symbolState = SymbolState.LOCAL_SYMBOLS_WITH_IMPORTS_ONLY;
        }
    }

    private void startLocalSymbolTableSymbolListIfNeeded() throws IOException {
        if (this.symbolState == SymbolState.LOCAL_SYMBOLS_WITH_IMPORTS_ONLY) {
            this.symbols.setFieldNameSymbol(Symbols.systemSymbol(7));
            this.symbols.stepIn(IonType.LIST);
            this.symbolState = SymbolState.LOCAL_SYMBOLS;
        }
    }

    private SymbolToken intern(String text) {
        if (text == null) {
            return null;
        }
        if ("".equals(text)) {
            throw new EmptySymbolException();
        }
        try {
            SymbolToken token = this.imports.importedSymbols.get(text);
            if (token != null) {
                if (token.getSid() > 9) {
                    this.startLocalSymbolTableIfNeeded(true);
                }
                return token;
            }
            token = this.locals.get(text);
            if (token == null) {
                if (this.localsLocked) {
                    throw new IonException("Local symbol table was locked (made read-only)");
                }
                this.startLocalSymbolTableIfNeeded(true);
                this.startLocalSymbolTableSymbolListIfNeeded();
                token = Symbols.symbol(text, this.imports.localSidStart + this.locals.size());
                this.locals.put(text, token);
                this.symbols.writeString(text);
            }
            return token;
        }
        catch (IOException e) {
            throw new IonException("Error synthesizing symbols", e);
        }
    }

    private SymbolToken intern(SymbolToken token) {
        if (token == null) {
            return null;
        }
        String text = token.getText();
        if (text != null) {
            return this.intern(text);
        }
        return token;
    }

    public SymbolTable getSymbolTable() {
        if (this.symbolState == SymbolState.SYSTEM_SYMBOLS && this.imports.parents.isEmpty()) {
            return Symbols.systemSymbolTable();
        }
        return this.localSymbolTableView;
    }

    public void setFieldName(String name) {
        if (!this.isInStruct()) {
            throw new IllegalStateException("IonWriter.setFieldName() must be called before writing a value into a struct.");
        }
        if (name == null) {
            throw new NullPointerException("Null field name is not allowed.");
        }
        SymbolToken token = this.intern(name);
        this.user.setFieldNameSymbol(token);
    }

    public void setFieldNameSymbol(SymbolToken token) {
        token = this.intern(token);
        this.user.setFieldNameSymbol(token);
    }

    public void setTypeAnnotations(String ... annotations) {
        if (annotations == null) {
            this.user.setTypeAnnotationSymbols(null);
        } else {
            SymbolToken[] tokens = new SymbolToken[annotations.length];
            for (int i = 0; i < tokens.length; ++i) {
                tokens[i] = this.intern(annotations[i]);
            }
            this.user.setTypeAnnotationSymbols(tokens);
        }
    }

    public void setTypeAnnotationSymbols(SymbolToken ... annotations) {
        if (annotations == null) {
            this.user.setTypeAnnotationSymbols(null);
        } else {
            for (int i = 0; i < annotations.length; ++i) {
                annotations[i] = this.intern(annotations[i]);
            }
            this.user.setTypeAnnotationSymbols(annotations);
        }
    }

    public void addTypeAnnotation(String annotation) {
        SymbolToken token = this.intern(annotation);
        this.user.addTypeAnnotationSymbol(token);
    }

    public void stepIn(IonType containerType) throws IOException {
        this.userState.beforeStepIn(this, containerType);
        this.user.stepIn(containerType);
    }

    public void stepOut() throws IOException {
        this.user.stepOut();
        this.userState.afterStepOut(this);
    }

    public boolean isInStruct() {
        return this.user.isInStruct();
    }

    public void writeNull() throws IOException {
        this.user.writeNull();
    }

    public void writeNull(IonType type) throws IOException {
        this.user.writeNull(type);
    }

    public void writeBool(boolean value) throws IOException {
        this.user.writeBool(value);
    }

    public void writeInt(long value) throws IOException {
        this.userState.writeInt(this, value);
        this.user.writeInt(value);
    }

    public void writeInt(BigInteger value) throws IOException {
        this.userState.writeInt(this, value);
        this.user.writeInt(value);
    }

    public void writeFloat(double value) throws IOException {
        this.user.writeFloat(value);
    }

    public void writeDecimal(BigDecimal value) throws IOException {
        this.user.writeDecimal(value);
    }

    public void writeTimestamp(Timestamp value) throws IOException {
        this.user.writeTimestamp(value);
    }

    public void writeSymbol(String content) throws IOException {
        SymbolToken token = this.intern(content);
        this.writeSymbolToken(token);
    }

    public void writeSymbolToken(SymbolToken token) throws IOException {
        if ((token = this.intern(token)) != null && token.getSid() == 2 && this.user.getDepth() == 0 && !this.user.hasAnnotations()) {
            if (this.user.hasWrittenValuesSinceFinished()) {
                this.finish();
            } else {
                this.forceSystemOutput = true;
            }
            return;
        }
        this.user.writeSymbolToken(token);
    }

    public void writeString(String value) throws IOException {
        this.userState.writeString(this, value);
        this.user.writeString(value);
    }

    public void writeClob(byte[] data) throws IOException {
        this.user.writeClob(data);
    }

    public void writeClob(byte[] data, int offset, int length) throws IOException {
        this.user.writeClob(data, offset, length);
    }

    public void writeBlob(byte[] data) throws IOException {
        this.user.writeBlob(data);
    }

    public void writeBlob(byte[] data, int offset, int length) throws IOException {
        this.user.writeBlob(data, offset, length);
    }

    public void writeBytes(byte[] data, int off, int len) throws IOException {
        this.startLocalSymbolTableIfNeeded(true);
        this.user.writeBytes(data, off, len);
    }

    public void flush() throws IOException {
        if (this.getDepth() == 0 && this.localsLocked) {
            this.unsafeFlush();
        }
    }

    private void unsafeFlush() throws IOException {
        if (this.user.hasWrittenValuesSinceFinished() || this.forceSystemOutput) {
            this.symbolState.closeTable(this.symbols);
        }
        this.symbolState = SymbolState.LOCAL_SYMBOLS_FLUSHED;
        this.forceSystemOutput = false;
        this.symbols.finish();
        this.user.finish();
    }

    public void finish() throws IOException {
        if (this.getDepth() != 0) {
            throw new IllegalStateException("IonWriter.finish() can only be called at top-level.");
        }
        this.unsafeFlush();
        this.locals.clear();
        this.localsLocked = false;
        this.symbolState = SymbolState.SYSTEM_SYMBOLS;
        this.imports = this.bootstrapImports;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            this.finish();
        }
        catch (IllegalStateException illegalStateException) {
            try {
                this.symbols.close();
            }
            finally {
                this.user.close();
            }
        }
        finally {
            try {
                this.symbols.close();
            }
            finally {
                this.user.close();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class LocalSymbolTableView
    extends AbstractSymbolTable {
        public LocalSymbolTableView() {
            super(null, 0);
        }

        @Override
        public Iterator<String> iterateDeclaredSymbolNames() {
            return IonManagedBinaryWriter.this.locals.keySet().iterator();
        }

        @Override
        public int getMaxId() {
            return this.getImportedMaxId() + IonManagedBinaryWriter.this.locals.size();
        }

        @Override
        public SymbolTable[] getImportedTables() {
            return ((IonManagedBinaryWriter)IonManagedBinaryWriter.this).imports.parents.toArray(EMPTY_SYMBOL_TABLE_ARRAY);
        }

        @Override
        public int getImportedMaxId() {
            return ((IonManagedBinaryWriter)IonManagedBinaryWriter.this).imports.localSidStart - 1;
        }

        @Override
        public boolean isSystemTable() {
            return false;
        }

        @Override
        public boolean isSubstitute() {
            return false;
        }

        @Override
        public boolean isSharedTable() {
            return false;
        }

        @Override
        public boolean isLocalTable() {
            return true;
        }

        @Override
        public boolean isReadOnly() {
            return IonManagedBinaryWriter.this.localsLocked;
        }

        @Override
        public SymbolTable getSystemSymbolTable() {
            return Symbols.systemSymbolTable();
        }

        @Override
        public SymbolToken intern(String text) {
            SymbolToken token = this.find(text);
            if (token == null) {
                if (IonManagedBinaryWriter.this.localsLocked) {
                    throw new IonException("Cannot intern into locked (read-only) local symbol table");
                }
                token = IonManagedBinaryWriter.this.intern(text);
            }
            return token;
        }

        @Override
        public String findKnownSymbol(int id) {
            for (SymbolTable table : ((IonManagedBinaryWriter)IonManagedBinaryWriter.this).imports.parents) {
                String text = table.findKnownSymbol(id);
                if (text == null) continue;
                return text;
            }
            for (SymbolToken token : IonManagedBinaryWriter.this.locals.values()) {
                if (token.getSid() != id) continue;
                return token.getText();
            }
            return null;
        }

        @Override
        public SymbolToken find(String text) {
            SymbolToken token = ((IonManagedBinaryWriter)IonManagedBinaryWriter.this).imports.importedSymbols.get(text);
            if (token != null) {
                return token;
            }
            return (SymbolToken)IonManagedBinaryWriter.this.locals.get(text);
        }

        @Override
        public void makeReadOnly() {
            IonManagedBinaryWriter.this.localsLocked = true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum UserState {
        NORMAL{

            public void beforeStepIn(IonManagedBinaryWriter self, IonType type) {
                if (self.user.hasTopLevelSymbolTableAnnotation() && type == IonType.STRUCT) {
                    self.userState = 1.LOCALS_AT_TOP;
                    self.userSymbolTablePosition = self.user.position();
                }
            }

            public void afterStepOut(IonManagedBinaryWriter self) {
            }

            public void writeInt(IonManagedBinaryWriter self, BigInteger value) {
            }
        }
        ,
        LOCALS_AT_TOP{

            public void beforeStepIn(IonManagedBinaryWriter self, IonType type) {
                if (self.user.getDepth() == 1) {
                    switch (self.user.getFieldId()) {
                        case 6: {
                            if (type != IonType.LIST) {
                                throw new IllegalArgumentException("Cannot step into Local Symbol Table 'symbols' field as non-list: " + (Object)((Object)type));
                            }
                            self.userState = 2.LOCALS_AT_IMPORTS;
                            break;
                        }
                        case 7: {
                            if (type != IonType.LIST) {
                                throw new IllegalArgumentException("Cannot step into Local Symbol Table 'symbols' field as non-list: " + (Object)((Object)type));
                            }
                            self.userState = 2.LOCALS_AT_SYMBOLS;
                        }
                    }
                }
            }

            public void afterStepOut(IonManagedBinaryWriter self) throws IOException {
                if (self.user.getDepth() == 0) {
                    self.user.truncate(self.userSymbolTablePosition);
                    self.finish();
                    self.imports = new ImportedSymbolContext(ImportedSymbolResolverMode.DELEGATE, self.userImports);
                    self.startLocalSymbolTableIfNeeded(false);
                    for (String text : self.userSymbols) {
                        self.intern(text);
                    }
                    self.userSymbolTablePosition = 0L;
                    self.userCurrentImport.reset();
                    self.userImports.clear();
                    self.userSymbols.clear();
                    self.userState = 2.NORMAL;
                }
            }
        }
        ,
        LOCALS_AT_IMPORTS{

            public void beforeStepIn(IonManagedBinaryWriter self, IonType type) {
                if (type != IonType.STRUCT) {
                    throw new IllegalArgumentException("Cannot step into non-struct in Local Symbol Table import list: " + (Object)((Object)type));
                }
            }

            public void afterStepOut(IonManagedBinaryWriter self) {
                switch (self.user.getDepth()) {
                    case 2: {
                        boolean declaredVersionMatches;
                        ImportDescriptor desc = self.userCurrentImport;
                        if (desc.isMalformed()) {
                            throw new IllegalArgumentException("Invalid import: " + desc);
                        }
                        if (!desc.isDefined()) break;
                        SymbolTable symbols = self.catalog.getTable(desc.name, desc.version);
                        if (symbols == null) {
                            if (desc.maxId == -1) {
                                throw new IllegalArgumentException("Import is not in catalog and no max ID provided: " + desc);
                            }
                            symbols = Symbols.unknownSharedSymbolTable(desc.name, desc.version, desc.maxId);
                        }
                        boolean hasDeclaredMaxId = desc.maxId != -1;
                        boolean declaredMaxIdMatches = desc.maxId == symbols.getMaxId();
                        boolean bl = declaredVersionMatches = desc.version == symbols.getVersion();
                        if (!(!hasDeclaredMaxId || declaredMaxIdMatches && declaredVersionMatches)) {
                            symbols = PrivateUtils.newSubstituteSymtab(symbols, desc.version, desc.maxId);
                        }
                        self.userImports.add(symbols);
                        break;
                    }
                    case 1: {
                        self.userState = 3.LOCALS_AT_TOP;
                    }
                }
            }

            public void writeString(IonManagedBinaryWriter self, String value) {
                if (self.user.getDepth() == 3 && self.user.getFieldId() == 4) {
                    if (value == null) {
                        throw new NullPointerException("Cannot have null import name");
                    }
                    ((IonManagedBinaryWriter)self).userCurrentImport.name = value;
                }
            }

            public void writeInt(IonManagedBinaryWriter self, long value) {
                if (self.user.getDepth() == 3) {
                    if (value > Integer.MAX_VALUE || value < 1L) {
                        throw new IllegalArgumentException("Invalid integer value in import: " + value);
                    }
                    switch (self.user.getFieldId()) {
                        case 5: {
                            ((IonManagedBinaryWriter)self).userCurrentImport.version = (int)value;
                            break;
                        }
                        case 8: {
                            ((IonManagedBinaryWriter)self).userCurrentImport.maxId = (int)value;
                        }
                    }
                }
            }
        }
        ,
        LOCALS_AT_SYMBOLS{

            public void beforeStepIn(IonManagedBinaryWriter self, IonType type) {
            }

            public void afterStepOut(IonManagedBinaryWriter self) {
                if (self.user.getDepth() == 1) {
                    self.userState = 4.LOCALS_AT_TOP;
                }
            }

            public void writeString(IonManagedBinaryWriter self, String value) {
                if (self.user.getDepth() == 2) {
                    self.userSymbols.add(value);
                }
            }
        };


        public abstract void beforeStepIn(IonManagedBinaryWriter var1, IonType var2) throws IOException;

        public abstract void afterStepOut(IonManagedBinaryWriter var1) throws IOException;

        public void writeString(IonManagedBinaryWriter self, String value) throws IOException {
        }

        public void writeInt(IonManagedBinaryWriter self, long value) throws IOException {
        }

        public void writeInt(IonManagedBinaryWriter self, BigInteger value) throws IOException {
            this.writeInt(self, value.longValue());
        }
    }

    private static class ImportDescriptor {
        public String name;
        public int version;
        public int maxId;

        public ImportDescriptor() {
            this.reset();
        }

        public void reset() {
            this.name = null;
            this.version = -1;
            this.maxId = -1;
        }

        public boolean isDefined() {
            return this.name != null && this.version >= 1;
        }

        public boolean isUndefined() {
            return this.name == null && this.version == -1 && this.maxId == -1;
        }

        public boolean isMalformed() {
            return !this.isDefined() && !this.isUndefined();
        }

        public String toString() {
            return "{name: \"" + this.name + "\", version: " + this.version + ", max_id: " + this.maxId + "}";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum SymbolState {
        SYSTEM_SYMBOLS{

            public void closeTable(IonRawBinaryWriter writer) throws IOException {
                writer.writeIonVersionMarker();
            }
        }
        ,
        LOCAL_SYMBOLS_WITH_IMPORTS_ONLY{

            public void closeTable(IonRawBinaryWriter writer) throws IOException {
                writer.stepOut();
            }
        }
        ,
        LOCAL_SYMBOLS{

            public void closeTable(IonRawBinaryWriter writer) throws IOException {
                writer.stepOut();
                writer.stepOut();
            }
        }
        ,
        LOCAL_SYMBOLS_FLUSHED{

            public void closeTable(IonRawBinaryWriter writer) throws IOException {
            }
        };


        public abstract void closeTable(IonRawBinaryWriter var1) throws IOException;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class ImportedSymbolContext {
        public final List<SymbolTable> parents;
        public final SymbolResolver importedSymbols;
        public final int localSidStart;

        ImportedSymbolContext(ImportedSymbolResolverMode mode, List<SymbolTable> imports) {
            ArrayList<SymbolTable> mutableParents = new ArrayList<SymbolTable>(imports.size());
            SymbolResolverBuilder builder = mode.createBuilder();
            int maxSid = 10;
            for (SymbolTable st : imports) {
                if (!st.isSharedTable()) {
                    throw new IonException("Imported symbol table is not shared: " + st);
                }
                if (st.isSystemTable()) continue;
                mutableParents.add(st);
                maxSid = builder.addSymbolTable(st, maxSid);
            }
            this.parents = Collections.unmodifiableList(mutableParents);
            this.importedSymbols = builder.build();
            this.localSidStart = maxSid;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum ImportedSymbolResolverMode {
        FLAT{

            SymbolResolverBuilder createBuilder() {
                final HashMap<String, SymbolToken> symbols = new HashMap<String, SymbolToken>();
                for (SymbolToken token : Symbols.systemSymbols()) {
                    symbols.put(token.getText(), token);
                }
                return new SymbolResolverBuilder(){

                    public int addSymbolTable(SymbolTable table, int startSid) {
                        int maxSid = startSid;
                        Iterator<String> iter = table.iterateDeclaredSymbolNames();
                        while (iter.hasNext()) {
                            String text = iter.next();
                            if (text != null && !symbols.containsKey(text)) {
                                symbols.put(text, Symbols.symbol(text, maxSid));
                            }
                            ++maxSid;
                        }
                        return maxSid;
                    }

                    public SymbolResolver build() {
                        return new SymbolResolver(){

                            public SymbolToken get(String text) {
                                return (SymbolToken)symbols.get(text);
                            }
                        };
                    }
                };
            }
        }
        ,
        DELEGATE{

            SymbolResolverBuilder createBuilder() {
                final ArrayList<ImportTablePosition> imports = new ArrayList<ImportTablePosition>();
                imports.add(new ImportTablePosition(Symbols.systemSymbolTable(), 1));
                return new SymbolResolverBuilder(){

                    public int addSymbolTable(SymbolTable table, int startId) {
                        imports.add(new ImportTablePosition(table, startId));
                        return startId + table.getMaxId();
                    }

                    public SymbolResolver build() {
                        return new SymbolResolver(){

                            public SymbolToken get(String text) {
                                for (ImportTablePosition tableImport : imports) {
                                    SymbolToken token = tableImport.table.find(text);
                                    if (token == null) continue;
                                    return Symbols.symbol(text, token.getSid() + tableImport.startId - 1);
                                }
                                return null;
                            }
                        };
                    }
                };
            }
        };


        abstract SymbolResolverBuilder createBuilder();
    }

    private static final class ImportTablePosition {
        public final SymbolTable table;
        public final int startId;

        public ImportTablePosition(SymbolTable table, int startId) {
            this.table = table;
            this.startId = startId;
        }
    }

    private static interface SymbolResolverBuilder {
        public int addSymbolTable(SymbolTable var1, int var2);

        public SymbolResolver build();
    }

    private static interface SymbolResolver {
        public SymbolToken get(String var1);
    }
}

