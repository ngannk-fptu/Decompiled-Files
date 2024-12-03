/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.context;

import java.util.Locale;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;

public interface MessageSource {
    @Nullable
    public String getMessage(String var1, @Nullable Object[] var2, @Nullable String var3, Locale var4);

    public String getMessage(String var1, @Nullable Object[] var2, Locale var3) throws NoSuchMessageException;

    public String getMessage(MessageSourceResolvable var1, Locale var2) throws NoSuchMessageException;
}

