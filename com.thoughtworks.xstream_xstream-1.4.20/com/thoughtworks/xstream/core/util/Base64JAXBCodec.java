/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.DatatypeConverter
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.core.StringCodec;
import javax.xml.bind.DatatypeConverter;

public class Base64JAXBCodec
implements StringCodec {
    public byte[] decode(String base64) {
        return DatatypeConverter.parseBase64Binary((String)base64);
    }

    public String encode(byte[] data) {
        return DatatypeConverter.printBase64Binary((byte[])data);
    }
}

