/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.io.IOByteArrayMap;
import com.mchange.util.ByteArrayBinding;
import com.mchange.util.ByteArrayComparator;
import java.io.IOException;

public interface IOSequentialByteArrayMap
extends IOByteArrayMap {
    public ByteArrayComparator getByteArrayComparator();

    public Cursor getCursor();

    public static interface Cursor {
        public ByteArrayBinding getFirst() throws IOException;

        public ByteArrayBinding getNext() throws IOException;

        public ByteArrayBinding getPrevious() throws IOException;

        public ByteArrayBinding getLast() throws IOException;

        public ByteArrayBinding getCurrent() throws IOException;

        public ByteArrayBinding find(byte[] var1) throws IOException;

        public ByteArrayBinding findGreaterThanOrEqual(byte[] var1) throws IOException;

        public ByteArrayBinding findLessThanOrEqual(byte[] var1) throws IOException;

        public void deleteCurrent() throws IOException;

        public void replaceCurrent(byte[] var1) throws IOException;
    }
}

