/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import java.io.IOException;
import org.springframework.core.io.Resource;

public interface FontDao {
    public void saveFont(String var1, Resource var2) throws IOException;

    public void saveFont(String var1, Resource var2, boolean var3) throws IOException;

    public Resource getFont(String var1) throws IOException;

    public void removeFont(String var1) throws IOException;
}

