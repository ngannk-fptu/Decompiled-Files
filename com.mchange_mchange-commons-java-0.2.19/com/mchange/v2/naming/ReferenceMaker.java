/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.naming;

import javax.naming.NamingException;
import javax.naming.Reference;

public interface ReferenceMaker {
    public Reference createReference(Object var1) throws NamingException;
}

