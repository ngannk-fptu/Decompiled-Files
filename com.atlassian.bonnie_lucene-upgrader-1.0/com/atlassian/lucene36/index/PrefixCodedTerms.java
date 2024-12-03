/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.RAMFile;
import com.atlassian.lucene36.store.RAMInputStream;
import com.atlassian.lucene36.store.RAMOutputStream;
import com.atlassian.lucene36.util.BytesRef;
import com.atlassian.lucene36.util.StringHelper;
import java.io.IOException;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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
        private BytesRef lastBytes = new BytesRef();
        private BytesRef scratch = new BytesRef();

        public void add(Term term) {
            assert (this.lastTerm.equals(new Term("")) || term.compareTo(this.lastTerm) > 0);
            this.scratch.copyChars(term.text);
            try {
                int prefix = this.sharedPrefix(this.lastBytes, this.scratch);
                int suffix = this.scratch.length - prefix;
                if (term.field.equals(this.lastTerm.field)) {
                    this.output.writeVInt(prefix << 1);
                } else {
                    this.output.writeVInt(prefix << 1 | 1);
                    this.output.writeString(term.field);
                }
                this.output.writeVInt(suffix);
                this.output.writeBytes(this.scratch.bytes, this.scratch.offset + prefix, suffix);
                this.lastBytes.copyBytes(this.scratch);
                this.lastTerm.text = term.text;
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class PrefixCodedTermsIterator
    implements Iterator<Term> {
        final IndexInput input;
        String field = "";
        BytesRef bytes = new BytesRef();
        Term term = new Term(this.field, "");

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
                    this.field = StringHelper.intern(this.input.readString());
                }
                int prefix = code >>> 1;
                int suffix = this.input.readVInt();
                this.bytes.grow(prefix + suffix);
                this.input.readBytes(this.bytes.bytes, prefix, suffix);
                this.bytes.length = prefix + suffix;
                this.term.set(this.field, this.bytes.utf8ToString());
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

