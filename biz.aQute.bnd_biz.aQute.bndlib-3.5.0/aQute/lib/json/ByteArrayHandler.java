/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.base64.Base64;
import aQute.lib.hex.Hex;
import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import aQute.lib.json.StringHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public class ByteArrayHandler
extends Handler {
    Pattern ENCODING = Pattern.compile("((:?[\\dA-Za-z][\\dA-Za-z])*)|((:?ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/)+={1,3})");

    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
        StringHandler.string(app, Hex.toHexString((byte[])object));
    }

    @Override
    public Object decodeArray(Decoder r) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArrayList<Object> list = new ArrayList<Object>();
        r.codec.parseArray((Collection<Object>)list, (Type)((Object)Byte.class), r);
        for (Object b : list) {
            out.write(((Byte)b).byteValue());
        }
        return out.toByteArray();
    }

    @Override
    public Object decode(Decoder dec, String s) throws Exception {
        boolean hex = true;
        StringBuilder sb = new StringBuilder(s);
        block3: for (int i = sb.length() - 1; i >= 0; --i) {
            char c = sb.charAt(i);
            if (Character.isWhitespace(c)) {
                sb.delete(i, i + 1);
                continue;
            }
            switch (c) {
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': 
                case '9': 
                case 'A': 
                case 'B': 
                case 'C': 
                case 'D': 
                case 'E': 
                case 'F': 
                case 'a': 
                case 'b': 
                case 'c': 
                case 'd': 
                case 'e': 
                case 'f': {
                    continue block3;
                }
                default: {
                    hex = false;
                }
            }
        }
        if (hex) {
            return Hex.toByteArray(sb.toString());
        }
        return Base64.decodeBase64(sb.toString());
    }
}

