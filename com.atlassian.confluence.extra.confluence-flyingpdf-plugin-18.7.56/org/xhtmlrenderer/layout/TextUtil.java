/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.util.Uu;

public class TextUtil {
    public static String transformText(String text, CalculatedStyle style) {
        IdentValue fontVariant;
        IdentValue transform = style.getIdent(CSSName.TEXT_TRANSFORM);
        if (transform == IdentValue.LOWERCASE) {
            text = text.toLowerCase();
        }
        if (transform == IdentValue.UPPERCASE) {
            text = text.toUpperCase();
        }
        if (transform == IdentValue.CAPITALIZE) {
            text = TextUtil.capitalizeWords(text);
        }
        if ((fontVariant = style.getIdent(CSSName.FONT_VARIANT)) == IdentValue.SMALL_CAPS) {
            text = text.toUpperCase();
        }
        return text;
    }

    public static String transformFirstLetterText(String text, CalculatedStyle style) {
        if (text.length() > 0) {
            IdentValue transform = style.getIdent(CSSName.TEXT_TRANSFORM);
            IdentValue fontVariant = style.getIdent(CSSName.FONT_VARIANT);
            int end = text.length();
            for (int i = 0; i < end; ++i) {
                char currentChar = text.charAt(i);
                if (TextUtil.isFirstLetterSeparatorChar(currentChar)) continue;
                if (transform == IdentValue.LOWERCASE) {
                    currentChar = Character.toLowerCase(currentChar);
                    text = TextUtil.replaceChar(text, currentChar, i);
                    break;
                }
                if (transform != IdentValue.UPPERCASE && transform != IdentValue.CAPITALIZE && fontVariant != IdentValue.SMALL_CAPS) break;
                currentChar = Character.toUpperCase(currentChar);
                text = TextUtil.replaceChar(text, currentChar, i);
                break;
            }
        }
        return text;
    }

    public static String replaceChar(String text, char newChar, int index) {
        int textLength = text.length();
        StringBuilder b = new StringBuilder(textLength);
        for (int i = 0; i < textLength; ++i) {
            if (i == index) {
                b.append(newChar);
                continue;
            }
            b.append(text.charAt(i));
        }
        return b.toString();
    }

    public static boolean isFirstLetterSeparatorChar(char c) {
        switch (Character.getType(c)) {
            case 12: 
            case 21: 
            case 22: 
            case 24: 
            case 29: 
            case 30: {
                return true;
            }
        }
        return false;
    }

    private static String capitalizeWords(String text) {
        if (text.length() == 0) {
            return text;
        }
        StringBuffer sb = new StringBuffer();
        boolean cap = true;
        for (int i = 0; i < text.length(); ++i) {
            String ch = text.substring(i, i + 1);
            if (cap) {
                sb.append(ch.toUpperCase());
            } else {
                sb.append(ch);
            }
            cap = false;
            if (!ch.equals(" ")) continue;
            cap = true;
        }
        if (sb.toString().length() != text.length()) {
            Uu.p("error! to strings arent the same length = -" + sb.toString() + "-" + text + "-");
        }
        return sb.toString();
    }
}

