/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import net.fortuna.ical4j.util.CompatibilityHints;

public final class Uris {
    public static final String INVALID_SCHEME = "net.fortunal.ical4j.invalid";
    private static final Pattern CID_PATTERN = Pattern.compile("(?i)^cid:.*");
    private static final Pattern NOTES_CID_REPLACEMENT_PATTERN = Pattern.compile("[<>]");

    private Uris() {
    }

    public static String encode(String s) {
        if (CompatibilityHints.isHintEnabled("ical4j.compatibility.notes") && CID_PATTERN.matcher(s).matches()) {
            return NOTES_CID_REPLACEMENT_PATTERN.matcher(s).replaceAll("");
        }
        return s;
    }

    public static String decode(String s) {
        return s;
    }

    public static URI create(String s) throws URISyntaxException {
        try {
            return new URI(Uris.encode(s));
        }
        catch (URISyntaxException use) {
            if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) {
                String encoded = Uris.encode(s);
                int index = encoded.indexOf(58);
                if (index != -1 && index < encoded.length() - 1) {
                    try {
                        return new URI(encoded.substring(0, index), encoded.substring(index + 1), null);
                    }
                    catch (URISyntaxException uRISyntaxException) {
                        // empty catch block
                    }
                }
                try {
                    return new URI(INVALID_SCHEME, s, null);
                }
                catch (URISyntaxException use2) {
                    throw new IllegalArgumentException("Could not build URI from " + s);
                }
            }
            throw use;
        }
    }
}

