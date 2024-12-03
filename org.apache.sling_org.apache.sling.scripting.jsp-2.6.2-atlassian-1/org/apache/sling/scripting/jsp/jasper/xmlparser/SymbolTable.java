/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.xmlparser;

public class SymbolTable {
    protected static final int TABLE_SIZE = 101;
    protected Entry[] fBuckets = null;
    protected int fTableSize;

    public SymbolTable() {
        this(101);
    }

    public SymbolTable(int tableSize) {
        this.fTableSize = tableSize;
        this.fBuckets = new Entry[this.fTableSize];
    }

    public String addSymbol(String symbol) {
        int bucket = this.hash(symbol) % this.fTableSize;
        int length = symbol.length();
        Entry entry = this.fBuckets[bucket];
        while (entry != null) {
            block4: {
                if (length == entry.characters.length) {
                    for (int i = 0; i < length; ++i) {
                        if (symbol.charAt(i) == entry.characters[i]) {
                            continue;
                        }
                        break block4;
                    }
                    return entry.symbol;
                }
            }
            entry = entry.next;
        }
        this.fBuckets[bucket] = entry = new Entry(symbol, this.fBuckets[bucket]);
        return entry.symbol;
    }

    public String addSymbol(char[] buffer, int offset, int length) {
        int bucket = this.hash(buffer, offset, length) % this.fTableSize;
        Entry entry = this.fBuckets[bucket];
        while (entry != null) {
            block4: {
                if (length == entry.characters.length) {
                    for (int i = 0; i < length; ++i) {
                        if (buffer[offset + i] == entry.characters[i]) {
                            continue;
                        }
                        break block4;
                    }
                    return entry.symbol;
                }
            }
            entry = entry.next;
        }
        this.fBuckets[bucket] = entry = new Entry(buffer, offset, length, this.fBuckets[bucket]);
        return entry.symbol;
    }

    public int hash(String symbol) {
        int code = 0;
        int length = symbol.length();
        for (int i = 0; i < length; ++i) {
            code = code * 37 + symbol.charAt(i);
        }
        return code & 0x7FFFFFF;
    }

    public int hash(char[] buffer, int offset, int length) {
        int code = 0;
        for (int i = 0; i < length; ++i) {
            code = code * 37 + buffer[offset + i];
        }
        return code & 0x7FFFFFF;
    }

    public boolean containsSymbol(String symbol) {
        int bucket = this.hash(symbol) % this.fTableSize;
        int length = symbol.length();
        Entry entry = this.fBuckets[bucket];
        while (entry != null) {
            block4: {
                if (length == entry.characters.length) {
                    for (int i = 0; i < length; ++i) {
                        if (symbol.charAt(i) == entry.characters[i]) {
                            continue;
                        }
                        break block4;
                    }
                    return true;
                }
            }
            entry = entry.next;
        }
        return false;
    }

    public boolean containsSymbol(char[] buffer, int offset, int length) {
        int bucket = this.hash(buffer, offset, length) % this.fTableSize;
        Entry entry = this.fBuckets[bucket];
        while (entry != null) {
            block4: {
                if (length == entry.characters.length) {
                    for (int i = 0; i < length; ++i) {
                        if (buffer[offset + i] == entry.characters[i]) {
                            continue;
                        }
                        break block4;
                    }
                    return true;
                }
            }
            entry = entry.next;
        }
        return false;
    }

    protected static final class Entry {
        public String symbol;
        public char[] characters;
        public Entry next;

        public Entry(String symbol, Entry next) {
            this.symbol = symbol.intern();
            this.characters = new char[symbol.length()];
            symbol.getChars(0, this.characters.length, this.characters, 0);
            this.next = next;
        }

        public Entry(char[] ch, int offset, int length, Entry next) {
            this.characters = new char[length];
            System.arraycopy(ch, offset, this.characters, 0, length);
            this.symbol = new String(this.characters).intern();
            this.next = next;
        }
    }
}

