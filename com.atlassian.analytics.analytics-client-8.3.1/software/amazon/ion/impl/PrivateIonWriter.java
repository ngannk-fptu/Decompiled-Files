/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonWriter;

@Deprecated
public interface PrivateIonWriter
extends IonWriter {
    public IonCatalog getCatalog();

    public boolean isFieldNameSet();

    public int getDepth();

    public void writeIonVersionMarker() throws IOException;

    public boolean isStreamCopyOptimized();
}

