/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.decode;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v3.decode.CannotDecodeException;
import com.mchange.v3.decode.Decoder;
import com.mchange.v3.decode.DecoderFinder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class DecodeUtils {
    public static final String DECODER_CLASS_DOT_KEY = ".decoderClass";
    public static final String DECODER_CLASS_NO_DOT_KEY = "decoderClass";
    private static final Object[] DECODER_CLASS_DOT_KEY_OBJ_ARRAY = new Object[]{".decoderClass"};
    private static final Object[] DECODER_CLASS_NO_DOT_KEY_OBJ_ARRAY = new Object[]{"decoderClass"};
    private static final MLogger logger = MLog.getLogger(DecodeUtils.class);
    private static final List<DecoderFinder> finders;
    private static final String[] finderClassNames;

    static final String findDecoderClassName(Object object) throws CannotDecodeException {
        for (DecoderFinder decoderFinder : finders) {
            String string = decoderFinder.decoderClassName(object);
            if (string == null) continue;
            return string;
        }
        throw new CannotDecodeException("Could not find a decoder class name for object: " + object);
    }

    public static Object decode(String string, Object object) throws CannotDecodeException {
        try {
            Class<?> clazz = Class.forName(string);
            Decoder decoder = (Decoder)clazz.newInstance();
            return decoder.decode(object);
        }
        catch (Exception exception) {
            throw new CannotDecodeException("An exception occurred while attempting to decode " + object, exception);
        }
    }

    public static Object decode(Object object) throws CannotDecodeException {
        return DecodeUtils.decode(DecodeUtils.findDecoderClassName(object), object);
    }

    private DecodeUtils() {
    }

    static {
        finderClassNames = new String[]{"com.mchange.sc.v1.decode.ScalaMapDecoderFinder"};
        LinkedList<DecoderFinder> linkedList = new LinkedList<DecoderFinder>();
        linkedList.add(new JavaMapDecoderFinder());
        int n = finderClassNames.length;
        for (int i = 0; i < n; ++i) {
            try {
                linkedList.add((DecoderFinder)Class.forName(finderClassNames[i]).newInstance());
                continue;
            }
            catch (Exception exception) {
                if (!logger.isLoggable(MLevel.INFO)) continue;
                logger.log(MLevel.INFO, "Could not load DecoderFinder '" + finderClassNames[i] + "'", exception);
            }
        }
        finders = Collections.unmodifiableList(linkedList);
    }

    static class JavaMapDecoderFinder
    implements DecoderFinder {
        JavaMapDecoderFinder() {
        }

        @Override
        public String decoderClassName(Object object) throws CannotDecodeException {
            if (object instanceof Map) {
                String string = null;
                Map map = (Map)object;
                string = (String)map.get(DecodeUtils.DECODER_CLASS_DOT_KEY);
                if (string == null) {
                    string = (String)map.get(DecodeUtils.DECODER_CLASS_NO_DOT_KEY);
                }
                if (string == null) {
                    throw new CannotDecodeException("Could not find the decoder class for java.util.Map: " + object);
                }
                return string;
            }
            return null;
        }
    }
}

