/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import java.lang.reflect.Type;
import java.util.Map;

public class CharacterHandler
extends Handler {
    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws Exception {
        Character c = (Character)object;
        char v = c.charValue();
        app.append(v + "");
    }

    @Override
    public Object decode(Decoder dec, boolean s) {
        return Character.valueOf(s ? (char)'t' : 'f');
    }

    @Override
    public Object decode(Decoder dec, String s) {
        return Character.valueOf((char)Integer.parseInt(s));
    }

    @Override
    public Object decode(Decoder dec, Number s) {
        return Character.valueOf((char)s.shortValue());
    }

    @Override
    public Object decode(Decoder dec) {
        return 0;
    }
}

