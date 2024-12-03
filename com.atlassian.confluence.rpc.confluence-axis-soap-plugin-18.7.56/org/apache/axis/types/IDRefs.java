/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.apache.axis.types.IDRef;
import org.apache.axis.types.NCName;

public class IDRefs
extends NCName {
    private IDRef[] idrefs;

    public IDRefs() {
    }

    public IDRefs(String stValue) throws IllegalArgumentException {
        this.setValue(stValue);
    }

    public void setValue(String stValue) {
        StringTokenizer tokenizer = new StringTokenizer(stValue);
        int count = tokenizer.countTokens();
        this.idrefs = new IDRef[count];
        for (int i = 0; i < count; ++i) {
            this.idrefs[i] = new IDRef(tokenizer.nextToken());
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.idrefs.length; ++i) {
            IDRef ref = this.idrefs[i];
            if (i > 0) {
                buf.append(" ");
            }
            buf.append(ref.toString());
        }
        return buf.toString();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof IDRefs) {
            IDRefs that = (IDRefs)object;
            if (this.idrefs.length == that.idrefs.length) {
                HashSet<IDRef> ourSet = new HashSet<IDRef>(Arrays.asList(this.idrefs));
                HashSet<IDRef> theirSet = new HashSet<IDRef>(Arrays.asList(that.idrefs));
                return ((Object)ourSet).equals(theirSet);
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < this.idrefs.length; ++i) {
            hash += this.idrefs[i].hashCode();
        }
        return hash;
    }
}

