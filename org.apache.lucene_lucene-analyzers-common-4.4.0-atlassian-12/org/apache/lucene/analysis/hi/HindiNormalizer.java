/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.hi;

import org.apache.lucene.analysis.util.StemmerUtil;

public class HindiNormalizer {
    public int normalize(char[] s, int len) {
        block36: for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u0928': {
                    if (i + 1 >= len || s[i + 1] != '\u094d') continue block36;
                    s[i] = 2306;
                    len = StemmerUtil.delete(s, i + 1, len);
                    continue block36;
                }
                case '\u0901': {
                    s[i] = 2306;
                    continue block36;
                }
                case '\u093c': {
                    len = StemmerUtil.delete(s, i, len);
                    --i;
                    continue block36;
                }
                case '\u0929': {
                    s[i] = 2344;
                    continue block36;
                }
                case '\u0931': {
                    s[i] = 2352;
                    continue block36;
                }
                case '\u0934': {
                    s[i] = 2355;
                    continue block36;
                }
                case '\u0958': {
                    s[i] = 2325;
                    continue block36;
                }
                case '\u0959': {
                    s[i] = 2326;
                    continue block36;
                }
                case '\u095a': {
                    s[i] = 2327;
                    continue block36;
                }
                case '\u095b': {
                    s[i] = 2332;
                    continue block36;
                }
                case '\u095c': {
                    s[i] = 2337;
                    continue block36;
                }
                case '\u095d': {
                    s[i] = 2338;
                    continue block36;
                }
                case '\u095e': {
                    s[i] = 2347;
                    continue block36;
                }
                case '\u095f': {
                    s[i] = 2351;
                    continue block36;
                }
                case '\u200c': 
                case '\u200d': {
                    len = StemmerUtil.delete(s, i, len);
                    --i;
                    continue block36;
                }
                case '\u094d': {
                    len = StemmerUtil.delete(s, i, len);
                    --i;
                    continue block36;
                }
                case '\u0945': 
                case '\u0946': {
                    s[i] = 2375;
                    continue block36;
                }
                case '\u0949': 
                case '\u094a': {
                    s[i] = 2379;
                    continue block36;
                }
                case '\u090d': 
                case '\u090e': {
                    s[i] = 2319;
                    continue block36;
                }
                case '\u0911': 
                case '\u0912': {
                    s[i] = 2323;
                    continue block36;
                }
                case '\u0972': {
                    s[i] = 2309;
                    continue block36;
                }
                case '\u0906': {
                    s[i] = 2309;
                    continue block36;
                }
                case '\u0908': {
                    s[i] = 2311;
                    continue block36;
                }
                case '\u090a': {
                    s[i] = 2313;
                    continue block36;
                }
                case '\u0960': {
                    s[i] = 2315;
                    continue block36;
                }
                case '\u0961': {
                    s[i] = 2316;
                    continue block36;
                }
                case '\u0910': {
                    s[i] = 2319;
                    continue block36;
                }
                case '\u0914': {
                    s[i] = 2323;
                    continue block36;
                }
                case '\u0940': {
                    s[i] = 2367;
                    continue block36;
                }
                case '\u0942': {
                    s[i] = 2369;
                    continue block36;
                }
                case '\u0944': {
                    s[i] = 2371;
                    continue block36;
                }
                case '\u0963': {
                    s[i] = 2402;
                    continue block36;
                }
                case '\u0948': {
                    s[i] = 2375;
                    continue block36;
                }
                case '\u094c': {
                    s[i] = 2379;
                    continue block36;
                }
            }
        }
        return len;
    }
}

