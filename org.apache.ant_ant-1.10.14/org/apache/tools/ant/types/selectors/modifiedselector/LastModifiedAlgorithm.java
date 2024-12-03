/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;
import org.apache.tools.ant.types.selectors.modifiedselector.Algorithm;

public class LastModifiedAlgorithm
implements Algorithm {
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getValue(File file) {
        long lastModified = file.lastModified();
        if (lastModified == 0L) {
            return null;
        }
        return Long.toString(lastModified);
    }
}

