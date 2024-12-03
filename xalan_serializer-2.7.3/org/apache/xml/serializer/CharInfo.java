/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.xml.transform.TransformerException;
import org.apache.xml.serializer.ObjectFactory;
import org.apache.xml.serializer.SerializerBase;
import org.apache.xml.serializer.utils.SystemIDResolver;
import org.apache.xml.serializer.utils.Utils;
import org.apache.xml.serializer.utils.WrappedRuntimeException;

final class CharInfo {
    private HashMap m_charToString;
    public static final String HTML_ENTITIES_RESOURCE = SerializerBase.PKG_NAME + ".HTMLEntities";
    public static final String XML_ENTITIES_RESOURCE = SerializerBase.PKG_NAME + ".XMLEntities";
    static final char S_HORIZONAL_TAB = '\t';
    static final char S_LINEFEED = '\n';
    static final char S_CARRIAGERETURN = '\r';
    static final char S_SPACE = ' ';
    static final char S_QUOTE = '\"';
    static final char S_LT = '<';
    static final char S_GT = '>';
    static final char S_NEL = '\u0085';
    static final char S_LINE_SEPARATOR = '\u2028';
    boolean onlyQuotAmpLtGt = true;
    static final int ASCII_MAX = 128;
    private final boolean[] shouldMapAttrChar_ASCII;
    private final boolean[] shouldMapTextChar_ASCII;
    private final int[] array_of_bits = this.createEmptySetOfIntegers(65535);
    private static final int SHIFT_PER_WORD = 5;
    private static final int LOW_ORDER_BITMASK = 31;
    private int firstWordNotUsed = 0;
    private final CharKey m_charKey;
    private static Hashtable m_getCharInfoCache = new Hashtable();

    private CharInfo() {
        this.shouldMapAttrChar_ASCII = new boolean[128];
        this.shouldMapTextChar_ASCII = new boolean[128];
        this.m_charKey = new CharKey();
    }

    private CharInfo(String entitiesResource, String method, boolean internal) {
        this();
        this.m_charToString = new HashMap();
        ResourceBundle entities = null;
        boolean noExtraEntities = true;
        if (internal) {
            try {
                entities = PropertyResourceBundle.getBundle(entitiesResource);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (entities != null) {
            Enumeration<String> keys = entities.getKeys();
            while (keys.hasMoreElements()) {
                String value;
                int code;
                String name = keys.nextElement();
                boolean extra = this.defineEntity(name, (char)(code = Integer.parseInt(value = entities.getString(name))));
                if (!extra) continue;
                noExtraEntities = false;
            }
        } else {
            InputStream is = null;
            try {
                BufferedReader reader;
                if (internal) {
                    is = CharInfo.class.getResourceAsStream(entitiesResource);
                } else {
                    ClassLoader cl = ObjectFactory.findClassLoader();
                    is = cl == null ? ClassLoader.getSystemResourceAsStream(entitiesResource) : cl.getResourceAsStream(entitiesResource);
                    if (is == null) {
                        try {
                            URL url = new URL(entitiesResource);
                            is = url.openStream();
                        }
                        catch (Exception url) {
                            // empty catch block
                        }
                    }
                }
                if (is == null) {
                    throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_FIND", new Object[]{entitiesResource, entitiesResource}));
                }
                try {
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    reader = new BufferedReader(new InputStreamReader(is));
                }
                String line = reader.readLine();
                while (line != null) {
                    if (line.length() == 0 || line.charAt(0) == '#') {
                        line = reader.readLine();
                        continue;
                    }
                    int index = line.indexOf(32);
                    if (index > 1) {
                        String name = line.substring(0, index);
                        if (++index < line.length()) {
                            int code;
                            boolean extra;
                            String value = line.substring(index);
                            if ((index = value.indexOf(32)) > 0) {
                                value = value.substring(0, index);
                            }
                            if (extra = this.defineEntity(name, (char)(code = Integer.parseInt(value)))) {
                                noExtraEntities = false;
                            }
                        }
                    }
                    line = reader.readLine();
                }
                is.close();
            }
            catch (Exception e) {
                throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_LOAD", new Object[]{entitiesResource, e.toString(), entitiesResource, e.toString()}));
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
        this.onlyQuotAmpLtGt = noExtraEntities;
        if ("xml".equals(method)) {
            this.shouldMapTextChar_ASCII[34] = false;
        }
        if ("html".equals(method)) {
            this.shouldMapAttrChar_ASCII[60] = false;
            this.shouldMapTextChar_ASCII[34] = false;
        }
    }

    private boolean defineEntity(String name, char value) {
        StringBuffer sb = new StringBuffer("&");
        sb.append(name);
        sb.append(';');
        String entityString = sb.toString();
        boolean extra = this.defineChar2StringMapping(entityString, value);
        return extra;
    }

    String getOutputStringForChar(char value) {
        this.m_charKey.setChar(value);
        return (String)this.m_charToString.get(this.m_charKey);
    }

    final boolean shouldMapAttrChar(int value) {
        if (value < 128) {
            return this.shouldMapAttrChar_ASCII[value];
        }
        return this.get(value);
    }

    final boolean shouldMapTextChar(int value) {
        if (value < 128) {
            return this.shouldMapTextChar_ASCII[value];
        }
        return this.get(value);
    }

    private static CharInfo getCharInfoBasedOnPrivilege(final String entitiesFileName, final String method, final boolean internal) {
        return (CharInfo)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return new CharInfo(entitiesFileName, method, internal);
            }
        });
    }

    static CharInfo getCharInfo(String entitiesFileName, String method) {
        CharInfo charInfo = (CharInfo)m_getCharInfoCache.get(entitiesFileName);
        if (charInfo != null) {
            return CharInfo.mutableCopyOf(charInfo);
        }
        try {
            charInfo = CharInfo.getCharInfoBasedOnPrivilege(entitiesFileName, method, true);
            m_getCharInfoCache.put(entitiesFileName, charInfo);
            return CharInfo.mutableCopyOf(charInfo);
        }
        catch (Exception exception) {
            try {
                return CharInfo.getCharInfoBasedOnPrivilege(entitiesFileName, method, false);
            }
            catch (Exception exception2) {
                if (entitiesFileName.indexOf(58) < 0) {
                    String absoluteEntitiesFileName = SystemIDResolver.getAbsoluteURIFromRelative(entitiesFileName);
                } else {
                    try {
                        String absoluteEntitiesFileName = SystemIDResolver.getAbsoluteURI(entitiesFileName, null);
                    }
                    catch (TransformerException te) {
                        throw new WrappedRuntimeException(te);
                    }
                }
                return CharInfo.getCharInfoBasedOnPrivilege(entitiesFileName, method, false);
            }
        }
    }

    private static CharInfo mutableCopyOf(CharInfo charInfo) {
        CharInfo copy = new CharInfo();
        int max = charInfo.array_of_bits.length;
        System.arraycopy(charInfo.array_of_bits, 0, copy.array_of_bits, 0, max);
        copy.firstWordNotUsed = charInfo.firstWordNotUsed;
        max = charInfo.shouldMapAttrChar_ASCII.length;
        System.arraycopy(charInfo.shouldMapAttrChar_ASCII, 0, copy.shouldMapAttrChar_ASCII, 0, max);
        max = charInfo.shouldMapTextChar_ASCII.length;
        System.arraycopy(charInfo.shouldMapTextChar_ASCII, 0, copy.shouldMapTextChar_ASCII, 0, max);
        copy.m_charToString = (HashMap)charInfo.m_charToString.clone();
        copy.onlyQuotAmpLtGt = charInfo.onlyQuotAmpLtGt;
        return copy;
    }

    private static int arrayIndex(int i) {
        return i >> 5;
    }

    private static int bit(int i) {
        int ret = 1 << (i & 0x1F);
        return ret;
    }

    private int[] createEmptySetOfIntegers(int max) {
        this.firstWordNotUsed = 0;
        int[] arr = new int[CharInfo.arrayIndex(max - 1) + 1];
        return arr;
    }

    private final void set(int i) {
        this.setASCIItextDirty(i);
        this.setASCIIattrDirty(i);
        int j = i >> 5;
        int k = j + 1;
        if (this.firstWordNotUsed < k) {
            this.firstWordNotUsed = k;
        }
        int n = j;
        this.array_of_bits[n] = this.array_of_bits[n] | 1 << (i & 0x1F);
    }

    private final boolean get(int i) {
        boolean in_the_set = false;
        int j = i >> 5;
        if (j < this.firstWordNotUsed) {
            in_the_set = (this.array_of_bits[j] & 1 << (i & 0x1F)) != 0;
        }
        return in_the_set;
    }

    private boolean extraEntity(String outputString, int charToMap) {
        boolean extra = false;
        if (charToMap < 128) {
            switch (charToMap) {
                case 34: {
                    if (outputString.equals("&quot;")) break;
                    extra = true;
                    break;
                }
                case 38: {
                    if (outputString.equals("&amp;")) break;
                    extra = true;
                    break;
                }
                case 60: {
                    if (outputString.equals("&lt;")) break;
                    extra = true;
                    break;
                }
                case 62: {
                    if (outputString.equals("&gt;")) break;
                    extra = true;
                    break;
                }
                default: {
                    extra = true;
                }
            }
        }
        return extra;
    }

    private void setASCIItextDirty(int j) {
        if (0 <= j && j < 128) {
            this.shouldMapTextChar_ASCII[j] = true;
        }
    }

    private void setASCIIattrDirty(int j) {
        if (0 <= j && j < 128) {
            this.shouldMapAttrChar_ASCII[j] = true;
        }
    }

    boolean defineChar2StringMapping(String outputString, char inputChar) {
        CharKey character = new CharKey(inputChar);
        this.m_charToString.put(character, outputString);
        this.set(inputChar);
        boolean extraMapping = this.extraEntity(outputString, inputChar);
        return extraMapping;
    }

    private static class CharKey {
        private char m_char;

        public CharKey(char key) {
            this.m_char = key;
        }

        public CharKey() {
        }

        public final void setChar(char c) {
            this.m_char = c;
        }

        public final int hashCode() {
            return this.m_char;
        }

        public final boolean equals(Object obj) {
            return ((CharKey)obj).m_char == this.m_char;
        }
    }
}

