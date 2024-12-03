/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.store.DataInput
 *  org.apache.lucene.store.InputStreamDataInput
 *  org.apache.lucene.util.IOUtils
 *  org.apache.lucene.util.fst.FST
 *  org.apache.lucene.util.fst.Outputs
 *  org.apache.lucene.util.fst.PositiveIntOutputs
 */
package org.apache.lucene.analysis.ja.dict;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import org.apache.lucene.analysis.ja.dict.BinaryDictionary;
import org.apache.lucene.analysis.ja.dict.TokenInfoFST;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.InputStreamDataInput;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.PositiveIntOutputs;

public final class TokenInfoDictionary
extends BinaryDictionary {
    public static final String FST_FILENAME_SUFFIX = "$fst.dat";
    private final TokenInfoFST fst;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private TokenInfoDictionary() throws IOException {
        IOException priorE = null;
        InputStream is = null;
        FST fst = null;
        try {
            is = this.getResource(FST_FILENAME_SUFFIX);
            is = new BufferedInputStream(is);
            fst = new FST((DataInput)new InputStreamDataInput(is), (Outputs)PositiveIntOutputs.getSingleton());
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
        this.fst = new TokenInfoFST((FST<Long>)fst, true);
    }

    public TokenInfoFST getFST() {
        return this.fst;
    }

    public static TokenInfoDictionary getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final TokenInfoDictionary INSTANCE;

        private SingletonHolder() {
        }

        static {
            try {
                INSTANCE = new TokenInfoDictionary();
            }
            catch (IOException ioe) {
                throw new RuntimeException("Cannot load TokenInfoDictionary.", ioe);
            }
        }
    }
}

