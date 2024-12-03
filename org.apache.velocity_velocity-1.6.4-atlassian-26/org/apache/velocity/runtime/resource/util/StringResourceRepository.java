/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.resource.util;

import org.apache.velocity.runtime.resource.util.StringResource;

public interface StringResourceRepository {
    public StringResource getStringResource(String var1);

    public void putStringResource(String var1, String var2);

    public void putStringResource(String var1, String var2, String var3);

    public void removeStringResource(String var1);

    public void setEncoding(String var1);

    public String getEncoding();
}

