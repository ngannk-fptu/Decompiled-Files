/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import aQute.lib.json.StringHandler;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Pattern;

public class SpecialHandler
extends Handler {
    final Class type;
    final Method valueOf;
    final Constructor<?> constructor;

    public SpecialHandler(Class<?> type, Constructor<?> constructor, Method valueOf) {
        this.type = type;
        this.constructor = constructor;
        this.valueOf = valueOf;
    }

    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
        StringHandler.string(app, object.toString());
    }

    @Override
    public Object decode(Decoder dec, String s) throws Exception {
        if (this.type == Pattern.class) {
            return Pattern.compile(s);
        }
        if (this.constructor != null) {
            return this.constructor.newInstance(s);
        }
        if (this.valueOf != null) {
            return this.valueOf.invoke(null, s);
        }
        throw new IllegalArgumentException("Do not know how to convert a " + this.type + " from a string");
    }
}

