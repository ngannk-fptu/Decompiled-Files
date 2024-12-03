/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.xmlgraphics.ps.PSDictionaryFormatException;

public class PSDictionary
extends HashMap {
    private static final long serialVersionUID = 815367222496219197L;

    public static PSDictionary valueOf(String str) throws PSDictionaryFormatException {
        return new Maker().parseDictionary(str);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PSDictionary)) {
            return false;
        }
        PSDictionary dictionaryObj = (PSDictionary)obj;
        if (dictionaryObj.size() != this.size()) {
            return false;
        }
        for (Map.Entry e : this.entrySet()) {
            Map.Entry entry = e;
            String key = (String)entry.getKey();
            if (!dictionaryObj.containsKey(key)) {
                return false;
            }
            if (dictionaryObj.get(key).equals(entry.getValue())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 7;
        for (Object value : this.values()) {
            hashCode += value.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer("<<\n");
        for (Object o : super.keySet()) {
            String key = (String)o;
            sb.append("  " + key + " ");
            Object obj = super.get(key);
            if (obj instanceof ArrayList) {
                List array = (List)obj;
                StringBuilder str = new StringBuilder("[");
                for (Object element : array) {
                    str.append(element + " ");
                }
                String str2 = str.toString().trim();
                str2 = str2 + "]";
                sb.append(str2 + "\n");
                continue;
            }
            sb.append(obj.toString() + "\n");
        }
        sb.append(">>");
        return sb.toString();
    }

    private static class Maker {
        private static final String[][] BRACES = new String[][]{{"<<", ">>"}, {"[", "]"}, {"{", "}"}, {"(", ")"}};
        private static final int OPENING = 0;
        private static final int CLOSING = 1;
        private static final int DICTIONARY = 0;
        private static final int ARRAY = 1;
        private static final int PROCEDURE = 2;
        private static final int STRING = 3;

        private Maker() {
        }

        protected Token nextToken(String str, int fromIndex) {
            Token t = null;
            for (int i = fromIndex; i < str.length(); ++i) {
                boolean isWhitespace = Character.isWhitespace(str.charAt(i));
                if (t == null && !isWhitespace) {
                    t = new Token();
                    t.startIndex = i;
                    continue;
                }
                if (t == null || !isWhitespace) continue;
                t.endIndex = i;
                break;
            }
            if (t != null) {
                if (t.endIndex == -1) {
                    t.endIndex = str.length();
                }
                t.value = str.substring(t.startIndex, t.endIndex);
            }
            return t;
        }

        private int indexOfMatchingBrace(String str, String[] braces, int fromIndex) throws PSDictionaryFormatException {
            int len = str.length();
            if (braces.length != 2) {
                throw new PSDictionaryFormatException("Wrong number of braces");
            }
            int openCnt = 0;
            int closeCnt = 0;
            while (fromIndex < len) {
                if (str.startsWith(braces[0], fromIndex)) {
                    ++openCnt;
                } else if (str.startsWith(braces[1], fromIndex) && openCnt > 0 && openCnt == ++closeCnt) {
                    return fromIndex;
                }
                ++fromIndex;
            }
            return -1;
        }

        private String stripBraces(String str, String[] braces) throws PSDictionaryFormatException {
            int firstIndex = str.indexOf(braces[0]);
            if (firstIndex == -1) {
                throw new PSDictionaryFormatException("Failed to find opening parameter '" + braces[0] + "");
            }
            int lastIndex = this.indexOfMatchingBrace(str, braces, firstIndex);
            if (lastIndex == -1) {
                throw new PSDictionaryFormatException("Failed to find matching closing parameter '" + braces[1] + "'");
            }
            int braceLen = braces[0].length();
            str = str.substring(firstIndex + braceLen, lastIndex).trim();
            return str;
        }

        public PSDictionary parseDictionary(String str) throws PSDictionaryFormatException {
            Token keyToken;
            PSDictionary dictionary = new PSDictionary();
            str = this.stripBraces(str.trim(), BRACES[0]);
            int len = str.length();
            int currIndex = 0;
            while ((keyToken = this.nextToken(str, currIndex)) != null && currIndex <= len) {
                if (keyToken.value == null) {
                    throw new PSDictionaryFormatException("Failed to parse object key");
                }
                Token valueToken = this.nextToken(str, keyToken.endIndex + 1);
                String[] braces = null;
                for (String[] brace : BRACES) {
                    if (!valueToken.value.startsWith(brace[0])) continue;
                    braces = brace;
                    break;
                }
                ArrayList<String> obj = null;
                if (braces != null) {
                    valueToken.endIndex = this.indexOfMatchingBrace(str, braces, valueToken.startIndex) + braces[0].length();
                    if (valueToken.endIndex < 0) {
                        throw new PSDictionaryFormatException("Closing value brace '" + braces[1] + "' not found for key '" + keyToken.value + "'");
                    }
                    valueToken.value = str.substring(valueToken.startIndex, valueToken.endIndex);
                }
                if (braces == null || braces == BRACES[2] || braces == BRACES[3]) {
                    obj = valueToken.value;
                } else if (BRACES[1] == braces) {
                    ArrayList<String> objList = new ArrayList<String>();
                    String objString = this.stripBraces(valueToken.value, braces);
                    StringTokenizer tokenizer = new StringTokenizer(objString, ",");
                    while (tokenizer.hasMoreTokens()) {
                        objList.add(tokenizer.nextToken());
                    }
                    obj = objList;
                } else if (BRACES[0] == braces) {
                    obj = this.parseDictionary(valueToken.value);
                }
                dictionary.put(keyToken.value, obj);
                currIndex = valueToken.endIndex + 1;
            }
            return dictionary;
        }

        private static class Token {
            private int startIndex = -1;
            private int endIndex = -1;
            private String value;

            private Token() {
            }
        }
    }
}

