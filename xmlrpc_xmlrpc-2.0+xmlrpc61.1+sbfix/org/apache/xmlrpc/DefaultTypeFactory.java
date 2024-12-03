/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.DecoderException
 *  org.apache.commons.codec.binary.Base64
 */
package org.apache.xmlrpc;

import java.text.ParseException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.xmlrpc.TypeFactory;
import org.apache.xmlrpc.util.DateTool;

public class DefaultTypeFactory
implements TypeFactory {
    private static DateTool dateTool = new DateTool();
    private static final Base64 base64Codec = new Base64();

    public Object createInteger(String cdata) {
        return new Integer(cdata.trim());
    }

    public Object createBoolean(String cdata) {
        return "1".equals(cdata.trim()) ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object createDouble(String cdata) {
        return new Double(cdata.trim());
    }

    public Object createDate(String cdata) {
        try {
            return dateTool.parse(cdata.trim());
        }
        catch (ParseException p) {
            throw new RuntimeException(p.getMessage());
        }
    }

    public Object createBase64(String cdata) {
        try {
            return base64Codec.decode((Object)cdata.getBytes());
        }
        catch (DecoderException e) {
            return new byte[0];
        }
    }

    public Object createString(String cdata) {
        return cdata;
    }
}

