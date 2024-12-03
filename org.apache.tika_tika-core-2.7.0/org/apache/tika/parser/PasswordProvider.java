/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import org.apache.tika.metadata.Metadata;

public interface PasswordProvider {
    public String getPassword(Metadata var1);
}

