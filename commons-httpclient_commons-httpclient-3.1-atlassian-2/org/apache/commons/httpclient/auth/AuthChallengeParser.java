/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.util.ParameterParser;

public final class AuthChallengeParser {
    public static String extractScheme(String challengeStr) throws MalformedChallengeException {
        if (challengeStr == null) {
            throw new IllegalArgumentException("Challenge may not be null");
        }
        int idx = challengeStr.indexOf(32);
        String s = null;
        s = idx == -1 ? challengeStr : challengeStr.substring(0, idx);
        if (s.equals("")) {
            throw new MalformedChallengeException("Invalid challenge: " + challengeStr);
        }
        return s.toLowerCase(Locale.ENGLISH);
    }

    public static Map extractParams(String challengeStr) throws MalformedChallengeException {
        if (challengeStr == null) {
            throw new IllegalArgumentException("Challenge may not be null");
        }
        int idx = challengeStr.indexOf(32);
        if (idx == -1) {
            throw new MalformedChallengeException("Invalid challenge: " + challengeStr);
        }
        HashMap<String, String> map = new HashMap<String, String>();
        ParameterParser parser = new ParameterParser();
        List params = parser.parse(challengeStr.substring(idx + 1, challengeStr.length()), ',');
        for (int i = 0; i < params.size(); ++i) {
            NameValuePair param = (NameValuePair)params.get(i);
            map.put(param.getName().toLowerCase(Locale.ENGLISH), param.getValue());
        }
        return map;
    }

    public static Map parseChallenges(Header[] headers) throws MalformedChallengeException {
        if (headers == null) {
            throw new IllegalArgumentException("Array of challenges may not be null");
        }
        String challenge = null;
        HashMap<String, String> challengemap = new HashMap<String, String>(headers.length);
        for (int i = 0; i < headers.length; ++i) {
            challenge = headers[i].getValue();
            String s = AuthChallengeParser.extractScheme(challenge);
            challengemap.put(s, challenge);
        }
        return challengemap;
    }
}

