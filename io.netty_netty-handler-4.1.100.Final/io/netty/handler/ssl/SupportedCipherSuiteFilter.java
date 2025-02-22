/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.EmptyArrays
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SupportedCipherSuiteFilter
implements CipherSuiteFilter {
    public static final SupportedCipherSuiteFilter INSTANCE = new SupportedCipherSuiteFilter();

    private SupportedCipherSuiteFilter() {
    }

    @Override
    public String[] filterCipherSuites(Iterable<String> ciphers, List<String> defaultCiphers, Set<String> supportedCiphers) {
        ArrayList<String> newCiphers;
        ObjectUtil.checkNotNull(defaultCiphers, (String)"defaultCiphers");
        ObjectUtil.checkNotNull(supportedCiphers, (String)"supportedCiphers");
        if (ciphers == null) {
            newCiphers = new ArrayList<String>(defaultCiphers.size());
            ciphers = defaultCiphers;
        } else {
            newCiphers = new ArrayList(supportedCiphers.size());
        }
        for (String c : ciphers) {
            if (c == null) break;
            if (!supportedCiphers.contains(c)) continue;
            newCiphers.add(c);
        }
        return newCiphers.toArray(EmptyArrays.EMPTY_STRINGS);
    }
}

