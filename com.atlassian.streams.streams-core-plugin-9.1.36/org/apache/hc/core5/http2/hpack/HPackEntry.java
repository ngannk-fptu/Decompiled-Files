/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import org.apache.hc.core5.http2.hpack.HPackHeader;

interface HPackEntry {
    public int getIndex();

    public HPackHeader getHeader();
}

