/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.ObjectPool;

public class StringBufferPool {
    private static ObjectPool m_stringBufPool = new ObjectPool(FastStringBuffer.class);

    public static synchronized FastStringBuffer get() {
        return (FastStringBuffer)m_stringBufPool.getInstance();
    }

    public static synchronized void free(FastStringBuffer sb) {
        sb.setLength(0);
        m_stringBufPool.freeInstance(sb);
    }
}

