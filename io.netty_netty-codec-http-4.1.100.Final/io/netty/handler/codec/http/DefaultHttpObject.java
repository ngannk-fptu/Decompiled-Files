/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.DecoderResult
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttpObject
implements HttpObject {
    private static final int HASH_CODE_PRIME = 31;
    private DecoderResult decoderResult = DecoderResult.SUCCESS;

    protected DefaultHttpObject() {
    }

    public DecoderResult decoderResult() {
        return this.decoderResult;
    }

    @Override
    @Deprecated
    public DecoderResult getDecoderResult() {
        return this.decoderResult();
    }

    public void setDecoderResult(DecoderResult decoderResult) {
        this.decoderResult = (DecoderResult)ObjectUtil.checkNotNull((Object)decoderResult, (String)"decoderResult");
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + this.decoderResult.hashCode();
        return result;
    }

    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttpObject)) {
            return false;
        }
        DefaultHttpObject other = (DefaultHttpObject)o;
        return this.decoderResult().equals(other.decoderResult());
    }
}

