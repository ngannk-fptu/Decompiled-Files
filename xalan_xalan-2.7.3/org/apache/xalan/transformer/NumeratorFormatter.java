/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.transformer;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.apache.xalan.transformer.DecimalToRoman;
import org.apache.xalan.transformer.TransformerImpl;
import org.w3c.dom.Element;

class NumeratorFormatter {
    protected Element m_xslNumberElement;
    NumberFormatStringTokenizer m_formatTokenizer;
    Locale m_locale;
    NumberFormat m_formatter;
    TransformerImpl m_processor;
    private static final DecimalToRoman[] m_romanConvertTable = new DecimalToRoman[]{new DecimalToRoman(1000L, "M", 900L, "CM"), new DecimalToRoman(500L, "D", 400L, "CD"), new DecimalToRoman(100L, "C", 90L, "XC"), new DecimalToRoman(50L, "L", 40L, "XL"), new DecimalToRoman(10L, "X", 9L, "IX"), new DecimalToRoman(5L, "V", 4L, "IV"), new DecimalToRoman(1L, "I", 1L, "I")};
    private static final char[] m_alphaCountTable = new char[]{'Z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y'};

    NumeratorFormatter(Element xslNumberElement, TransformerImpl processor) {
        this.m_xslNumberElement = xslNumberElement;
        this.m_processor = processor;
    }

    protected String int2alphaCount(int val, char[] table) {
        int radix = table.length;
        char[] buf = new char[100];
        int charPos = buf.length - 1;
        int lookupIndex = 1;
        int correction = 0;
        while ((lookupIndex = (val + (correction = lookupIndex == 0 || correction != 0 && lookupIndex == radix - 1 ? radix - 1 : 0)) % radix) != 0 || (val /= radix) != 0) {
            buf[charPos--] = table[lookupIndex];
            if (val > 0) continue;
        }
        return new String(buf, charPos + 1, buf.length - charPos - 1);
    }

    String long2roman(long val, boolean prefixesAreOK) {
        String roman;
        if (val <= 0L) {
            return "#E(" + val + ")";
        }
        int place = 0;
        if (val <= 3999L) {
            StringBuffer romanBuffer = new StringBuffer();
            while (true) {
                if (val >= NumeratorFormatter.m_romanConvertTable[place].m_postValue) {
                    romanBuffer.append(NumeratorFormatter.m_romanConvertTable[place].m_postLetter);
                    val -= NumeratorFormatter.m_romanConvertTable[place].m_postValue;
                    continue;
                }
                if (prefixesAreOK && val >= NumeratorFormatter.m_romanConvertTable[place].m_preValue) {
                    romanBuffer.append(NumeratorFormatter.m_romanConvertTable[place].m_preLetter);
                    val -= NumeratorFormatter.m_romanConvertTable[place].m_preValue;
                }
                ++place;
                if (val <= 0L) break;
            }
            roman = romanBuffer.toString();
        } else {
            roman = "#error";
        }
        return roman;
    }

    static class NumberFormatStringTokenizer {
        private int currentPosition;
        private int maxPosition;
        private String str;

        NumberFormatStringTokenizer(String str) {
            this.str = str;
            this.maxPosition = str.length();
        }

        void reset() {
            this.currentPosition = 0;
        }

        String nextToken() {
            if (this.currentPosition >= this.maxPosition) {
                throw new NoSuchElementException();
            }
            int start = this.currentPosition;
            while (this.currentPosition < this.maxPosition && Character.isLetterOrDigit(this.str.charAt(this.currentPosition))) {
                ++this.currentPosition;
            }
            if (start == this.currentPosition && !Character.isLetterOrDigit(this.str.charAt(this.currentPosition))) {
                ++this.currentPosition;
            }
            return this.str.substring(start, this.currentPosition);
        }

        boolean hasMoreTokens() {
            return this.currentPosition < this.maxPosition;
        }

        int countTokens() {
            int count = 0;
            int currpos = this.currentPosition;
            while (currpos < this.maxPosition) {
                int start = currpos;
                while (currpos < this.maxPosition && Character.isLetterOrDigit(this.str.charAt(currpos))) {
                    ++currpos;
                }
                if (start == currpos && !Character.isLetterOrDigit(this.str.charAt(currpos))) {
                    ++currpos;
                }
                ++count;
            }
            return count;
        }
    }
}

