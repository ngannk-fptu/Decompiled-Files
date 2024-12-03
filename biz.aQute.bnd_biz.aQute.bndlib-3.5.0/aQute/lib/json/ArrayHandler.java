/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.hex.Hex;
import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import aQute.lib.json.StringHandler;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class ArrayHandler
extends Handler {
    Type componentType;

    ArrayHandler(Class<?> rawClass, Type componentType) {
        this.componentType = componentType;
    }

    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
        if (object instanceof byte[]) {
            StringHandler.string(app, Hex.toHexString((byte[])object));
            return;
        }
        app.append("[");
        app.indent();
        String del = "";
        int l = Array.getLength(object);
        for (int i = 0; i < l; ++i) {
            try {
                app.append(del);
                app.encode(Array.get(object, i), this.componentType, visited);
                del = ",";
                continue;
            }
            catch (Exception e) {
                throw new IllegalArgumentException("[" + i + "]", e);
            }
        }
        app.undent();
        app.append("]");
    }

    @Override
    public Object decodeArray(Decoder r) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        r.codec.parseArray(list, this.componentType, r);
        Object array = Array.newInstance(r.codec.getRawClass(this.componentType), list.size());
        int n = 0;
        for (Object o : list) {
            Array.set(array, n++, o);
        }
        return array;
    }
}

