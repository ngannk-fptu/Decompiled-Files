/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.lang3.StringUtils;

public class DefaultConfigurationKey {
    private static final int INITIAL_SIZE = 32;
    private final DefaultExpressionEngine expressionEngine;
    private final StringBuilder keyBuffer;

    public DefaultConfigurationKey(DefaultExpressionEngine engine) {
        this(engine, null);
    }

    public DefaultConfigurationKey(DefaultExpressionEngine engine, String key) {
        if (engine == null) {
            throw new IllegalArgumentException("Expression engine must not be null!");
        }
        this.expressionEngine = engine;
        this.keyBuffer = key != null ? new StringBuilder(this.trim(key)) : new StringBuilder(32);
    }

    public DefaultExpressionEngine getExpressionEngine() {
        return this.expressionEngine;
    }

    public DefaultConfigurationKey append(String property, boolean escape) {
        String key = escape && property != null ? this.escapeDelimiters(property) : property;
        key = this.trim(key);
        if (this.keyBuffer.length() > 0 && !this.isAttributeKey(property) && !key.isEmpty()) {
            this.keyBuffer.append(this.getSymbols().getPropertyDelimiter());
        }
        this.keyBuffer.append(key);
        return this;
    }

    public DefaultConfigurationKey append(String property) {
        return this.append(property, false);
    }

    public DefaultConfigurationKey appendIndex(int index) {
        this.keyBuffer.append(this.getSymbols().getIndexStart());
        this.keyBuffer.append(index);
        this.keyBuffer.append(this.getSymbols().getIndexEnd());
        return this;
    }

    public DefaultConfigurationKey appendAttribute(String attr) {
        this.keyBuffer.append(this.constructAttributeKey(attr));
        return this;
    }

    public int length() {
        return this.keyBuffer.length();
    }

    public void setLength(int len) {
        this.keyBuffer.setLength(len);
    }

    public DefaultConfigurationKey commonKey(DefaultConfigurationKey other) {
        if (other == null) {
            throw new IllegalArgumentException("Other key must no be null!");
        }
        DefaultConfigurationKey result = new DefaultConfigurationKey(this.getExpressionEngine());
        KeyIterator it1 = this.iterator();
        KeyIterator it2 = other.iterator();
        while (it1.hasNext() && it2.hasNext() && DefaultConfigurationKey.partsEqual(it1, it2)) {
            if (it1.isAttribute()) {
                result.appendAttribute(it1.currentKey());
                continue;
            }
            result.append(it1.currentKey());
            if (!it1.hasIndex) continue;
            result.appendIndex(it1.getIndex());
        }
        return result;
    }

    public DefaultConfigurationKey differenceKey(DefaultConfigurationKey other) {
        DefaultConfigurationKey common = this.commonKey(other);
        DefaultConfigurationKey result = new DefaultConfigurationKey(this.getExpressionEngine());
        if (common.length() < other.length()) {
            int i;
            String k = other.toString().substring(common.length());
            for (i = 0; i < k.length() && String.valueOf(k.charAt(i)).equals(this.getSymbols().getPropertyDelimiter()); ++i) {
            }
            if (i < k.length()) {
                result.append(k.substring(i));
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultConfigurationKey)) {
            return false;
        }
        DefaultConfigurationKey c = (DefaultConfigurationKey)obj;
        return this.keyBuffer.toString().equals(c.toString());
    }

    public int hashCode() {
        return String.valueOf(this.keyBuffer).hashCode();
    }

    public String toString() {
        return this.keyBuffer.toString();
    }

    public boolean isAttributeKey(String key) {
        if (key == null) {
            return false;
        }
        return key.startsWith(this.getSymbols().getAttributeStart()) && (this.getSymbols().getAttributeEnd() == null || key.endsWith(this.getSymbols().getAttributeEnd()));
    }

    public String constructAttributeKey(String key) {
        if (key == null) {
            return "";
        }
        if (this.isAttributeKey(key)) {
            return key;
        }
        StringBuilder buf = new StringBuilder();
        buf.append(this.getSymbols().getAttributeStart()).append(key);
        if (this.getSymbols().getAttributeEnd() != null) {
            buf.append(this.getSymbols().getAttributeEnd());
        }
        return buf.toString();
    }

    public String attributeName(String key) {
        return this.isAttributeKey(key) ? this.removeAttributeMarkers(key) : key;
    }

    public String trimLeft(String key) {
        if (key == null) {
            return "";
        }
        String result = key;
        while (this.hasLeadingDelimiter(result)) {
            result = result.substring(this.getSymbols().getPropertyDelimiter().length());
        }
        return result;
    }

    public String trimRight(String key) {
        if (key == null) {
            return "";
        }
        String result = key;
        while (this.hasTrailingDelimiter(result)) {
            result = result.substring(0, result.length() - this.getSymbols().getPropertyDelimiter().length());
        }
        return result;
    }

    public String trim(String key) {
        return this.trimRight(this.trimLeft(key));
    }

    public KeyIterator iterator() {
        return new KeyIterator();
    }

    private boolean hasTrailingDelimiter(String key) {
        return key.endsWith(this.getSymbols().getPropertyDelimiter()) && (this.getSymbols().getEscapedDelimiter() == null || !key.endsWith(this.getSymbols().getEscapedDelimiter()));
    }

    private boolean hasLeadingDelimiter(String key) {
        return key.startsWith(this.getSymbols().getPropertyDelimiter()) && (this.getSymbols().getEscapedDelimiter() == null || !key.startsWith(this.getSymbols().getEscapedDelimiter()));
    }

    private String removeAttributeMarkers(String key) {
        return key.substring(this.getSymbols().getAttributeStart().length(), key.length() - (this.getSymbols().getAttributeEnd() != null ? this.getSymbols().getAttributeEnd().length() : 0));
    }

    private String unescapeDelimiters(String key) {
        return this.getSymbols().getEscapedDelimiter() == null ? key : StringUtils.replace((String)key, (String)this.getSymbols().getEscapedDelimiter(), (String)this.getSymbols().getPropertyDelimiter());
    }

    private DefaultExpressionEngineSymbols getSymbols() {
        return this.getExpressionEngine().getSymbols();
    }

    private String escapeDelimiters(String key) {
        return this.getSymbols().getEscapedDelimiter() == null || !key.contains(this.getSymbols().getPropertyDelimiter()) ? key : StringUtils.replace((String)key, (String)this.getSymbols().getPropertyDelimiter(), (String)this.getSymbols().getEscapedDelimiter());
    }

    private static boolean partsEqual(KeyIterator it1, KeyIterator it2) {
        return it1.nextKey().equals(it2.nextKey()) && it1.getIndex() == it2.getIndex() && it1.isAttribute() == it2.isAttribute();
    }

    public class KeyIterator
    implements Iterator<Object>,
    Cloneable {
        private String current;
        private int startIndex;
        private int endIndex;
        private int indexValue;
        private boolean hasIndex;
        private boolean attribute;

        public String nextKey() {
            return this.nextKey(false);
        }

        public String nextKey(boolean decorated) {
            String key;
            if (!this.hasNext()) {
                throw new NoSuchElementException("No more key parts!");
            }
            this.hasIndex = false;
            this.indexValue = -1;
            this.current = key = this.findNextIndices();
            this.hasIndex = this.checkIndex(key);
            this.attribute = this.checkAttribute(this.current);
            return this.currentKey(decorated);
        }

        @Override
        public boolean hasNext() {
            return this.endIndex < DefaultConfigurationKey.this.keyBuffer.length();
        }

        @Override
        public Object next() {
            return this.nextKey();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported!");
        }

        public String currentKey() {
            return this.currentKey(false);
        }

        public String currentKey(boolean decorated) {
            return decorated && !this.isPropertyKey() ? DefaultConfigurationKey.this.constructAttributeKey(this.current) : this.current;
        }

        public boolean isAttribute() {
            return this.attribute || this.isAttributeEmulatingMode() && !this.hasNext();
        }

        public boolean isPropertyKey() {
            return !this.attribute;
        }

        public int getIndex() {
            return this.indexValue;
        }

        public boolean hasIndex() {
            return this.hasIndex;
        }

        public Object clone() {
            try {
                return super.clone();
            }
            catch (CloneNotSupportedException cex) {
                return null;
            }
        }

        private String findNextIndices() {
            this.startIndex = this.endIndex;
            while (this.startIndex < DefaultConfigurationKey.this.length() && DefaultConfigurationKey.this.hasLeadingDelimiter(DefaultConfigurationKey.this.keyBuffer.substring(this.startIndex))) {
                this.startIndex += DefaultConfigurationKey.this.getSymbols().getPropertyDelimiter().length();
            }
            if (this.startIndex >= DefaultConfigurationKey.this.length()) {
                this.endIndex = DefaultConfigurationKey.this.length();
                this.startIndex = this.endIndex - 1;
                return DefaultConfigurationKey.this.keyBuffer.substring(this.startIndex, this.endIndex);
            }
            return this.nextKeyPart();
        }

        private String nextKeyPart() {
            int delIdx;
            int attrIdx = DefaultConfigurationKey.this.keyBuffer.toString().indexOf(DefaultConfigurationKey.this.getSymbols().getAttributeStart(), this.startIndex);
            if (attrIdx < 0 || attrIdx == this.startIndex) {
                attrIdx = DefaultConfigurationKey.this.length();
            }
            if ((delIdx = this.nextDelimiterPos(DefaultConfigurationKey.this.keyBuffer.toString(), this.startIndex, attrIdx)) < 0) {
                delIdx = attrIdx;
            }
            this.endIndex = Math.min(attrIdx, delIdx);
            return DefaultConfigurationKey.this.unescapeDelimiters(DefaultConfigurationKey.this.keyBuffer.substring(this.startIndex, this.endIndex));
        }

        private int nextDelimiterPos(String key, int pos, int endPos) {
            int delimiterPos = pos;
            boolean found = false;
            do {
                if ((delimiterPos = key.indexOf(DefaultConfigurationKey.this.getSymbols().getPropertyDelimiter(), delimiterPos)) < 0 || delimiterPos >= endPos) {
                    return -1;
                }
                int escapePos = this.escapedPosition(key, delimiterPos);
                if (escapePos < 0) {
                    found = true;
                    continue;
                }
                delimiterPos = escapePos;
            } while (!found);
            return delimiterPos;
        }

        private int escapedPosition(String key, int pos) {
            if (DefaultConfigurationKey.this.getSymbols().getEscapedDelimiter() == null) {
                return -1;
            }
            int escapeOffset = this.escapeOffset();
            if (escapeOffset < 0 || escapeOffset > pos) {
                return -1;
            }
            int escapePos = key.indexOf(DefaultConfigurationKey.this.getSymbols().getEscapedDelimiter(), pos - escapeOffset);
            if (escapePos <= pos && escapePos >= 0) {
                return escapePos + DefaultConfigurationKey.this.getSymbols().getEscapedDelimiter().length();
            }
            return -1;
        }

        private int escapeOffset() {
            return DefaultConfigurationKey.this.getSymbols().getEscapedDelimiter().indexOf(DefaultConfigurationKey.this.getSymbols().getPropertyDelimiter());
        }

        private boolean checkAttribute(String key) {
            if (DefaultConfigurationKey.this.isAttributeKey(key)) {
                this.current = DefaultConfigurationKey.this.removeAttributeMarkers(key);
                return true;
            }
            return false;
        }

        private boolean checkIndex(String key) {
            boolean result = false;
            try {
                int endidx;
                int idx = key.lastIndexOf(DefaultConfigurationKey.this.getSymbols().getIndexStart());
                if (idx > 0 && (endidx = key.indexOf(DefaultConfigurationKey.this.getSymbols().getIndexEnd(), idx)) > idx + 1) {
                    this.indexValue = Integer.parseInt(key.substring(idx + 1, endidx));
                    this.current = key.substring(0, idx);
                    result = true;
                }
            }
            catch (NumberFormatException nfe) {
                result = false;
            }
            return result;
        }

        private boolean isAttributeEmulatingMode() {
            return DefaultConfigurationKey.this.getSymbols().getAttributeEnd() == null && StringUtils.equals((CharSequence)DefaultConfigurationKey.this.getSymbols().getPropertyDelimiter(), (CharSequence)DefaultConfigurationKey.this.getSymbols().getAttributeStart());
        }
    }
}

