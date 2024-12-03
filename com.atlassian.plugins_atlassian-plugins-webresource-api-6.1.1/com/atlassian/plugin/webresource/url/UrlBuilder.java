/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource.url;

import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import java.util.Collection;
import javax.annotation.Nullable;

public interface UrlBuilder {
    public void addToHash(String var1, @Nullable Object var2);

    public void addToQueryString(String var1, String var2);

    public void addPrebakeError(PrebakeError var1);

    public void addAllPrebakeErrors(Collection<PrebakeError> var1);
}

