/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.codecs.TermsConsumer;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.Terms;

public abstract class FieldsConsumer
implements Closeable {
    protected FieldsConsumer() {
    }

    public abstract TermsConsumer addField(FieldInfo var1) throws IOException;

    @Override
    public abstract void close() throws IOException;

    public void merge(MergeState mergeState, Fields fields) throws IOException {
        for (String field : fields) {
            FieldInfo info = mergeState.fieldInfos.fieldInfo(field);
            assert (info != null) : "FieldInfo for field is null: " + field;
            Terms terms = fields.terms(field);
            if (terms == null) continue;
            TermsConsumer termsConsumer = this.addField(info);
            termsConsumer.merge(mergeState, info.getIndexOptions(), terms.iterator(null));
        }
    }
}

