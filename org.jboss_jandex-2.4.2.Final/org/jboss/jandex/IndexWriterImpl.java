/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.IOException;
import org.jboss.jandex.Index;

abstract class IndexWriterImpl {
    IndexWriterImpl() {
    }

    abstract int write(Index var1, int var2) throws IOException;
}

