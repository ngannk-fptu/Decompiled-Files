/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMXMLStreamReader;

public interface OMXMLStreamReaderEx
extends OMXMLStreamReader {
    public void enableDataSourceEvents(boolean var1);

    public OMDataSource getDataSource();

    public boolean isClosed();

    public void releaseParserOnClose(boolean var1);
}

