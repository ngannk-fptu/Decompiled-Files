/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.streaming;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.util.xml.XMLStreamReaderFilter;
import java.io.Closeable;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public class TidyXMLStreamReader
extends XMLStreamReaderFilter {
    private final Closeable closeableSource;

    public TidyXMLStreamReader(@NotNull XMLStreamReader reader, @Nullable Closeable closeableSource) {
        super(reader);
        this.closeableSource = closeableSource;
    }

    @Override
    public void close() throws XMLStreamException {
        super.close();
        try {
            if (this.closeableSource != null) {
                this.closeableSource.close();
            }
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
    }
}

