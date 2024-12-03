/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;
import java.util.List;

public final class UnsupportedMediaException
extends JAXWSExceptionBase {
    public UnsupportedMediaException(@NotNull String contentType, List<String> expectedContentTypes) {
        super(ServerMessages.localizableUNSUPPORTED_CONTENT_TYPE(contentType, expectedContentTypes));
    }

    public UnsupportedMediaException() {
        super(ServerMessages.localizableNO_CONTENT_TYPE());
    }

    public UnsupportedMediaException(String charset) {
        super(ServerMessages.localizableUNSUPPORTED_CHARSET(charset));
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.server";
    }
}

