/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.webservices.impl.internalspi.encoding;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import java.io.IOException;
import java.io.InputStream;

public interface StreamDecoder {
    public Message decode(InputStream var1, String var2, AttachmentSet var3, SOAPVersion var4) throws IOException;
}

