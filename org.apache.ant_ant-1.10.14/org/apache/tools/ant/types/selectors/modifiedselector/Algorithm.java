/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;

public interface Algorithm {
    public boolean isValid();

    public String getValue(File var1);
}

