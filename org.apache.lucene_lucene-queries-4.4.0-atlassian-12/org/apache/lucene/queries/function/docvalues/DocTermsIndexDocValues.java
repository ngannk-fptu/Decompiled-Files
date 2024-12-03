/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.SortedDocValues
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.UnicodeUtil
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueStr
 */
package org.apache.lucene.queries.function.docvalues;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueStr;

public abstract class DocTermsIndexDocValues
extends FunctionValues {
    protected final SortedDocValues termsIndex;
    protected final ValueSource vs;
    protected final MutableValueStr val = new MutableValueStr();
    protected final BytesRef spare = new BytesRef();
    protected final CharsRef spareChars = new CharsRef();

    public DocTermsIndexDocValues(ValueSource vs, AtomicReaderContext context, String field) throws IOException {
        try {
            this.termsIndex = FieldCache.DEFAULT.getTermsIndex(context.reader(), field);
        }
        catch (RuntimeException e) {
            throw new DocTermsIndexException(field, e);
        }
        this.vs = vs;
    }

    protected abstract String toTerm(String var1);

    @Override
    public boolean exists(int doc) {
        return this.ordVal(doc) >= 0;
    }

    @Override
    public int ordVal(int doc) {
        return this.termsIndex.getOrd(doc);
    }

    @Override
    public int numOrd() {
        return this.termsIndex.getValueCount();
    }

    @Override
    public boolean bytesVal(int doc, BytesRef target) {
        this.termsIndex.get(doc, target);
        return target.length > 0;
    }

    @Override
    public String strVal(int doc) {
        this.termsIndex.get(doc, this.spare);
        if (this.spare.length == 0) {
            return null;
        }
        UnicodeUtil.UTF8toUTF16((BytesRef)this.spare, (CharsRef)this.spareChars);
        return this.spareChars.toString();
    }

    @Override
    public boolean boolVal(int doc) {
        return this.exists(doc);
    }

    @Override
    public abstract Object objectVal(int var1);

    @Override
    public ValueSourceScorer getRangeScorer(IndexReader reader, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
        lowerVal = lowerVal == null ? null : this.toTerm(lowerVal);
        upperVal = upperVal == null ? null : this.toTerm(upperVal);
        int lower = Integer.MIN_VALUE;
        if (lowerVal != null) {
            lower = this.termsIndex.lookupTerm(new BytesRef((CharSequence)lowerVal));
            if (lower < 0) {
                lower = -lower - 1;
            } else if (!includeLower) {
                ++lower;
            }
        }
        int upper = Integer.MAX_VALUE;
        if (upperVal != null) {
            upper = this.termsIndex.lookupTerm(new BytesRef((CharSequence)upperVal));
            if (upper < 0) {
                upper = -upper - 2;
            } else if (!includeUpper) {
                --upper;
            }
        }
        final int ll = lower;
        final int uu = upper;
        return new ValueSourceScorer(reader, this){

            @Override
            public boolean matchesValue(int doc) {
                int ord = DocTermsIndexDocValues.this.termsIndex.getOrd(doc);
                return ord >= ll && ord <= uu;
            }
        };
    }

    @Override
    public String toString(int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
    }

    @Override
    public FunctionValues.ValueFiller getValueFiller() {
        return new FunctionValues.ValueFiller(){
            private final MutableValueStr mval = new MutableValueStr();

            @Override
            public MutableValue getValue() {
                return this.mval;
            }

            @Override
            public void fillValue(int doc) {
                DocTermsIndexDocValues.this.termsIndex.get(doc, this.mval.value);
                this.mval.exists = this.mval.value.bytes != SortedDocValues.MISSING;
            }
        };
    }

    public static final class DocTermsIndexException
    extends RuntimeException {
        public DocTermsIndexException(String fieldName, RuntimeException cause) {
            super("Can't initialize DocTermsIndex to generate (function) FunctionValues for field: " + fieldName, cause);
        }
    }
}

