/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.message.source;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.message.AttachmentSetImpl;
import com.sun.xml.ws.message.stream.PayloadStreamReaderMessage;
import com.sun.xml.ws.streaming.SourceReaderFactory;
import javax.xml.transform.Source;

public class PayloadSourceMessage
extends PayloadStreamReaderMessage {
    public PayloadSourceMessage(@Nullable MessageHeaders headers, @NotNull Source payload, @NotNull AttachmentSet attSet, @NotNull SOAPVersion soapVersion) {
        super(headers, SourceReaderFactory.createSourceReader(payload, true), attSet, soapVersion);
    }

    public PayloadSourceMessage(Source s, SOAPVersion soapVer) {
        this(null, s, (AttachmentSet)new AttachmentSetImpl(), soapVer);
    }
}

