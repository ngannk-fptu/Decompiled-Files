/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.IOException;
import org.jboss.jandex.Index;

abstract class IndexReaderImpl {
    IndexReaderImpl() {
    }

    abstract Index read(int var1) throws IOException;

    abstract int toDataVersion(int var1);
}

