/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import org.apache.tools.ant.types.selectors.FileSelector;

public interface SelectorScanner {
    public void setSelectors(FileSelector[] var1);

    public String[] getDeselectedDirectories();

    public String[] getDeselectedFiles();
}

