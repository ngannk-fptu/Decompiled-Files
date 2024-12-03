/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.apache.axis.types.NCName;
import org.apache.axis.types.NMToken;

public class NMTokens
extends NCName {
    private NMToken[] tokens;

    public NMTokens() {
    }

    public NMTokens(String stValue) throws IllegalArgumentException {
        this.setValue(stValue);
    }

    public void setValue(String stValue) {
        StringTokenizer tokenizer = new StringTokenizer(stValue);
        int count = tokenizer.countTokens();
        this.tokens = new NMToken[count];
        for (int i = 0; i < count; ++i) {
            this.tokens[i] = new NMToken(tokenizer.nextToken());
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.tokens.length; ++i) {
            NMToken token = this.tokens[i];
            if (i > 0) {
                buf.append(" ");
            }
            buf.append(token.toString());
        }
        return buf.toString();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof NMTokens) {
            NMTokens that = (NMTokens)object;
            if (this.tokens.length == that.tokens.length) {
                HashSet<NMToken> ourSet = new HashSet<NMToken>(Arrays.asList(this.tokens));
                HashSet<NMToken> theirSet = new HashSet<NMToken>(Arrays.asList(that.tokens));
                return ((Object)ourSet).equals(theirSet);
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < this.tokens.length; ++i) {
            hash += this.tokens[i].hashCode();
        }
        return hash;
    }
}

