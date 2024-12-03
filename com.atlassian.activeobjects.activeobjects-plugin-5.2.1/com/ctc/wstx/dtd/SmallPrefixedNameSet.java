/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.PrefixedNameSet;
import com.ctc.wstx.util.PrefixedName;

public final class SmallPrefixedNameSet
extends PrefixedNameSet {
    final boolean mNsAware;
    final String[] mStrings;

    public SmallPrefixedNameSet(boolean nsAware, PrefixedName[] names) {
        this.mNsAware = nsAware;
        int len = names.length;
        if (len == 0) {
            throw new IllegalStateException("Trying to construct empty PrefixedNameSet");
        }
        this.mStrings = new String[nsAware ? len + len : len];
        int out = 0;
        for (int in = 0; in < len; ++in) {
            PrefixedName nk = names[in];
            if (nsAware) {
                this.mStrings[out++] = nk.getPrefix();
            }
            this.mStrings[out++] = nk.getLocalName();
        }
    }

    public boolean hasMultiple() {
        return this.mStrings.length > 1;
    }

    public boolean contains(PrefixedName name) {
        int len = this.mStrings.length;
        String ln = name.getLocalName();
        String[] strs = this.mStrings;
        if (this.mNsAware) {
            String prefix = name.getPrefix();
            if (strs[1] == ln && strs[0] == prefix) {
                return true;
            }
            for (int i = 2; i < len; i += 2) {
                if (strs[i + 1] != ln || strs[i] != prefix) continue;
                return true;
            }
        } else {
            if (strs[0] == ln) {
                return true;
            }
            for (int i = 1; i < len; ++i) {
                if (strs[i] != ln) continue;
                return true;
            }
        }
        return false;
    }

    public void appendNames(StringBuffer sb, String sep) {
        int i = 0;
        while (i < this.mStrings.length) {
            String prefix;
            if (i > 0) {
                sb.append(sep);
            }
            if (this.mNsAware && (prefix = this.mStrings[i++]) != null) {
                sb.append(prefix);
                sb.append(':');
            }
            sb.append(this.mStrings[i++]);
        }
    }
}

