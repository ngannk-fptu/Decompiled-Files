/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

public class XMLGrammarPoolImpl
implements XMLGrammarPool {
    protected static final int TABLE_SIZE = 11;
    protected Entry[] fGrammars = null;
    protected boolean fPoolIsLocked;
    protected int fGrammarCount = 0;
    private static final boolean DEBUG = false;

    public XMLGrammarPoolImpl() {
        this.fGrammars = new Entry[11];
        this.fPoolIsLocked = false;
    }

    public XMLGrammarPoolImpl(int n) {
        this.fGrammars = new Entry[n];
        this.fPoolIsLocked = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Grammar[] retrieveInitialGrammarSet(String string) {
        Entry[] entryArray = this.fGrammars;
        synchronized (this.fGrammars) {
            int n = this.fGrammars.length;
            Grammar[] grammarArray = new Grammar[this.fGrammarCount];
            int n2 = 0;
            for (int i = 0; i < n; ++i) {
                Entry entry = this.fGrammars[i];
                while (entry != null) {
                    if (entry.desc.getGrammarType().equals(string)) {
                        grammarArray[n2++] = entry.grammar;
                    }
                    entry = entry.next;
                }
            }
            Grammar[] grammarArray2 = new Grammar[n2];
            System.arraycopy(grammarArray, 0, grammarArray2, 0, n2);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return grammarArray2;
        }
    }

    @Override
    public void cacheGrammars(String string, Grammar[] grammarArray) {
        if (!this.fPoolIsLocked) {
            for (int i = 0; i < grammarArray.length; ++i) {
                this.putGrammar(grammarArray[i]);
            }
        }
    }

    @Override
    public Grammar retrieveGrammar(XMLGrammarDescription xMLGrammarDescription) {
        return this.getGrammar(xMLGrammarDescription);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void putGrammar(Grammar grammar) {
        if (this.fPoolIsLocked) return;
        Entry[] entryArray = this.fGrammars;
        synchronized (this.fGrammars) {
            XMLGrammarDescription xMLGrammarDescription = grammar.getGrammarDescription();
            int n = this.hashCode(xMLGrammarDescription);
            int n2 = (n & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry entry = this.fGrammars[n2];
            while (entry != null) {
                if (entry.hash == n && this.equals(entry.desc, xMLGrammarDescription)) {
                    entry.grammar = grammar;
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return;
                }
                entry = entry.next;
            }
            this.fGrammars[n2] = entry = new Entry(n, xMLGrammarDescription, grammar, this.fGrammars[n2]);
            ++this.fGrammarCount;
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Grammar getGrammar(XMLGrammarDescription xMLGrammarDescription) {
        Entry[] entryArray = this.fGrammars;
        synchronized (this.fGrammars) {
            int n = this.hashCode(xMLGrammarDescription);
            int n2 = (n & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry entry = this.fGrammars[n2];
            while (entry != null) {
                if (entry.hash == n && this.equals(entry.desc, xMLGrammarDescription)) {
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return entry.grammar;
                }
                entry = entry.next;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Grammar removeGrammar(XMLGrammarDescription xMLGrammarDescription) {
        Entry[] entryArray = this.fGrammars;
        synchronized (this.fGrammars) {
            int n = this.hashCode(xMLGrammarDescription);
            int n2 = (n & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry entry = this.fGrammars[n2];
            Entry entry2 = null;
            while (entry != null) {
                if (entry.hash == n && this.equals(entry.desc, xMLGrammarDescription)) {
                    if (entry2 != null) {
                        entry2.next = entry.next;
                    } else {
                        this.fGrammars[n2] = entry.next;
                    }
                    Grammar grammar = entry.grammar;
                    entry.grammar = null;
                    --this.fGrammarCount;
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return grammar;
                }
                entry2 = entry;
                entry = entry.next;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsGrammar(XMLGrammarDescription xMLGrammarDescription) {
        Entry[] entryArray = this.fGrammars;
        synchronized (this.fGrammars) {
            int n = this.hashCode(xMLGrammarDescription);
            int n2 = (n & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry entry = this.fGrammars[n2];
            while (entry != null) {
                if (entry.hash == n && this.equals(entry.desc, xMLGrammarDescription)) {
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return true;
                }
                entry = entry.next;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return false;
        }
    }

    @Override
    public void lockPool() {
        this.fPoolIsLocked = true;
    }

    @Override
    public void unlockPool() {
        this.fPoolIsLocked = false;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.fGrammars.length; ++i) {
            if (this.fGrammars[i] == null) continue;
            this.fGrammars[i].clear();
            this.fGrammars[i] = null;
        }
        this.fGrammarCount = 0;
    }

    public boolean equals(XMLGrammarDescription xMLGrammarDescription, XMLGrammarDescription xMLGrammarDescription2) {
        return xMLGrammarDescription.equals(xMLGrammarDescription2);
    }

    public int hashCode(XMLGrammarDescription xMLGrammarDescription) {
        return xMLGrammarDescription.hashCode();
    }

    protected static final class Entry {
        public int hash;
        public XMLGrammarDescription desc;
        public Grammar grammar;
        public Entry next;

        protected Entry(int n, XMLGrammarDescription xMLGrammarDescription, Grammar grammar, Entry entry) {
            this.hash = n;
            this.desc = xMLGrammarDescription;
            this.grammar = grammar;
            this.next = entry;
        }

        protected void clear() {
            this.desc = null;
            this.grammar = null;
            if (this.next != null) {
                this.next.clear();
                this.next = null;
            }
        }
    }
}

