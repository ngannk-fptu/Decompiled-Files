/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import software.amazon.ion.EmptySymbolException;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonException;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.SubstituteSymbolTableException;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.UnknownSymbolException;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.Base64Encoder;
import software.amazon.ion.impl.IonIteratorImpl;
import software.amazon.ion.impl.IonUTF8;
import software.amazon.ion.impl.LocalSymbolTable;
import software.amazon.ion.impl.PrivateIonConstants;
import software.amazon.ion.impl.SharedSymbolTable;
import software.amazon.ion.impl.SubstituteSymbolTable;
import software.amazon.ion.impl.SymbolTokenImpl;
import software.amazon.ion.util.IonStreamUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public final class PrivateUtils {
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final String ASCII_CHARSET_NAME = "US-ASCII";
    public static final Charset ASCII_CHARSET = Charset.forName("US-ASCII");
    public static final String UTF8_CHARSET_NAME = "UTF-8";
    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final ListIterator<?> EMPTY_ITERATOR = new ListIterator(){

        public boolean hasNext() {
            return false;
        }

        public boolean hasPrevious() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        public Object previous() {
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new IllegalStateException();
        }

        public int nextIndex() {
            return 0;
        }

        public int previousIndex() {
            return -1;
        }

        public void add(Object o) {
            throw new UnsupportedOperationException();
        }

        public void set(Object o) {
            throw new UnsupportedOperationException();
        }
    };

    public static final <T> ListIterator<T> emptyIterator() {
        return EMPTY_ITERATOR;
    }

    public static boolean safeEquals(Object a, Object b) {
        return a != null ? a.equals(b) : b == null;
    }

    public static byte[] copyOf(byte[] original, int newLength) {
        byte[] result = new byte[newLength];
        System.arraycopy(original, 0, result, 0, Math.min(newLength, original.length));
        return result;
    }

    public static String[] copyOf(String[] original, int newLength) {
        String[] result = new String[newLength];
        System.arraycopy(original, 0, result, 0, Math.min(newLength, original.length));
        return result;
    }

    public static <T> void addAll(Collection<T> dest, Iterator<T> src) {
        if (src != null) {
            while (src.hasNext()) {
                T value = src.next();
                dest.add(value);
            }
        }
    }

    public static <T> void addAllNonNull(Collection<T> dest, Iterator<T> src) {
        if (src != null) {
            while (src.hasNext()) {
                T value = src.next();
                if (value == null) continue;
                dest.add(value);
            }
        }
    }

    public static void ensureNonEmptySymbols(String[] strings) {
        for (String s : strings) {
            if (s != null && s.length() != 0) continue;
            throw new EmptySymbolException();
        }
    }

    public static void ensureNonEmptySymbols(SymbolToken[] symbols) {
        for (SymbolToken s : symbols) {
            if (s != null && (s.getText() == null || s.getText().length() != 0)) continue;
            throw new EmptySymbolException();
        }
    }

    public static SymbolTokenImpl newSymbolToken(String text, int sid) {
        return new SymbolTokenImpl(text, sid);
    }

    public static SymbolTokenImpl newSymbolToken(int sid) {
        return new SymbolTokenImpl(sid);
    }

    public static SymbolToken newSymbolToken(SymbolTable symtab, String text) {
        SymbolToken tok;
        if (text == null || text.length() == 0) {
            throw new EmptySymbolException();
        }
        SymbolToken symbolToken = tok = symtab == null ? null : symtab.find(text);
        if (tok == null) {
            tok = new SymbolTokenImpl(text, -1);
        }
        return tok;
    }

    public static SymbolToken newSymbolToken(SymbolTable symtab, int sid) {
        if (sid < 1) {
            throw new IllegalArgumentException();
        }
        String text = symtab == null ? null : symtab.findKnownSymbol(sid);
        return new SymbolTokenImpl(text, sid);
    }

    public static SymbolToken[] newSymbolTokens(SymbolTable symtab, String ... text) {
        int count;
        if (text != null && (count = text.length) != 0) {
            SymbolToken[] result = new SymbolToken[count];
            for (int i = 0; i < count; ++i) {
                String s = text[i];
                result[i] = PrivateUtils.newSymbolToken(symtab, s);
            }
            return result;
        }
        return SymbolToken.EMPTY_ARRAY;
    }

    public static SymbolToken[] newSymbolTokens(SymbolTable symtab, int ... syms) {
        if (syms != null) {
            int count = syms.length;
            if (syms.length != 0) {
                SymbolToken[] result = new SymbolToken[count];
                for (int i = 0; i < count; ++i) {
                    int s = syms[i];
                    result[i] = PrivateUtils.newSymbolToken(symtab, s);
                }
                return result;
            }
        }
        return SymbolToken.EMPTY_ARRAY;
    }

    public static SymbolToken localize(SymbolTable symtab, SymbolToken sym) {
        String text = sym.getText();
        int sid = sym.getSid();
        if (symtab != null) {
            if (text == null) {
                text = symtab.findKnownSymbol(sid);
                if (text != null) {
                    sym = new SymbolTokenImpl(text, sid);
                }
            } else {
                SymbolToken newSym = symtab.find(text);
                if (newSym != null) {
                    sym = newSym;
                } else if (sid >= 0) {
                    sym = new SymbolTokenImpl(text, -1);
                }
            }
        } else if (text != null && sid >= 0) {
            sym = new SymbolTokenImpl(text, -1);
        }
        return sym;
    }

    public static void localize(SymbolTable symtab, SymbolToken[] syms, int count) {
        for (int i = 0; i < count; ++i) {
            SymbolToken sym = syms[i];
            SymbolToken updated = PrivateUtils.localize(symtab, sym);
            if (updated == sym) continue;
            syms[i] = updated;
        }
    }

    public static void localize(SymbolTable symtab, SymbolToken[] syms) {
        PrivateUtils.localize(symtab, syms, syms.length);
    }

    public static String[] toStrings(SymbolToken[] symbols, int count) {
        if (count == 0) {
            return EMPTY_STRING_ARRAY;
        }
        String[] annotations = new String[count];
        for (int i = 0; i < count; ++i) {
            SymbolToken tok = symbols[i];
            String text = tok.getText();
            if (text == null) {
                throw new UnknownSymbolException(tok.getSid());
            }
            annotations[i] = text;
        }
        return annotations;
    }

    public static int[] toSids(SymbolToken[] symbols, int count) {
        if (count == 0) {
            return EMPTY_INT_ARRAY;
        }
        int[] sids = new int[count];
        for (int i = 0; i < count; ++i) {
            sids[i] = symbols[i].getSid();
        }
        return sids;
    }

    public static byte[] encode(String s, Charset charset) {
        CharsetEncoder encoder = charset.newEncoder();
        try {
            ByteBuffer buffer = encoder.encode(CharBuffer.wrap(s));
            byte[] bytes = buffer.array();
            int limit = buffer.limit();
            if (limit < bytes.length) {
                bytes = PrivateUtils.copyOf(bytes, limit);
            }
            return bytes;
        }
        catch (CharacterCodingException e) {
            throw new IonException("Invalid string data", e);
        }
    }

    public static String decode(byte[] bytes, Charset charset) {
        CharsetDecoder decoder = charset.newDecoder();
        try {
            CharBuffer buffer = decoder.decode(ByteBuffer.wrap(bytes));
            return buffer.toString();
        }
        catch (CharacterCodingException e) {
            String message = "Input is not valid " + charset.displayName() + " data";
            throw new IonException(message, e);
        }
    }

    public static byte[] utf8(String s) {
        return PrivateUtils.encode(s, UTF8_CHARSET);
    }

    public static String utf8(byte[] bytes) {
        return PrivateUtils.decode(bytes, UTF8_CHARSET);
    }

    public static byte[] convertUtf16UnitsToUtf8(String text) {
        byte[] data = new byte[4 * text.length()];
        int limit = 0;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            limit += IonUTF8.convertToUTF8Bytes(c, data, limit, data.length - limit);
        }
        byte[] result = new byte[limit];
        System.arraycopy(data, 0, result, 0, limit);
        return result;
    }

    public static int readFully(InputStream in, byte[] buf) throws IOException {
        return PrivateUtils.readFully(in, buf, 0, buf.length);
    }

    public static int readFully(InputStream in, byte[] buf, int offset, int length) throws IOException {
        int readBytes = 0;
        while (readBytes < length) {
            int amount = in.read(buf, offset, length - readBytes);
            if (amount < 0) {
                return readBytes;
            }
            readBytes += amount;
            offset += amount;
        }
        return readBytes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] loadFileBytes(File file) throws IOException {
        long len = file.length();
        if (len < 0L || len > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("File too long: " + file);
        }
        byte[] buf = new byte[(int)len];
        FileInputStream in = new FileInputStream(file);
        try {
            int readBytesCount = in.read(buf);
            if ((long)readBytesCount != len || in.read() != -1) {
                throw new IOException("Read the wrong number of bytes from " + file);
            }
        }
        finally {
            in.close();
        }
        return buf;
    }

    public static String utf8FileToString(File file) throws IonException, IOException {
        byte[] utf8Bytes = PrivateUtils.loadFileBytes(file);
        String s = PrivateUtils.utf8(utf8Bytes);
        return s;
    }

    public static String loadReader(Reader in) throws IOException {
        int len;
        StringBuilder buf = new StringBuilder(2048);
        char[] chars = new char[2048];
        while ((len = in.read(chars)) != -1) {
            buf.append(chars, 0, len);
        }
        return buf.toString();
    }

    public static boolean streamIsIonBinary(PushbackInputStream pushback) throws IonException, IOException {
        boolean isBinary = false;
        byte[] cookie = new byte[PrivateIonConstants.BINARY_VERSION_MARKER_SIZE];
        int len = PrivateUtils.readFully(pushback, cookie);
        if (len == PrivateIonConstants.BINARY_VERSION_MARKER_SIZE) {
            isBinary = IonStreamUtils.isIonBinary(cookie);
        }
        if (len > 0) {
            pushback.unread(cookie, 0, len);
        }
        return isBinary;
    }

    public static Iterator<IonValue> iterate(ValueFactory valueFactory, IonReader input) {
        return new IonIteratorImpl(valueFactory, input);
    }

    public static boolean valueIsLocalSymbolTable(IonValue v) {
        return v instanceof IonStruct && v.hasTypeAnnotation("$ion_symbol_table");
    }

    public static final boolean symtabIsSharedNotSystem(SymbolTable symtab) {
        return symtab != null && symtab.isSharedTable() && !symtab.isSystemTable();
    }

    public static boolean symtabIsLocalAndNonTrivial(SymbolTable symtab) {
        if (symtab == null) {
            return false;
        }
        if (!symtab.isLocalTable()) {
            return false;
        }
        if (symtab.getImportedTables().length > 0) {
            return true;
        }
        return symtab.getImportedMaxId() < symtab.getMaxId();
    }

    public static boolean isTrivialTable(SymbolTable table) {
        if (table == null) {
            return true;
        }
        if (table.isSystemTable()) {
            return true;
        }
        return table.isLocalTable() && table.getMaxId() == table.getSystemSymbolTable().getMaxId();
    }

    public static SymbolTable systemSymtab(int version) {
        return SharedSymbolTable.getSystemSymbolTable(version);
    }

    public static SymbolTable newSharedSymtab(IonStruct ionRep) {
        return SharedSymbolTable.newSharedSymbolTable(ionRep);
    }

    public static SymbolTable newSharedSymtab(IonReader reader, boolean alreadyInStruct) {
        return SharedSymbolTable.newSharedSymbolTable(reader, alreadyInStruct);
    }

    public static SymbolTable newSharedSymtab(String name, int version, SymbolTable priorSymtab, Iterator<String> symbols) {
        return SharedSymbolTable.newSharedSymbolTable(name, version, priorSymtab, symbols);
    }

    public static SymbolTable newLocalSymtab(ValueFactory imageFactory, SymbolTable systemSymtab, List<String> localSymbols, SymbolTable ... imports) {
        return LocalSymbolTable.makeNewLocalSymbolTable(imageFactory, systemSymtab, localSymbols, imports);
    }

    public static SymbolTable newLocalSymtab(ValueFactory imageFactory, SymbolTable systemSymtab, SymbolTable ... imports) {
        return LocalSymbolTable.makeNewLocalSymbolTable(imageFactory, systemSymtab, null, imports);
    }

    public static SymbolTable newLocalSymtab(SymbolTable systemSymbtab, IonCatalog catalog, IonStruct ionRep) {
        return LocalSymbolTable.makeNewLocalSymbolTable(systemSymbtab, catalog, ionRep);
    }

    public static SymbolTable newLocalSymtab(ValueFactory imageFactory, SymbolTable systemSymbolTable, IonCatalog catalog, IonReader reader, boolean alreadyInStruct) {
        return LocalSymbolTable.makeNewLocalSymbolTable(imageFactory, systemSymbolTable, catalog, reader, alreadyInStruct);
    }

    public static SymbolTable newSubstituteSymtab(SymbolTable original, int version, int maxId) {
        return new SubstituteSymbolTable(original, version, maxId);
    }

    public static SymbolTable copyLocalSymbolTable(SymbolTable symtab) throws SubstituteSymbolTableException {
        if (!symtab.isLocalTable()) {
            String message = "symtab should be a local symtab";
            throw new IllegalArgumentException(message);
        }
        SymbolTable[] imports = ((LocalSymbolTable)symtab).getImportedTablesNoCopy();
        for (int i = 0; i < imports.length; ++i) {
            if (!imports[i].isSubstitute()) continue;
            String message = "local symtabs with substituted symtabs for imports (indicating no exact match within the catalog) cannot be copied";
            throw new SubstituteSymbolTableException(message);
        }
        return ((LocalSymbolTable)symtab).makeCopy();
    }

    public static SymbolTable initialSymtab(ValueFactory imageFactory, SymbolTable defaultSystemSymtab, SymbolTable ... imports) {
        if (imports == null || imports.length == 0) {
            return defaultSystemSymtab;
        }
        if (imports.length == 1 && imports[0].isSystemTable()) {
            return imports[0];
        }
        return LocalSymbolTable.makeNewLocalSymbolTable(imageFactory, defaultSystemSymtab, null, imports);
    }

    public static IonStruct symtabTree(ValueFactory vf, SymbolTable symtab) {
        return ((LocalSymbolTable)symtab).getIonRepresentation(vf);
    }

    private static boolean localSymtabExtends(SymbolTable superset, SymbolTable subset) {
        SymbolTable[] subsetImports;
        if (subset.getMaxId() > superset.getMaxId()) {
            return false;
        }
        SymbolTable[] supersetImports = superset.getImportedTables();
        if (supersetImports.length != (subsetImports = subset.getImportedTables()).length) {
            return false;
        }
        for (int i = 0; i < supersetImports.length; ++i) {
            SymbolTable supersetImport = supersetImports[i];
            SymbolTable subsetImport = subsetImports[i];
            if (supersetImport.getName().equals(subsetImport.getName()) && supersetImport.getVersion() == subsetImport.getVersion()) continue;
            return false;
        }
        Iterator<String> supersetIter = superset.iterateDeclaredSymbolNames();
        Iterator<String> subsetIter = subset.iterateDeclaredSymbolNames();
        while (subsetIter.hasNext()) {
            String nextSupersetSymbol;
            String nextSubsetSymbol = subsetIter.next();
            if (nextSubsetSymbol.equals(nextSupersetSymbol = supersetIter.next())) continue;
            return false;
        }
        return true;
    }

    public static boolean symtabExtends(SymbolTable superset, SymbolTable subset) {
        assert (superset.isSystemTable() || superset.isLocalTable());
        assert (subset.isSystemTable() || subset.isLocalTable());
        if (superset == subset) {
            return true;
        }
        if (subset.isSystemTable()) {
            return true;
        }
        if (superset.isLocalTable()) {
            if (superset instanceof LocalSymbolTable && subset instanceof LocalSymbolTable) {
                return ((LocalSymbolTable)superset).symtabExtends(subset);
            }
            return PrivateUtils.localSymtabExtends(superset, subset);
        }
        return subset.getMaxId() == superset.getMaxId();
    }

    public static boolean isNonSymbolScalar(IonType type) {
        return !IonType.isContainer(type) && !type.equals((Object)IonType.SYMBOL);
    }

    public static final int getSidForSymbolTableField(String text) {
        int shortestFieldNameLength = 4;
        if (text != null && text.length() >= 4) {
            char c = text.charAt(0);
            switch (c) {
                case 'v': {
                    if (!"version".equals(text)) break;
                    return 5;
                }
                case 'n': {
                    if (!"name".equals(text)) break;
                    return 4;
                }
                case 's': {
                    if (!"symbols".equals(text)) break;
                    return 7;
                }
                case 'i': {
                    if (!"imports".equals(text)) break;
                    return 6;
                }
                case 'm': {
                    if (!"max_id".equals(text)) break;
                    return 8;
                }
            }
        }
        return -1;
    }

    public static final Iterator<String> stringIterator(String ... values) {
        if (values == null || values.length == 0) {
            return PrivateUtils.emptyIterator();
        }
        return new StringIterator(values, values.length);
    }

    public static final Iterator<String> stringIterator(String[] values, int len) {
        if (values == null || values.length == 0 || len == 0) {
            return PrivateUtils.emptyIterator();
        }
        return new StringIterator(values, len);
    }

    public static final Iterator<Integer> intIterator(int ... values) {
        if (values == null || values.length == 0) {
            return PrivateUtils.emptyIterator();
        }
        return new IntIterator(values);
    }

    public static final Iterator<Integer> intIterator(int[] values, int len) {
        if (values == null || values.length == 0 || len == 0) {
            return PrivateUtils.emptyIterator();
        }
        return new IntIterator(values, 0, len);
    }

    public static void writeAsBase64(InputStream byteStream, Appendable out) throws IOException {
        int c;
        Base64Encoder.TextStream ts = new Base64Encoder.TextStream(byteStream);
        while ((c = ts.read()) != -1) {
            out.append((char)c);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class IntIterator
    implements Iterator<Integer> {
        private final int[] _values;
        private int _pos;
        private final int _len;

        IntIterator(int[] values) {
            this(values, 0, values.length);
        }

        IntIterator(int[] values, int off, int len) {
            this._values = values;
            this._len = len;
            this._pos = off;
        }

        @Override
        public boolean hasNext() {
            return this._pos < this._len;
        }

        @Override
        public Integer next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            int value = this._values[this._pos++];
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class StringIterator
    implements Iterator<String> {
        private final String[] _values;
        private int _pos;
        private final int _len;

        StringIterator(String[] values, int len) {
            this._values = values;
            this._len = len;
        }

        @Override
        public boolean hasNext() {
            return this._pos < this._len;
        }

        @Override
        public String next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this._values[this._pos++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

