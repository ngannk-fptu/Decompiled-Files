/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.FieldInfo;

public abstract class StoredFieldVisitor {
    protected StoredFieldVisitor() {
    }

    public void binaryField(FieldInfo fieldInfo, byte[] value) throws IOException {
    }

    public void stringField(FieldInfo fieldInfo, String value) throws IOException {
    }

    public void intField(FieldInfo fieldInfo, int value) throws IOException {
    }

    public void longField(FieldInfo fieldInfo, long value) throws IOException {
    }

    public void floatField(FieldInfo fieldInfo, float value) throws IOException {
    }

    public void doubleField(FieldInfo fieldInfo, double value) throws IOException {
    }

    public abstract Status needsField(FieldInfo var1) throws IOException;

    public static enum Status {
        YES,
        NO,
        STOP;

    }
}

