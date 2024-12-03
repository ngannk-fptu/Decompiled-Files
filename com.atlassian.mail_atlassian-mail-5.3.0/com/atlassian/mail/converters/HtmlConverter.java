/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.mail.converters;

import java.io.IOException;
import javax.annotation.Nonnull;

public interface HtmlConverter {
    public String convert(@Nonnull String var1) throws IOException;
}

