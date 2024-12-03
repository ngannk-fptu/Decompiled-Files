/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.RAMFile;
import org.apache.lucene.store.RAMInputStream;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.util.BytesRef;

class PrefixCodedTerms
implements Iterable<Term> {
    final RAMFile buffer;

    private PrefixCodedTerms(RAMFile buffer) {
        this.buffer = buffer;
    }

    public long getSizeInBytes() {
        return this.buffer.getSizeInBytes();
    }

    @Override
    public Iterator<Term> iterator() {
        return new PrefixCodedTermsIterator();
    }

    public static class Builder {
        private RAMFile buffer = new RAMFile();
        private RAMOutputStream output = new RAMOutputStream(this.buffer);
        private Term lastTerm = new Term("");

        public void add(Term term) {
            assert (this.lastTerm.equals(new Term("")) || term.compareTo(this.lastTerm) > 0);
            try {
                int prefix = this.sharedPrefix(this.lastTerm.bytes, term.bytes);
                int suffix = term.bytes.length - prefix;
                if (term.field.equals(this.lastTerm.field)) {
                    this.output.writeVInt(prefix << 1);
                } else {
                    this.output.writeVInt(prefix << 1 | 1);
                    this.output.writeString(term.field);
                }
                this.output.writeVInt(suffix);
                this.output.writeBytes(term.bytes.bytes, term.bytes.offset + prefix, suffix);
                this.lastTerm.bytes.copyBytes(term.bytes);
                this.lastTerm.field = term.field;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public PrefixCodedTerms finish() {
            try {
                this.output.close();
                return new PrefixCodedTerms(this.buffer);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private int sharedPrefix(BytesRef term1, BytesRef term2) {
            int pos1 = 0;
            int pos1End = pos1 + Math.min(term1.length, term2.length);
            int pos2 = 0;
            while (pos1 < pos1End) {
                if (term1.bytes[term1.offset + pos1] != term2.bytes[term2.offset + pos2]) {
                    return pos1;
                }
                ++pos1;
                ++pos2;
            }
            return pos1;
        }
    }

    class PrefixCodedTermsIterator
    implements Iterator<Term> {
        final IndexInput input;
        String field = "";
        BytesRef bytes = new BytesRef();
        Term term = new Term(this.field, this.bytes);

        PrefixCodedTermsIterator() {
            try {
                this.input = new RAMInputStream("PrefixCodedTermsIterator", PrefixCodedTerms.this.buffer);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean hasNext() {
            return this.input.getFilePointer() < this.input.length();
        }

        @Override
        public Term next() {
            assert (this.hasNext());
            try {
                int code = this.input.readVInt();
                if ((code & 1) != 0) {
                    this.field = this.input.readString();
                }
                int prefix = code >>> 1;
                int suffix = this.input.readVInt();
                this.bytes.grow(prefix + suffix);
                this.input.readBytes(this.bytes.bytes, prefix, suffix);
                this.bytes.length = prefix + suffix;
                this.term.set(this.field, this.bytes);
                return this.term;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

