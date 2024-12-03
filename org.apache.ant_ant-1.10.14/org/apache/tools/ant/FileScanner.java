/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.File;

public interface FileScanner {
    public void addDefaultExcludes();

    public File getBasedir();

    public String[] getExcludedDirectories();

    public String[] getExcludedFiles();

    public String[] getIncludedDirectories();

    public String[] getIncludedFiles();

    public String[] getNotIncludedDirectories();

    public String[] getNotIncludedFiles();

    public void scan() throws IllegalStateException;

    public void setBasedir(String var1);

    public void setBasedir(File var1);

    public void setExcludes(String[] var1);

    public void setIncludes(String[] var1);

    public void setCaseSensitive(boolean var1);
}

