/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.confluence.servlet.simpledisplay.PathConverter;
import java.util.List;

public interface PathConverterManager {
    public List<PathConverter> getPathConverters();

    public void addPathConverter(int var1, PathConverter var2);

    public void removePathConverter(PathConverter var1);
}

