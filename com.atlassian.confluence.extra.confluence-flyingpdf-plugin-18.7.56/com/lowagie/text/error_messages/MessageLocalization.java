/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.error_messages;

import com.lowagie.text.pdf.BaseFont;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class MessageLocalization {
    private static Map<String, String> defaultLanguage = new HashMap<String, String>();
    private static Map<String, String> currentLanguage;
    private static final String BASE_PATH = "com/lowagie/text/error_messages/";

    private MessageLocalization() {
    }

    public static String getMessage(String key) {
        String val;
        Map<String, String> cl = currentLanguage;
        if (cl != null && (val = cl.get(key)) != null) {
            return val;
        }
        cl = defaultLanguage;
        val = cl.get(key);
        if (val != null) {
            return val;
        }
        return "No message found for " + key;
    }

    public static String getComposedMessage(String key) {
        return MessageLocalization.getComposedMessage(key, null, null, null, null);
    }

    public static String getComposedMessage(String key, Object p1) {
        return MessageLocalization.getComposedMessage(key, p1, null, null, null);
    }

    public static String getComposedMessage(String key, int p1) {
        return MessageLocalization.getComposedMessage(key, String.valueOf(p1), null, null, null);
    }

    public static String getComposedMessage(String key, Object p1, Object p2) {
        return MessageLocalization.getComposedMessage(key, p1, p2, null, null);
    }

    public static String getComposedMessage(String key, Object p1, Object p2, Object p3) {
        return MessageLocalization.getComposedMessage(key, p1, p2, p3, null);
    }

    public static String getComposedMessage(String key, Object p1, Object p2, Object p3, Object p4) {
        String msg = MessageLocalization.getMessage(key);
        if (p1 != null) {
            msg = msg.replaceAll("\\{1}", p1.toString());
        }
        if (p2 != null) {
            msg = msg.replaceAll("\\{2}", p2.toString());
        }
        if (p3 != null) {
            msg = msg.replaceAll("\\{3}", p3.toString());
        }
        if (p4 != null) {
            msg = msg.replaceAll("\\{4}", p4.toString());
        }
        return msg;
    }

    public static boolean setLanguage(String language, String country) throws IOException {
        Map<String, String> lang = MessageLocalization.getLanguageMessages(language, country);
        if (lang == null) {
            return false;
        }
        currentLanguage = lang;
        return true;
    }

    public static void setMessages(Reader r) throws IOException {
        currentLanguage = MessageLocalization.readLanguageStream(r);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Map<String, String> getLanguageMessages(String language, String country) throws IOException {
        if (language == null) {
            throw new IllegalArgumentException("The language cannot be null.");
        }
        InputStream is = null;
        try {
            String file = country != null ? language + "_" + country + ".lng" : language + ".lng";
            is = BaseFont.getResourceStream(BASE_PATH + file, MessageLocalization.class.getClassLoader());
            if (is != null) {
                Map<String, String> map = MessageLocalization.readLanguageStream(is);
                return map;
            }
            if (country == null) {
                Map<String, String> map = null;
                return map;
            }
            file = language + ".lng";
            is = BaseFont.getResourceStream(BASE_PATH + file, MessageLocalization.class.getClassLoader());
            if (is != null) {
                Map<String, String> map = MessageLocalization.readLanguageStream(is);
                return map;
            }
            Map<String, String> map = null;
            return map;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Exception exception) {}
        }
    }

    private static Map<String, String> readLanguageStream(InputStream is) throws IOException {
        return MessageLocalization.readLanguageStream(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    private static Map<String, String> readLanguageStream(Reader r) throws IOException {
        String line;
        HashMap<String, String> lang = new HashMap<String, String>();
        BufferedReader br = new BufferedReader(r);
        while ((line = br.readLine()) != null) {
            String key;
            int idxeq = line.indexOf(61);
            if (idxeq < 0 || (key = line.substring(0, idxeq).trim()).startsWith("#")) continue;
            lang.put(key, line.substring(idxeq + 1));
        }
        return lang;
    }

    static {
        try {
            defaultLanguage = MessageLocalization.getLanguageMessages("en", null);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (defaultLanguage == null) {
            defaultLanguage = new HashMap<String, String>();
        }
    }
}

