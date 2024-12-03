/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.auth;

import org.apache.hc.client5.http.impl.auth.NTLMEngineException;

public interface NTLMEngine {
    public String generateType1Msg(String var1, String var2) throws NTLMEngineException;

    public String generateType3Msg(String var1, char[] var2, String var3, String var4, String var5) throws NTLMEngineException;
}

