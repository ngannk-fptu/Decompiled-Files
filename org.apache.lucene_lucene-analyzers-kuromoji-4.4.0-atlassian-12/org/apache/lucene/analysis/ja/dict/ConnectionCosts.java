/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.codecs.CodecUtil
 *  org.apache.lucene.store.DataInput
 *  org.apache.lucene.store.InputStreamDataInput
 *  org.apache.lucene.util.IOUtils
 */
package org.apache.lucene.analysis.ja.dict;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import org.apache.lucene.analysis.ja.dict.BinaryDictionary;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.InputStreamDataInput;
import org.apache.lucene.util.IOUtils;

public final class ConnectionCosts {
    public static final String FILENAME_SUFFIX = ".dat";
    public static final String HEADER = "kuromoji_cc";
    public static final int VERSION = 1;
    private final short[][] costs;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ConnectionCosts() throws IOException {
        IOException priorE = null;
        InputStream is = null;
        short[][] costs = null;
        try {
            is = BinaryDictionary.getClassResource(this.getClass(), FILENAME_SUFFIX);
            is = new BufferedInputStream(is);
            InputStreamDataInput in = new InputStreamDataInput(is);
            CodecUtil.checkHeader((DataInput)in, (String)HEADER, (int)1, (int)1);
            int forwardSize = in.readVInt();
            int backwardSize = in.readVInt();
            costs = new short[backwardSize][forwardSize];
            int accum = 0;
            for (int j = 0; j < costs.length; ++j) {
                short[] a = costs[j];
                for (int i = 0; i < a.length; ++i) {
                    int raw = in.readVInt();
                    a[i] = (short)(accum += raw >>> 1 ^ -(raw & 1));
                }
            }
        }
        catch (IOException ioe) {
            try {
                priorE = ioe;
            }
            catch (Throwable throwable) {
                IOUtils.closeWhileHandlingException((Exception)priorE, (Closeable[])new Closeable[]{is});
                throw throwable;
            }
            IOUtils.closeWhileHandlingException((Exception)priorE, (Closeable[])new Closeable[]{is});
        }
        IOUtils.closeWhileHandlingException((Exception)priorE, (Closeable[])new Closeable[]{is});
        this.costs = costs;
    }

    public int get(int forwardId, int backwardId) {
        return this.costs[backwardId][forwardId];
    }

    public static ConnectionCosts getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final ConnectionCosts INSTANCE;

        private SingletonHolder() {
        }

        static {
            try {
                INSTANCE = new ConnectionCosts();
            }
            catch (IOException ioe) {
                throw new RuntimeException("Cannot load ConnectionCosts.", ioe);
            }
        }
    }
}

