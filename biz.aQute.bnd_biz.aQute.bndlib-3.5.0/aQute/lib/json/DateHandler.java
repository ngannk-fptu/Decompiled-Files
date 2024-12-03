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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateHandler
extends Handler {
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
        String s;
        SimpleDateFormat simpleDateFormat = sdf;
        synchronized (simpleDateFormat) {
            s = sdf.format((Date)object);
        }
        StringHandler.string(app, s);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object decode(Decoder dec, String s) throws Exception {
        SimpleDateFormat simpleDateFormat = sdf;
        synchronized (simpleDateFormat) {
            return sdf.parse(s);
        }
    }

    @Override
    public Object decode(Decoder dec, Number s) throws Exception {
        return new Date(s.longValue());
    }

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
}

