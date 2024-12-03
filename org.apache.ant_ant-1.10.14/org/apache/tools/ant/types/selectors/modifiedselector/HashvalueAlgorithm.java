/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import org.apache.tools.ant.types.selectors.modifiedselector.Algorithm;
import org.apache.tools.ant.util.FileUtils;

public class HashvalueAlgorithm
implements Algorithm {
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getValue(File file) {
        String string;
        if (!file.canRead()) {
            return null;
        }
        FileReader r = new FileReader(file);
        try {
            int hash = FileUtils.readFully(r).hashCode();
            string = Integer.toString(hash);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((Reader)r).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                return null;
            }
        }
        ((Reader)r).close();
        return string;
    }

    public String toString() {
        return "HashvalueAlgorithm";
    }
}

