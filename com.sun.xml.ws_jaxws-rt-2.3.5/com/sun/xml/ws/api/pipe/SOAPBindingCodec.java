/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;

public interface SOAPBindingCodec
extends Codec {
    public StreamSOAPCodec getXMLCodec();
}

