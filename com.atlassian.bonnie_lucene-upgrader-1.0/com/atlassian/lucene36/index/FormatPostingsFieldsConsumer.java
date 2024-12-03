/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FormatPostingsTermsConsumer;
import java.io.IOException;

abstract class FormatPostingsFieldsConsumer {
    FormatPostingsFieldsConsumer() {
    }

    abstract FormatPostingsTermsConsumer addField(FieldInfo var1) throws IOException;

    abstract void finish() throws IOException;
}

