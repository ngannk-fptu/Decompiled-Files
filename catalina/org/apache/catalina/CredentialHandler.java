/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

public interface CredentialHandler {
    public boolean matches(String var1, String var2);

    public String mutate(String var1);
}

