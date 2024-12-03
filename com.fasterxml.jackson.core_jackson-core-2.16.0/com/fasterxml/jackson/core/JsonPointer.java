/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.io.NumberInput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.Serializable;

public class JsonPointer
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final char SEPARATOR = '/';
    protected static final JsonPointer EMPTY = new JsonPointer();
    protected final JsonPointer _nextSegment;
    protected volatile JsonPointer _head;
    protected final String _asString;
    protected final int _asStringOffset;
    protected final String _matchingPropertyName;
    protected final int _matchingElementIndex;
    protected int _hashCode;

    protected JsonPointer() {
        this._nextSegment = null;
        this._matchingPropertyName = null;
        this._matchingElementIndex = -1;
        this._asString = "";
        this._asStringOffset = 0;
    }

    protected JsonPointer(String fullString, int fullStringOffset, String segment, JsonPointer next) {
        this._asString = fullString;
        this._asStringOffset = fullStringOffset;
        this._nextSegment = next;
        this._matchingPropertyName = segment;
        this._matchingElementIndex = JsonPointer._parseIndex(segment);
    }

    protected JsonPointer(String fullString, int fullStringOffset, String segment, int matchIndex, JsonPointer next) {
        this._asString = fullString;
        this._asStringOffset = fullStringOffset;
        this._nextSegment = next;
        this._matchingPropertyName = segment;
        this._matchingElementIndex = matchIndex;
    }

    public static JsonPointer compile(String expr) throws IllegalArgumentException {
        if (expr == null || expr.length() == 0) {
            return EMPTY;
        }
        if (expr.charAt(0) != '/') {
            throw new IllegalArgumentException("Invalid input: JSON Pointer expression must start with '/': \"" + expr + "\"");
        }
        return JsonPointer._parseTail(expr);
    }

    public static JsonPointer valueOf(String expr) {
        return JsonPointer.compile(expr);
    }

    public static JsonPointer empty() {
        return EMPTY;
    }

    public static JsonPointer forPath(JsonStreamContext context, boolean includeRoot) {
        if (context == null) {
            return EMPTY;
        }
        if (!(context.hasPathSegment() || includeRoot && context.inRoot() && context.hasCurrentIndex())) {
            context = context.getParent();
        }
        PointerSegment next = null;
        int approxLength = 0;
        while (context != null) {
            if (context.inObject()) {
                String propName = context.getCurrentName();
                if (propName == null) {
                    propName = "";
                }
                approxLength += 2 + propName.length();
                next = new PointerSegment(next, propName, -1);
            } else if (context.inArray() || includeRoot) {
                int ix = context.getCurrentIndex();
                approxLength += 6;
                next = new PointerSegment(next, null, ix);
            }
            context = context.getParent();
        }
        if (next == null) {
            return EMPTY;
        }
        StringBuilder pathBuilder = new StringBuilder(approxLength);
        PointerSegment last = null;
        while (next != null) {
            last = next;
            next.pathOffset = pathBuilder.length();
            pathBuilder.append('/');
            if (next.property != null) {
                JsonPointer._appendEscaped(pathBuilder, next.property);
            } else {
                pathBuilder.append(next.index);
            }
            next = next.next;
        }
        String fullPath = pathBuilder.toString();
        PointerSegment currSegment = last;
        JsonPointer currPtr = EMPTY;
        while (currSegment != null) {
            if (currSegment.property != null) {
                currPtr = new JsonPointer(fullPath, currSegment.pathOffset, currSegment.property, currPtr);
            } else {
                int index = currSegment.index;
                currPtr = new JsonPointer(fullPath, currSegment.pathOffset, String.valueOf(index), index, currPtr);
            }
            currSegment = currSegment.prev;
        }
        return currPtr;
    }

    private static void _appendEscaped(StringBuilder sb, String segment) {
        int end = segment.length();
        for (int i = 0; i < end; ++i) {
            char c = segment.charAt(i);
            if (c == '/') {
                sb.append("~1");
                continue;
            }
            if (c == '~') {
                sb.append("~0");
                continue;
            }
            sb.append(c);
        }
    }

    public int length() {
        return this._asString.length() - this._asStringOffset;
    }

    public boolean matches() {
        return this._nextSegment == null;
    }

    public String getMatchingProperty() {
        return this._matchingPropertyName;
    }

    public int getMatchingIndex() {
        return this._matchingElementIndex;
    }

    public boolean mayMatchProperty() {
        return this._matchingPropertyName != null;
    }

    public boolean mayMatchElement() {
        return this._matchingElementIndex >= 0;
    }

    public JsonPointer last() {
        JsonPointer next;
        JsonPointer current = this;
        if (current == EMPTY) {
            return null;
        }
        while ((next = current._nextSegment) != EMPTY) {
            current = next;
        }
        return current;
    }

    public JsonPointer append(JsonPointer tail) {
        if (this == EMPTY) {
            return tail;
        }
        if (tail == EMPTY) {
            return this;
        }
        String currentJsonPointer = this._asString;
        if (currentJsonPointer.endsWith("/")) {
            currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
        }
        return JsonPointer.compile(currentJsonPointer + tail._asString);
    }

    public JsonPointer appendProperty(String property) {
        String currentJsonPointer;
        if (property == null || property.isEmpty()) {
            return this;
        }
        if (property.charAt(0) != '/') {
            property = '/' + property;
        }
        if ((currentJsonPointer = this._asString).endsWith("/")) {
            currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
        }
        return JsonPointer.compile(currentJsonPointer + property);
    }

    public JsonPointer appendIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Negative index cannot be appended");
        }
        String currentJsonPointer = this._asString;
        if (currentJsonPointer.endsWith("/")) {
            currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
        }
        return JsonPointer.compile(currentJsonPointer + '/' + index);
    }

    public boolean matchesProperty(String name) {
        return this._nextSegment != null && this._matchingPropertyName.equals(name);
    }

    public JsonPointer matchProperty(String name) {
        if (this._nextSegment != null && this._matchingPropertyName.equals(name)) {
            return this._nextSegment;
        }
        return null;
    }

    public boolean matchesElement(int index) {
        return index == this._matchingElementIndex && index >= 0;
    }

    public JsonPointer matchElement(int index) {
        if (index != this._matchingElementIndex || index < 0) {
            return null;
        }
        return this._nextSegment;
    }

    public JsonPointer tail() {
        return this._nextSegment;
    }

    public JsonPointer head() {
        JsonPointer h = this._head;
        if (h == null) {
            if (this != EMPTY) {
                h = this._constructHead();
            }
            this._head = h;
        }
        return h;
    }

    public String toString() {
        if (this._asStringOffset <= 0) {
            return this._asString;
        }
        return this._asString.substring(this._asStringOffset);
    }

    public int hashCode() {
        int h = this._hashCode;
        if (h == 0) {
            h = this.toString().hashCode();
            if (h == 0) {
                h = -1;
            }
            this._hashCode = h;
        }
        return h;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof JsonPointer)) {
            return false;
        }
        JsonPointer other = (JsonPointer)o;
        return this._compare(this._asString, this._asStringOffset, other._asString, other._asStringOffset);
    }

    private final boolean _compare(String str1, int offset1, String str2, int offset2) {
        int end1 = str1.length();
        if (end1 - offset1 != str2.length() - offset2) {
            return false;
        }
        while (offset1 < end1) {
            if (str1.charAt(offset1++) == str2.charAt(offset2++)) continue;
            return false;
        }
        return true;
    }

    private static final int _parseIndex(String str) {
        long l;
        int len = str.length();
        if (len == 0 || len > 10) {
            return -1;
        }
        char c = str.charAt(0);
        if (c <= '0') {
            return len == 1 && c == '0' ? 0 : -1;
        }
        if (c > '9') {
            return -1;
        }
        for (int i = 1; i < len; ++i) {
            c = str.charAt(i);
            if (c <= '9' && c >= '0') continue;
            return -1;
        }
        if (len == 10 && (l = NumberInput.parseLong(str)) > Integer.MAX_VALUE) {
            return -1;
        }
        return NumberInput.parseInt(str);
    }

    protected static JsonPointer _parseTail(String fullPath) {
        PointerParent parent = null;
        int i = 1;
        int end = fullPath.length();
        int startOffset = 0;
        while (i < end) {
            char c = fullPath.charAt(i);
            if (c == '/') {
                parent = new PointerParent(parent, startOffset, fullPath.substring(startOffset + 1, i));
                startOffset = i++;
                continue;
            }
            if (c != '~' || ++i >= end) continue;
            StringBuilder sb = new StringBuilder(32);
            i = JsonPointer._extractEscapedSegment(fullPath, startOffset + 1, i, sb);
            String segment = sb.toString();
            if (i < 0) {
                return JsonPointer._buildPath(fullPath, startOffset, segment, parent);
            }
            parent = new PointerParent(parent, startOffset, segment);
            startOffset = i++;
        }
        return JsonPointer._buildPath(fullPath, startOffset, fullPath.substring(startOffset + 1), parent);
    }

    private static JsonPointer _buildPath(String fullPath, int fullPathOffset, String segment, PointerParent parent) {
        JsonPointer curr = new JsonPointer(fullPath, fullPathOffset, segment, EMPTY);
        while (parent != null) {
            curr = new JsonPointer(fullPath, parent.fullPathOffset, parent.segment, curr);
            parent = parent.parent;
        }
        return curr;
    }

    protected static int _extractEscapedSegment(String input, int firstCharOffset, int i, StringBuilder sb) {
        int end = input.length();
        int toCopy = i - 1 - firstCharOffset;
        if (toCopy > 0) {
            sb.append(input, firstCharOffset, i - 1);
        }
        JsonPointer._appendEscape(sb, input.charAt(i++));
        while (i < end) {
            char c = input.charAt(i);
            if (c == '/') {
                return i;
            }
            if (c == '~' && ++i < end) {
                JsonPointer._appendEscape(sb, input.charAt(i++));
                continue;
            }
            sb.append(c);
        }
        return -1;
    }

    private static void _appendEscape(StringBuilder sb, char c) {
        if (c == '0') {
            c = (char)126;
        } else if (c == '1') {
            c = (char)47;
        } else {
            sb.append('~');
        }
        sb.append(c);
    }

    protected JsonPointer _constructHead() {
        JsonPointer last = this.last();
        if (last == this) {
            return EMPTY;
        }
        int suffixLength = last.length();
        JsonPointer next = this._nextSegment;
        String fullString = this.toString();
        return new JsonPointer(fullString.substring(0, fullString.length() - suffixLength), 0, this._matchingPropertyName, this._matchingElementIndex, next._constructHead(suffixLength, last));
    }

    protected JsonPointer _constructHead(int suffixLength, JsonPointer last) {
        if (this == last) {
            return EMPTY;
        }
        JsonPointer next = this._nextSegment;
        String str = this.toString();
        return new JsonPointer(str.substring(0, str.length() - suffixLength), 0, this._matchingPropertyName, this._matchingElementIndex, next._constructHead(suffixLength, last));
    }

    private Object writeReplace() {
        return new Serialization(this.toString());
    }

    static class Serialization
    implements Externalizable {
        private String _fullPath;

        public Serialization() {
        }

        Serialization(String fullPath) {
            this._fullPath = fullPath;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(this._fullPath);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this._fullPath = in.readUTF();
        }

        private Object readResolve() throws ObjectStreamException {
            return JsonPointer.compile(this._fullPath);
        }
    }

    private static class PointerSegment {
        public final PointerSegment next;
        public final String property;
        public final int index;
        public int pathOffset;
        public PointerSegment prev;

        public PointerSegment(PointerSegment next, String pn, int ix) {
            this.next = next;
            this.property = pn;
            this.index = ix;
            if (next != null) {
                next.prev = this;
            }
        }
    }

    private static class PointerParent {
        public final PointerParent parent;
        public final int fullPathOffset;
        public final String segment;

        PointerParent(PointerParent pp, int fpo, String sgm) {
            this.parent = pp;
            this.fullPathOffset = fpo;
            this.segment = sgm;
        }
    }
}

