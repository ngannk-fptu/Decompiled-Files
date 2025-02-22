/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import software.amazon.ion.EmptySymbolException;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonException;
import software.amazon.ion.IonList;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonString;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonWriter;
import software.amazon.ion.ReadOnlyValueException;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.IonReaderTreeSystem;
import software.amazon.ion.impl.LocalSymbolTableImports;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.SubstituteSymbolTable;
import software.amazon.ion.impl.SymbolTableReader;
import software.amazon.ion.impl.SymbolTokenImpl;
import software.amazon.ion.util.IonTextUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class LocalSymbolTable
implements SymbolTable {
    private static final int DEFAULT_CAPACITY = 16;
    private final LocalSymbolTableImports myImportsList;
    private final ValueFactory myImageFactory;
    private final Map<String, Integer> mySymbolsMap;
    private boolean isReadOnly;
    private IonStruct myImage;
    private String[] mySymbolNames;
    private int mySymbolsCount;
    private final int myFirstLocalSid;

    private void buildSymbolsMap() {
        int sid = this.myFirstLocalSid;
        int i = 0;
        while (i < this.mySymbolNames.length) {
            String symbolText = this.mySymbolNames[i];
            if (symbolText != null) {
                LocalSymbolTable.putToMapIfNotThere(this.mySymbolsMap, symbolText, sid);
            }
            ++i;
            ++sid;
        }
    }

    private LocalSymbolTable(ValueFactory imageFactory, LocalSymbolTableImports imports, List<String> symbolsList) {
        if (symbolsList == null || symbolsList.isEmpty()) {
            this.mySymbolsCount = 0;
            this.mySymbolNames = PrivateUtils.EMPTY_STRING_ARRAY;
        } else {
            this.mySymbolsCount = symbolsList.size();
            this.mySymbolNames = symbolsList.toArray(new String[this.mySymbolsCount]);
        }
        this.myImageFactory = imageFactory;
        this.myImportsList = imports;
        this.myFirstLocalSid = this.myImportsList.getMaxId() + 1;
        this.mySymbolsMap = new HashMap<String, Integer>();
        this.buildSymbolsMap();
    }

    private LocalSymbolTable(LocalSymbolTable other, int maxId) {
        this.isReadOnly = false;
        this.myFirstLocalSid = other.myFirstLocalSid;
        this.myImage = null;
        this.myImageFactory = other.myImageFactory;
        this.myImportsList = other.myImportsList;
        this.mySymbolsCount = maxId - this.myImportsList.getMaxId();
        this.mySymbolNames = PrivateUtils.copyOf(other.mySymbolNames, this.mySymbolsCount);
        if (maxId == other.getMaxId()) {
            this.mySymbolsMap = new HashMap<String, Integer>(other.mySymbolsMap);
        } else {
            this.mySymbolsMap = new HashMap<String, Integer>(this.mySymbolsCount);
            this.buildSymbolsMap();
        }
    }

    static LocalSymbolTable makeNewLocalSymbolTable(ValueFactory imageFactory, SymbolTable defaultSystemSymtab, List<String> localSymbols, SymbolTable ... imports) {
        LocalSymbolTableImports unifiedSymtabImports = new LocalSymbolTableImports(defaultSystemSymtab, imports);
        return new LocalSymbolTable(imageFactory, unifiedSymtabImports, localSymbols);
    }

    static LocalSymbolTable makeNewLocalSymbolTable(SymbolTable systemSymbolTable, IonCatalog catalog, IonStruct ionRep) {
        IonSystem imageFactory = ionRep.getSystem();
        IonReaderTreeSystem reader = new IonReaderTreeSystem(ionRep);
        LocalSymbolTable table = LocalSymbolTable.makeNewLocalSymbolTable((ValueFactory)imageFactory, systemSymbolTable, catalog, reader, false);
        table.myImage = ionRep;
        return table;
    }

    static LocalSymbolTable makeNewLocalSymbolTable(ValueFactory imageFactory, SymbolTable systemSymbolTable, IonCatalog catalog, IonReader reader, boolean isOnStruct) {
        IonType fieldType;
        if (!isOnStruct) {
            reader.next();
        }
        assert (reader.getType() == IonType.STRUCT) : "invalid symbol table image passed in reader " + (Object)((Object)reader.getType()) + " encountered when a struct was expected";
        assert ("$ion_symbol_table".equals(reader.getTypeAnnotations()[0])) : "local symbol tables must be annotated by $ion_symbol_table";
        reader.stepIn();
        ArrayList<String> symbolsList = new ArrayList<String>();
        ArrayList<SymbolTable> importsList = new ArrayList<SymbolTable>();
        importsList.add(systemSymbolTable);
        while ((fieldType = reader.next()) != null) {
            if (reader.isNullValue()) continue;
            SymbolToken symTok = reader.getFieldNameSymbol();
            int sid = symTok.getSid();
            if (sid == -1) {
                String fieldName = reader.getFieldName();
                sid = PrivateUtils.getSidForSymbolTableField(fieldName);
            }
            switch (sid) {
                case 7: {
                    IonType type;
                    if (fieldType != IonType.LIST) break;
                    reader.stepIn();
                    while ((type = reader.next()) != null) {
                        String text = null;
                        if (type == IonType.STRING && (text = reader.stringValue()) != null && text.length() == 0) {
                            text = null;
                        }
                        symbolsList.add(text);
                    }
                    reader.stepOut();
                    break;
                }
                case 6: {
                    if (fieldType != IonType.LIST) break;
                    LocalSymbolTable.prepImportsList(importsList, reader, catalog);
                    break;
                }
            }
        }
        reader.stepOut();
        LocalSymbolTableImports imports = new LocalSymbolTableImports(importsList);
        return new LocalSymbolTable(imageFactory, imports, symbolsList);
    }

    synchronized LocalSymbolTable makeCopy() {
        return new LocalSymbolTable(this, this.getMaxId());
    }

    synchronized LocalSymbolTable makeCopy(int maxId) {
        return new LocalSymbolTable(this, maxId);
    }

    @Override
    public boolean isLocalTable() {
        return true;
    }

    @Override
    public boolean isSharedTable() {
        return false;
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
    public synchronized boolean isReadOnly() {
        return this.isReadOnly;
    }

    @Override
    public synchronized void makeReadOnly() {
        this.isReadOnly = true;
    }

    @Override
    public int getImportedMaxId() {
        return this.myImportsList.getMaxId();
    }

    @Override
    public synchronized int getMaxId() {
        int maxid = this.mySymbolsCount + this.myImportsList.getMaxId();
        return maxid;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getIonVersionId() {
        SymbolTable system_table = this.myImportsList.getSystemSymbolTable();
        return system_table.getIonVersionId();
    }

    @Override
    public synchronized Iterator<String> iterateDeclaredSymbolNames() {
        return new SymbolIterator(this.mySymbolNames, this.mySymbolsCount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String findKnownSymbol(int id) {
        String name = null;
        if (id < 1) {
            String message = "symbol IDs must be greater than 0";
            throw new IllegalArgumentException(message);
        }
        if (id < this.myFirstLocalSid) {
            name = this.myImportsList.findKnownSymbol(id);
        } else {
            String[] names;
            int offset = id - this.myFirstLocalSid;
            LocalSymbolTable localSymbolTable = this;
            synchronized (localSymbolTable) {
                names = this.mySymbolNames;
            }
            if (offset < names.length) {
                name = names[offset];
            }
        }
        return name;
    }

    @Override
    public int findSymbol(String name) {
        if (name.length() < 1) {
            throw new EmptySymbolException();
        }
        int sid = this.myImportsList.findSymbol(name);
        if (sid == -1) {
            sid = this.findLocalSymbol(name);
        }
        return sid;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int findLocalSymbol(String name) {
        Integer isid;
        assert (name.length() > 0);
        LocalSymbolTable localSymbolTable = this;
        synchronized (localSymbolTable) {
            isid = this.mySymbolsMap.get(name);
        }
        if (isid != null) {
            assert (isid != -1);
            return isid;
        }
        return -1;
    }

    @Override
    public synchronized SymbolToken intern(String text) {
        SymbolToken is = this.find(text);
        if (is == null) {
            LocalSymbolTable.validateSymbol(text);
            int sid = this.putSymbol(text);
            is = new SymbolTokenImpl(text, sid);
        }
        return is;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SymbolToken find(String text) {
        if (text.length() < 1) {
            throw new EmptySymbolException();
        }
        SymbolToken symTok = this.myImportsList.find(text);
        if (symTok == null) {
            String[] names;
            Integer sid;
            LocalSymbolTable localSymbolTable = this;
            synchronized (localSymbolTable) {
                sid = this.mySymbolsMap.get(text);
                names = this.mySymbolNames;
            }
            if (sid != null) {
                int offset = sid - this.myFirstLocalSid;
                String internedText = names[offset];
                assert (internedText != null);
                symTok = new SymbolTokenImpl(internedText, sid);
            }
        }
        return symTok;
    }

    private static final void validateSymbol(String name) {
        if (name == null || name.length() < 1) {
            String message = "symbols must contain 1 or more characters";
            throw new IllegalArgumentException(message);
        }
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (c < '\ud800' || c > '\udfff') continue;
            if (c >= '\udc00') {
                String message = "unpaired trailing surrogate in symbol name at position " + i;
                throw new IllegalArgumentException(message);
            }
            if (++i == name.length()) {
                String message = "unmatched leading surrogate in symbol name at position " + i;
                throw new IllegalArgumentException(message);
            }
            c = name.charAt(i);
            if (c >= '\udc00' && c <= '\udfff') continue;
            String message = "unmatched leading surrogate in symbol name at position " + i;
            throw new IllegalArgumentException(message);
        }
    }

    private int putSymbol(String symbolName) {
        assert (symbolName.length() != 0);
        if (this.isReadOnly) {
            throw new ReadOnlyValueException(SymbolTable.class);
        }
        if (this.mySymbolsCount == this.mySymbolNames.length) {
            int newlen = this.mySymbolsCount * 2;
            if (newlen < 16) {
                newlen = 16;
            }
            String[] temp = new String[newlen];
            System.arraycopy(this.mySymbolNames, 0, temp, 0, this.mySymbolsCount);
            this.mySymbolNames = temp;
        }
        int sid = this.mySymbolsCount + this.myFirstLocalSid;
        assert (sid == this.getMaxId() + 1);
        LocalSymbolTable.putToMapIfNotThere(this.mySymbolsMap, symbolName, sid);
        this.mySymbolNames[this.mySymbolsCount] = symbolName;
        ++this.mySymbolsCount;
        if (this.myImage != null) {
            this.recordLocalSymbolInIonRep(this.myImage, symbolName, sid);
        }
        return sid;
    }

    private static void putToMapIfNotThere(Map<String, Integer> symbolsMap, String text, int sid) {
        Integer extantSid = symbolsMap.put(text, sid);
        if (extantSid != null) {
            assert (extantSid < sid);
            symbolsMap.put(text, extantSid);
        }
    }

    @Override
    public SymbolTable getSystemSymbolTable() {
        return this.myImportsList.getSystemSymbolTable();
    }

    @Override
    public SymbolTable[] getImportedTables() {
        return this.myImportsList.getImportedTables();
    }

    SymbolTable[] getImportedTablesNoCopy() {
        return this.myImportsList.getImportedTablesNoCopy();
    }

    @Override
    public void writeTo(IonWriter writer) throws IOException {
        SymbolTableReader reader = new SymbolTableReader(this);
        writer.writeValues(reader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    IonStruct getIonRepresentation(ValueFactory imageFactory) {
        IonStruct image;
        if (imageFactory == null) {
            throw new IonExceptionNoSystem("can't create representation without a system");
        }
        if (imageFactory != this.myImageFactory) {
            throw new IonException("wrong system");
        }
        LocalSymbolTable localSymbolTable = this;
        synchronized (localSymbolTable) {
            image = this.myImage;
            if (image == null) {
                this.myImage = image = this.makeIonRepresentation(this.myImageFactory);
            }
        }
        return image;
    }

    private IonStruct makeIonRepresentation(ValueFactory factory) {
        IonStruct ionRep = factory.newEmptyStruct();
        ionRep.addTypeAnnotation("$ion_symbol_table");
        SymbolTable[] importedTables = this.getImportedTablesNoCopy();
        if (importedTables.length > 1) {
            IonList importsList = factory.newEmptyList();
            for (int i = 1; i < importedTables.length; ++i) {
                SymbolTable importedTable = importedTables[i];
                IonStruct importStruct = factory.newEmptyStruct();
                importStruct.add("name", (IonValue)factory.newString(importedTable.getName()));
                importStruct.add("version", (IonValue)factory.newInt(importedTable.getVersion()));
                importStruct.add("max_id", (IonValue)factory.newInt(importedTable.getMaxId()));
                importsList.add(importStruct);
            }
            ionRep.add("imports", (IonValue)importsList);
        }
        if (this.mySymbolsCount > 0) {
            int sid = this.myFirstLocalSid;
            int offset = 0;
            while (offset < this.mySymbolsCount) {
                String symbolName = this.mySymbolNames[offset];
                this.recordLocalSymbolInIonRep(ionRep, symbolName, sid);
                ++offset;
                ++sid;
            }
        }
        return ionRep;
    }

    private void recordLocalSymbolInIonRep(IonStruct ionRep, String symbolName, int sid) {
        assert (sid >= this.myFirstLocalSid);
        IonSystem sys = ionRep.getSystem();
        IonValue syms = ionRep.get("symbols");
        while (syms != null && syms.getType() != IonType.LIST) {
            ionRep.remove(syms);
            syms = ionRep.get("symbols");
        }
        if (syms == null) {
            syms = sys.newEmptyList();
            ionRep.put("symbols", syms);
        }
        int this_offset = sid - this.myFirstLocalSid;
        IonString name = sys.newString(symbolName);
        ((IonList)syms).add(this_offset, name);
    }

    private static void prepImportsList(List<SymbolTable> importsList, IonReader reader, IonCatalog catalog) {
        IonType t;
        assert ("imports".equals(reader.getFieldName()));
        reader.stepIn();
        while ((t = reader.next()) != null) {
            SymbolTable importedTable;
            if (reader.isNullValue() || t != IonType.STRUCT || (importedTable = LocalSymbolTable.readOneImport(reader, catalog)) == null) continue;
            importsList.add(importedTable);
        }
        reader.stepOut();
    }

    private static SymbolTable readOneImport(IonReader ionRep, IonCatalog catalog) {
        IonType t;
        assert (ionRep.getType() == IonType.STRUCT);
        String name = null;
        int version = -1;
        int maxid = -1;
        ionRep.stepIn();
        while ((t = ionRep.next()) != null) {
            if (ionRep.isNullValue()) continue;
            SymbolToken symTok = ionRep.getFieldNameSymbol();
            int field_id = symTok.getSid();
            if (field_id == -1) {
                String fieldName = ionRep.getFieldName();
                field_id = PrivateUtils.getSidForSymbolTableField(fieldName);
            }
            switch (field_id) {
                case 4: {
                    if (t != IonType.STRING) break;
                    name = ionRep.stringValue();
                    break;
                }
                case 5: {
                    if (t != IonType.INT) break;
                    version = ionRep.intValue();
                    break;
                }
                case 8: {
                    if (t != IonType.INT) break;
                    maxid = ionRep.intValue();
                    break;
                }
            }
        }
        ionRep.stepOut();
        if (name == null || name.length() == 0 || name.equals("$ion")) {
            return null;
        }
        if (version < 1) {
            version = 1;
        }
        SymbolTable itab = null;
        if (catalog != null) {
            itab = catalog.getTable(name, version);
        }
        if (maxid < 0) {
            if (itab == null || version != itab.getVersion()) {
                String message = "Import of shared table " + IonTextUtils.printString(name) + " lacks a valid max_id field, but an exact match was not" + " found in the catalog";
                if (itab != null) {
                    message = message + " (found version " + itab.getVersion() + ")";
                }
                throw new IonException(message);
            }
            maxid = itab.getMaxId();
        }
        if (itab == null) {
            assert (maxid >= 0);
            itab = new SubstituteSymbolTable(name, version, maxid);
        } else if (itab.getVersion() != version || itab.getMaxId() != maxid) {
            itab = new SubstituteSymbolTable(itab, version, maxid);
        }
        return itab;
    }

    public static String unknownSymbolName(int id) {
        assert (id > 0);
        return "$" + id;
    }

    public String toString() {
        return "(LocalSymbolTable max_id:" + this.getMaxId() + ')';
    }

    boolean symtabExtends(SymbolTable other) {
        LocalSymbolTable subset = (LocalSymbolTable)other;
        if (this.getMaxId() < subset.getMaxId()) {
            return false;
        }
        if (!this.myImportsList.equalImports(subset.myImportsList)) {
            return false;
        }
        int subLocalSymbolCount = subset.mySymbolsCount;
        if (subLocalSymbolCount == 0) {
            return true;
        }
        if (this.mySymbolsCount < subLocalSymbolCount) {
            return false;
        }
        String[] subsetSymbols = subset.mySymbolNames;
        if (!PrivateUtils.safeEquals(this.mySymbolNames[subLocalSymbolCount - 1], subsetSymbols[subLocalSymbolCount - 1])) {
            return false;
        }
        for (int i = 0; i < subLocalSymbolCount - 1; ++i) {
            if (PrivateUtils.safeEquals(this.mySymbolNames[i], subsetSymbols[i])) continue;
            return false;
        }
        return true;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class SymbolIterator
    implements Iterator<String> {
        private final String[] mySymbolNames;
        private final int mySymbolsCount;
        private int _idx = 0;

        SymbolIterator(String[] symbolNames, int count) {
            this.mySymbolNames = symbolNames;
            this.mySymbolsCount = count;
        }

        @Override
        public boolean hasNext() {
            return this._idx < this.mySymbolsCount;
        }

        @Override
        public String next() {
            if (this._idx < this.mySymbolsCount) {
                return this.mySymbolNames[this._idx++];
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static class IonExceptionNoSystem
    extends IonException {
        private static final long serialVersionUID = 1L;

        IonExceptionNoSystem(String msg) {
            super(msg);
        }
    }
}

