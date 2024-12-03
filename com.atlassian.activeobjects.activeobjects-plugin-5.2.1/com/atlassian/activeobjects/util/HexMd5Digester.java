/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.activeobjects.util;

import com.atlassian.activeobjects.util.Digester;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public final class HexMd5Digester
implements Digester {
    @Override
    public String digest(String s) {
        return DigestUtils.md5Hex((String)s);
    }

    @Override
    public String digest(String s, int n) {
        return StringUtils.right((String)this.digest(s), (int)n);
    }
}

