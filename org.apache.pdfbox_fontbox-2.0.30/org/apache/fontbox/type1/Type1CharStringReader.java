/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.type1;

import java.io.IOException;
import org.apache.fontbox.cff.Type1CharString;

public interface Type1CharStringReader {
    public Type1CharString getType1CharString(String var1) throws IOException;
}

