/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Integers;

public class ISOTrailers {
    private static final Map<String, Integer> trailerMap;
    public static final int TRAILER_IMPLICIT = 188;
    public static final int TRAILER_RIPEMD160 = 12748;
    public static final int TRAILER_RIPEMD128 = 13004;
    public static final int TRAILER_SHA1 = 13260;
    public static final int TRAILER_SHA256 = 13516;
    public static final int TRAILER_SHA512 = 13772;
    public static final int TRAILER_SHA384 = 14028;
    public static final int TRAILER_WHIRLPOOL = 14284;
    public static final int TRAILER_SHA224 = 14540;
    public static final int TRAILER_SHA512_224 = 14796;
    public static final int TRAILER_SHA512_256 = 15052;

    public static Integer getTrailer(Digest digest) {
        return trailerMap.get(digest.getAlgorithmName());
    }

    public static boolean noTrailerAvailable(Digest digest) {
        return !trailerMap.containsKey(digest.getAlgorithmName());
    }

    static {
        HashMap<String, Integer> trailers = new HashMap<String, Integer>();
        trailers.put("RIPEMD128", Integers.valueOf(13004));
        trailers.put("RIPEMD160", Integers.valueOf(12748));
        trailers.put("SHA-1", Integers.valueOf(13260));
        trailers.put("SHA-224", Integers.valueOf(14540));
        trailers.put("SHA-256", Integers.valueOf(13516));
        trailers.put("SHA-384", Integers.valueOf(14028));
        trailers.put("SHA-512", Integers.valueOf(13772));
        trailers.put("SHA-512/224", Integers.valueOf(14796));
        trailers.put("SHA-512/256", Integers.valueOf(15052));
        trailers.put("Whirlpool", Integers.valueOf(14284));
        trailerMap = Collections.unmodifiableMap(trailers);
    }
}

