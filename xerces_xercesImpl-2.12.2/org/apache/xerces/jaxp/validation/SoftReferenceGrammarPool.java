/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.grammars.XMLSchemaDescription;

final class SoftReferenceGrammarPool
implements XMLGrammarPool {
    protected static final int TABLE_SIZE = 11;
    protected static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
    protected Entry[] fGrammars = null;
    protected boolean fPoolIsLocked;
    protected int fGrammarCount = 0;
    protected final ReferenceQueue fReferenceQueue = new ReferenceQueue();

    public SoftReferenceGrammarPool() {
        this.fGrammars = new Entry[11];
        this.fPoolIsLocked = false;
    }

    public SoftReferenceGrammarPool(int n) {
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
            this.clean();
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return ZERO_LENGTH_GRAMMAR_ARRAY;
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
            this.clean();
            XMLGrammarDescription xMLGrammarDescription = grammar.getGrammarDescription();
            int n = this.hashCode(xMLGrammarDescription);
            int n2 = (n & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry entry = this.fGrammars[n2];
            while (entry != null) {
                if (entry.hash == n && this.equals(entry.desc, xMLGrammarDescription)) {
                    if (entry.grammar.get() == grammar) return;
                    entry.grammar = new SoftGrammarReference(entry, grammar, this.fReferenceQueue);
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return;
                }
                entry = entry.next;
            }
            this.fGrammars[n2] = entry = new Entry(n, n2, xMLGrammarDescription, grammar, this.fGrammars[n2], this.fReferenceQueue);
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
            this.clean();
            int n = this.hashCode(xMLGrammarDescription);
            int n2 = (n & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry entry = this.fGrammars[n2];
            while (entry != null) {
                Grammar grammar = (Grammar)entry.grammar.get();
                if (grammar == null) {
                    this.removeEntry(entry);
                } else if (entry.hash == n && this.equals(entry.desc, xMLGrammarDescription)) {
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return grammar;
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
            this.clean();
            int n = this.hashCode(xMLGrammarDescription);
            int n2 = (n & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry entry = this.fGrammars[n2];
            while (entry != null) {
                if (entry.hash == n && this.equals(entry.desc, xMLGrammarDescription)) {
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return this.removeEntry(entry);
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
    public boolean containsGrammar(XMLGrammarDescription xMLGrammarDescription) {
        Entry[] entryArray = this.fGrammars;
        synchronized (this.fGrammars) {
            this.clean();
            int n = this.hashCode(xMLGrammarDescription);
            int n2 = (n & Integer.MAX_VALUE) % this.fGrammars.length;
            Entry entry = this.fGrammars[n2];
            while (entry != null) {
                Grammar grammar = (Grammar)entry.grammar.get();
                if (grammar == null) {
                    this.removeEntry(entry);
                } else if (entry.hash == n && this.equals(entry.desc, xMLGrammarDescription)) {
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
        if (xMLGrammarDescription instanceof XMLSchemaDescription) {
            if (!(xMLGrammarDescription2 instanceof XMLSchemaDescription)) {
                return false;
            }
            XMLSchemaDescription xMLSchemaDescription = (XMLSchemaDescription)xMLGrammarDescription;
            XMLSchemaDescription xMLSchemaDescription2 = (XMLSchemaDescription)xMLGrammarDescription2;
            String string = xMLSchemaDescription.getTargetNamespace();
            if (string != null ? !string.equals(xMLSchemaDescription2.getTargetNamespace()) : xMLSchemaDescription2.getTargetNamespace() != null) {
                return false;
            }
            String string2 = xMLSchemaDescription.getExpandedSystemId();
            return !(string2 != null ? !string2.equals(xMLSchemaDescription2.getExpandedSystemId()) : xMLSchemaDescription2.getExpandedSystemId() != null);
        }
        return xMLGrammarDescription.equals(xMLGrammarDescription2);
    }

    public int hashCode(XMLGrammarDescription xMLGrammarDescription) {
        if (xMLGrammarDescription instanceof XMLSchemaDescription) {
            XMLSchemaDescription xMLSchemaDescription = (XMLSchemaDescription)xMLGrammarDescription;
            String string = xMLSchemaDescription.getTargetNamespace();
            String string2 = xMLSchemaDescription.getExpandedSystemId();
            int n = string != null ? string.hashCode() : 0;
            return n ^= string2 != null ? string2.hashCode() : 0;
        }
        return xMLGrammarDescription.hashCode();
    }

    private Grammar removeEntry(Entry entry) {
        if (entry.prev != null) {
            entry.prev.next = entry.next;
        } else {
            this.fGrammars[entry.bucket] = entry.next;
        }
        if (entry.next != null) {
            entry.next.prev = entry.prev;
        }
        --this.fGrammarCount;
        entry.grammar.entry = null;
        return (Grammar)entry.grammar.get();
    }

    private void clean() {
        Reference reference = this.fReferenceQueue.poll();
        while (reference != null) {
            Entry entry = ((SoftGrammarReference)reference).entry;
            if (entry != null) {
                this.removeEntry(entry);
            }
            reference = this.fReferenceQueue.poll();
        }
    }

    static final class SoftGrammarReference
    extends SoftReference {
        public Entry entry;

        protected SoftGrammarReference(Entry entry, Grammar grammar, ReferenceQueue referenceQueue) {
            super(grammar, referenceQueue);
            this.entry = entry;
        }
    }

    static final class Entry {
        public int hash;
        public int bucket;
        public Entry prev;
        public Entry next;
        public XMLGrammarDescription desc;
        public SoftGrammarReference grammar;

        protected Entry(int n, int n2, XMLGrammarDescription xMLGrammarDescription, Grammar grammar, Entry entry, ReferenceQueue referenceQueue) {
            this.hash = n;
            this.bucket = n2;
            this.prev = null;
            this.next = entry;
            if (entry != null) {
                entry.prev = this;
            }
            this.desc = xMLGrammarDescription;
            this.grammar = new SoftGrammarReference(this, grammar, referenceQueue);
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

