/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import org.codehaus.groovy.ast.ClassNode;

public interface SignatureCodec {
    public String encode(ClassNode var1);

    public ClassNode decode(String var1);
}

