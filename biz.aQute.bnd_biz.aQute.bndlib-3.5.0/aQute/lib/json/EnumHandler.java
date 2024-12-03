/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import aQute.lib.json.StringHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class EnumHandler
extends Handler {
    final Class type;

    public EnumHandler(Class<?> type) {
        this.type = type;
    }

    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
        StringHandler.string(app, object.toString());
    }

    @Override
    public Object decode(Decoder dec, String s) throws Exception {
        return Enum.valueOf(this.type, s);
    }
}

