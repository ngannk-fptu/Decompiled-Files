/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.lookup;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.text.lookup.AbstractStringLookup;

final class LocalHostStringLookup
extends AbstractStringLookup {
    static final LocalHostStringLookup INSTANCE = new LocalHostStringLookup();

    private LocalHostStringLookup() {
    }

    @Override
    public String lookup(String key) {
        if (key == null) {
            return null;
        }
        try {
            switch (key) {
                case "name": {
                    return InetAddress.getLocalHost().getHostName();
                }
                case "canonical-name": {
                    return InetAddress.getLocalHost().getCanonicalHostName();
                }
                case "address": {
                    return InetAddress.getLocalHost().getHostAddress();
                }
            }
            throw new IllegalArgumentException(key);
        }
        catch (UnknownHostException e) {
            return null;
        }
    }
}

