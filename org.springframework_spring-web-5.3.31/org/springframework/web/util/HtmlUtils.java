/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.web.util;

import org.springframework.util.Assert;
import org.springframework.web.util.HtmlCharacterEntityDecoder;
import org.springframework.web.util.HtmlCharacterEntityReferences;

public abstract class HtmlUtils {
    private static final HtmlCharacterEntityReferences characterEntityReferences = new HtmlCharacterEntityReferences();

    public static String htmlEscape(String input) {
        return HtmlUtils.htmlEscape(input, "ISO-8859-1");
    }

    public static String htmlEscape(String input, String encoding) {
        Assert.notNull((Object)input, (String)"Input is required");
        Assert.notNull((Object)encoding, (String)"Encoding is required");
        StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); ++i) {
            char character = input.charAt(i);
            String reference = characterEntityReferences.convertToReference(character, encoding);
            if (reference != null) {
                escaped.append(reference);
                continue;
            }
            escaped.append(character);
        }
        return escaped.toString();
    }

    public static String htmlEscapeDecimal(String input) {
        return HtmlUtils.htmlEscapeDecimal(input, "ISO-8859-1");
    }

    public static String htmlEscapeDecimal(String input, String encoding) {
        Assert.notNull((Object)input, (String)"Input is required");
        Assert.notNull((Object)encoding, (String)"Encoding is required");
        StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); ++i) {
            char character = input.charAt(i);
            if (characterEntityReferences.isMappedToReference(character, encoding)) {
                escaped.append("&#");
                escaped.append((int)character);
                escaped.append(';');
                continue;
            }
            escaped.append(character);
        }
        return escaped.toString();
    }

    public static String htmlEscapeHex(String input) {
        return HtmlUtils.htmlEscapeHex(input, "ISO-8859-1");
    }

    public static String htmlEscapeHex(String input, String encoding) {
        Assert.notNull((Object)input, (String)"Input is required");
        Assert.notNull((Object)encoding, (String)"Encoding is required");
        StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); ++i) {
            char character = input.charAt(i);
            if (characterEntityReferences.isMappedToReference(character, encoding)) {
                escaped.append("&#x");
                escaped.append(Integer.toString(character, 16));
                escaped.append(';');
                continue;
            }
            escaped.append(character);
        }
        return escaped.toString();
    }

    public static String htmlUnescape(String input) {
        return new HtmlCharacterEntityDecoder(characterEntityReferences, input).decode();
    }
}

