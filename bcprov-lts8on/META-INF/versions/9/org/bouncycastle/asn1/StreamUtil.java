/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.LimitedInputStream;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class StreamUtil {
    StreamUtil() {
    }

    static int findLimit(InputStream in) {
        long maxMemory;
        if (in instanceof LimitedInputStream) {
            return ((LimitedInputStream)in).getLimit();
        }
        if (in instanceof ASN1InputStream) {
            return ((ASN1InputStream)in).getLimit();
        }
        if (in instanceof ByteArrayInputStream) {
            return ((ByteArrayInputStream)in).available();
        }
        if (in instanceof FileInputStream) {
            try {
                long size;
                FileChannel channel = ((FileInputStream)in).getChannel();
                long l = size = channel != null ? channel.size() : Integer.MAX_VALUE;
                if (size < Integer.MAX_VALUE) {
                    return (int)size;
                }
            }
            catch (IOException channel) {
                // empty catch block
            }
        }
        if ((maxMemory = Runtime.getRuntime().maxMemory()) > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)maxMemory;
    }
}

