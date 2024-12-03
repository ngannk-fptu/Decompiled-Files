/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.convert.TypeMapper
 */
package org.springframework.vault.repository.convert;

import java.util.Map;
import org.springframework.data.convert.TypeMapper;

public interface VaultTypeMapper
extends TypeMapper<Map<String, Object>> {
    public boolean isTypeKey(String var1);
}

