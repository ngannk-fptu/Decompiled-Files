/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.sr.Attribute;
import com.ctc.wstx.sr.ElemAttrs;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.sw.XmlWriter;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.InternCache;
import com.ctc.wstx.util.StringUtil;
import com.ctc.wstx.util.StringVector;
import com.ctc.wstx.util.TextBuilder;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.CharArrayBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.XMLValidator;

public final class AttributeCollector {
    static final int INT_SPACE = 32;
    protected static final int LONG_ATTR_LIST_LEN = 4;
    protected static final int EXP_ATTR_COUNT = 12;
    protected static final int EXP_NS_COUNT = 6;
    protected static final int XMLID_IX_DISABLED = -2;
    protected static final int XMLID_IX_NONE = -1;
    protected static final InternCache sInternCache = InternCache.getInstance();
    final String mXmlIdPrefix;
    final String mXmlIdLocalName;
    protected Attribute[] mAttributes;
    protected int mAttrCount;
    protected int mNonDefCount;
    protected Attribute[] mNamespaces;
    protected int mNsCount;
    protected boolean mDefaultNsDeclared = false;
    protected int mXmlIdAttrIndex;
    protected TextBuilder mValueBuilder = null;
    private final TextBuilder mNamespaceBuilder = new TextBuilder(6);
    protected int[] mAttrMap = null;
    protected int mAttrHashSize;
    protected int mAttrSpillEnd;
    protected int mMaxAttributesPerElement;
    protected int mMaxAttributeSize;

    protected AttributeCollector(ReaderConfig cfg, boolean nsAware) {
        int n = this.mXmlIdAttrIndex = cfg.willDoXmlIdTyping() ? -1 : -2;
        if (nsAware) {
            this.mXmlIdPrefix = "xml";
            this.mXmlIdLocalName = "id";
        } else {
            this.mXmlIdPrefix = null;
            this.mXmlIdLocalName = "xml:id";
        }
        this.mMaxAttributesPerElement = cfg.getMaxAttributesPerElement();
        this.mMaxAttributeSize = cfg.getMaxAttributeSize();
    }

    public void reset() {
        if (this.mNsCount > 0) {
            this.mNamespaceBuilder.reset();
            this.mDefaultNsDeclared = false;
            this.mNsCount = 0;
        }
        if (this.mAttrCount > 0) {
            this.mValueBuilder.reset();
            this.mAttrCount = 0;
            if (this.mXmlIdAttrIndex >= 0) {
                this.mXmlIdAttrIndex = -1;
            }
        }
    }

    public void normalizeSpacesInValue(int index) {
        char[] attrCB = this.mValueBuilder.getCharBuffer();
        String normValue = StringUtil.normalizeSpaces(attrCB, this.getValueStartOffset(index), this.getValueStartOffset(index + 1));
        if (normValue != null) {
            this.mAttributes[index].setValue(normValue);
        }
    }

    protected int getNsCount() {
        return this.mNsCount;
    }

    public boolean hasDefaultNs() {
        return this.mDefaultNsDeclared;
    }

    public final int getCount() {
        return this.mAttrCount;
    }

    public int getSpecifiedCount() {
        return this.mNonDefCount;
    }

    public String getNsPrefix(int index) {
        if (index < 0 || index >= this.mNsCount) {
            this.throwIndex(index);
        }
        return this.mNamespaces[index].mLocalName;
    }

    public String getNsURI(int index) {
        if (index < 0 || index >= this.mNsCount) {
            this.throwIndex(index);
        }
        return this.mNamespaces[index].mNamespaceURI;
    }

    public String getPrefix(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return this.mAttributes[index].mPrefix;
    }

    public String getLocalName(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return this.mAttributes[index].mLocalName;
    }

    public String getURI(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return this.mAttributes[index].mNamespaceURI;
    }

    public QName getQName(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return this.mAttributes[index].getQName();
    }

    public final String getValue(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        String full = this.mValueBuilder.getAllValues();
        Attribute attr = this.mAttributes[index];
        if (++index < this.mAttrCount) {
            int endOffset = this.mAttributes[index].mValueStartOffset;
            return attr.getValue(full, endOffset);
        }
        return attr.getValue(full);
    }

    public String getValue(String nsURI, String localName) {
        int ix;
        int hashSize = this.mAttrHashSize;
        if (hashSize == 0) {
            return null;
        }
        int hash = localName.hashCode();
        if (nsURI != null) {
            if (nsURI.length() == 0) {
                nsURI = null;
            } else {
                hash ^= nsURI.hashCode();
            }
        }
        if ((ix = this.mAttrMap[hash & hashSize - 1]) == 0) {
            return null;
        }
        if (this.mAttributes[--ix].hasQName(nsURI, localName)) {
            return this.getValue(ix);
        }
        int len = this.mAttrSpillEnd;
        for (int i = hashSize; i < len; i += 2) {
            if (this.mAttrMap[i] != hash || !this.mAttributes[ix = this.mAttrMap[i + 1]].hasQName(nsURI, localName)) continue;
            return this.getValue(ix);
        }
        return null;
    }

    public int getMaxAttributesPerElement() {
        return this.mMaxAttributesPerElement;
    }

    public void setMaxAttributesPerElement(int maxAttributesPerElement) {
        this.mMaxAttributesPerElement = maxAttributesPerElement;
    }

    public int findIndex(String localName) {
        return this.findIndex(null, localName);
    }

    public int findIndex(String nsURI, String localName) {
        int ix;
        int hashSize = this.mAttrHashSize;
        if (hashSize == 0) {
            return -1;
        }
        int hash = localName.hashCode();
        if (nsURI != null) {
            if (nsURI.length() == 0) {
                nsURI = null;
            } else {
                hash ^= nsURI.hashCode();
            }
        }
        if ((ix = this.mAttrMap[hash & hashSize - 1]) == 0) {
            return -1;
        }
        if (this.mAttributes[--ix].hasQName(nsURI, localName)) {
            return ix;
        }
        int len = this.mAttrSpillEnd;
        for (int i = hashSize; i < len; i += 2) {
            if (this.mAttrMap[i] != hash || !this.mAttributes[ix = this.mAttrMap[i + 1]].hasQName(nsURI, localName)) continue;
            return ix;
        }
        return -1;
    }

    public final boolean isSpecified(int index) {
        return index < this.mNonDefCount;
    }

    public final int getXmlIdAttrIndex() {
        return this.mXmlIdAttrIndex;
    }

    public final void decodeValue(int index, TypedValueDecoder tvd) throws IllegalArgumentException {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        char[] buf = this.mValueBuilder.getCharBuffer();
        int start = this.mAttributes[index].mValueStartOffset;
        int end = this.getValueStartOffset(index + 1);
        while (true) {
            if (start >= end) {
                tvd.handleEmptyValue();
                return;
            }
            if (!StringUtil.isSpace(buf[start])) break;
            ++start;
        }
        while (--end > start && StringUtil.isSpace(buf[end])) {
        }
        tvd.decode(buf, start, end + 1);
    }

    public final int decodeValues(int index, TypedArrayDecoder tad, InputProblemReporter rep) throws XMLStreamException {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return AttributeCollector.decodeValues(tad, rep, this.mValueBuilder.getCharBuffer(), this.mAttributes[index].mValueStartOffset, this.getValueStartOffset(index + 1));
    }

    public final byte[] decodeBinary(int index, Base64Variant v, CharArrayBase64Decoder dec, InputProblemReporter rep) throws XMLStreamException {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        Attribute attr = this.mAttributes[index];
        char[] cbuf = this.mValueBuilder.getCharBuffer();
        int start = attr.mValueStartOffset;
        int end = this.getValueStartOffset(index + 1);
        int len = end - start;
        dec.init(v, true, cbuf, start, len, null);
        try {
            return dec.decodeCompletely();
        }
        catch (IllegalArgumentException iae) {
            String lexical = new String(cbuf, start, len);
            throw new TypedXMLStreamException(lexical, iae.getMessage(), rep.getLocation(), iae);
        }
    }

    private static final int decodeValues(TypedArrayDecoder tad, InputProblemReporter rep, char[] buf, int ptr, int end) throws XMLStreamException {
        int count;
        block5: {
            int start = ptr;
            count = 0;
            try {
                while (ptr < end) {
                    while (buf[ptr] <= ' ') {
                        if (++ptr < end) continue;
                        break block5;
                    }
                    start = ptr++;
                    while (ptr < end && buf[ptr] > ' ') {
                        ++ptr;
                    }
                    int tokenEnd = ptr++;
                    ++count;
                    if (!tad.decodeValue(buf, start, tokenEnd) || AttributeCollector.checkExpand(tad)) continue;
                    break;
                }
            }
            catch (IllegalArgumentException iae) {
                Location loc = rep.getLocation();
                String lexical = new String(buf, start, ptr - start);
                throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
            }
        }
        return count;
    }

    private static final boolean checkExpand(TypedArrayDecoder tad) {
        if (tad instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder)tad).expand();
            return true;
        }
        return false;
    }

    protected int getValueStartOffset(int index) {
        if (index < this.mAttrCount) {
            return this.mAttributes[index].mValueStartOffset;
        }
        return this.mValueBuilder.getCharSize();
    }

    protected char[] getSharedValueBuffer() {
        return this.mValueBuilder.getCharBuffer();
    }

    protected Attribute resolveNamespaceDecl(int index, boolean internURI) {
        String uri;
        Attribute ns = this.mNamespaces[index];
        String full = this.mNamespaceBuilder.getAllValues();
        if (this.mNsCount == 0) {
            uri = full;
        } else if (++index < this.mNsCount) {
            int endOffset = this.mNamespaces[index].mValueStartOffset;
            uri = ns.getValue(full, endOffset);
        } else {
            uri = ns.getValue(full);
        }
        if (internURI && uri.length() > 0) {
            uri = sInternCache.intern(uri);
        }
        ns.mNamespaceURI = uri;
        return ns;
    }

    public ElemAttrs buildAttrOb() {
        int count = this.mAttrCount;
        if (count == 0) {
            return null;
        }
        String[] raw = new String[count << 2];
        for (int i = 0; i < count; ++i) {
            Attribute attr = this.mAttributes[i];
            int ix = i << 2;
            raw[ix] = attr.mLocalName;
            raw[ix + 1] = attr.mNamespaceURI;
            raw[ix + 2] = attr.mPrefix;
            raw[ix + 3] = this.getValue(i);
        }
        if (count < 4) {
            return new ElemAttrs(raw, this.mNonDefCount);
        }
        int amapLen = this.mAttrMap.length;
        int[] amap = new int[amapLen];
        System.arraycopy(this.mAttrMap, 0, amap, 0, amapLen);
        return new ElemAttrs(raw, this.mNonDefCount, amap, this.mAttrHashSize, this.mAttrSpillEnd);
    }

    protected void validateAttribute(int index, XMLValidator vld) throws XMLStreamException {
        Attribute attr = this.mAttributes[index];
        String normValue = vld.validateAttribute(attr.mLocalName, attr.mNamespaceURI, attr.mPrefix, this.mValueBuilder.getCharBuffer(), this.getValueStartOffset(index), this.getValueStartOffset(index + 1));
        if (normValue != null) {
            attr.setValue(normValue);
        }
    }

    public final TextBuilder getAttrBuilder(String attrPrefix, String attrLocalName) throws XMLStreamException {
        if (this.mAttrCount == 0) {
            if (this.mAttributes == null) {
                this.allocBuffers();
            }
            this.mAttributes[0] = new Attribute(attrPrefix, attrLocalName, 0);
        } else {
            Attribute curr;
            int valueStart = this.mValueBuilder.getCharSize();
            if (this.mAttrCount >= this.mAttributes.length) {
                if (this.mAttrCount + this.mNsCount >= this.mMaxAttributesPerElement) {
                    throw new XMLStreamException("Attribute limit (" + this.mMaxAttributesPerElement + ") exceeded");
                }
                this.mAttributes = (Attribute[])DataUtil.growArrayBy50Pct(this.mAttributes);
            }
            if ((curr = this.mAttributes[this.mAttrCount]) == null) {
                this.mAttributes[this.mAttrCount] = new Attribute(attrPrefix, attrLocalName, valueStart);
            } else {
                curr.reset(attrPrefix, attrLocalName, valueStart);
            }
        }
        ++this.mAttrCount;
        if (attrLocalName == this.mXmlIdLocalName && attrPrefix == this.mXmlIdPrefix && this.mXmlIdAttrIndex != -2) {
            this.mXmlIdAttrIndex = this.mAttrCount - 1;
        }
        return this.mValueBuilder;
    }

    public int addDefaultAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        int index;
        int[] map;
        int attrIndex = this.mAttrCount;
        if (attrIndex < 1) {
            this.initHashArea();
        }
        int hash = localName.hashCode();
        if (uri != null && uri.length() > 0) {
            hash ^= uri.hashCode();
        }
        if ((map = this.mAttrMap)[index = hash & this.mAttrHashSize - 1] == 0) {
            map[index] = attrIndex + 1;
        } else {
            int currIndex = map[index] - 1;
            int spillIndex = this.mAttrSpillEnd;
            if ((map = this.spillAttr(uri, localName, map, currIndex, spillIndex, attrIndex, hash, this.mAttrHashSize)) == null) {
                return -1;
            }
            map[++spillIndex] = attrIndex;
            this.mAttrMap = map;
            this.mAttrSpillEnd = ++spillIndex;
        }
        this.getAttrBuilder(prefix, localName);
        Attribute attr = this.mAttributes[this.mAttrCount - 1];
        attr.mNamespaceURI = uri;
        attr.setValue(value);
        return this.mAttrCount - 1;
    }

    public final void setNormalizedValue(int index, String value) {
        this.mAttributes[index].setValue(value);
    }

    public TextBuilder getDefaultNsBuilder() throws XMLStreamException {
        if (this.mDefaultNsDeclared) {
            return null;
        }
        this.mDefaultNsDeclared = true;
        return this.getNsBuilder(null);
    }

    public TextBuilder getNsBuilder(String prefix) throws XMLStreamException {
        if (this.mNsCount == 0) {
            if (this.mNamespaces == null) {
                this.mNamespaces = new Attribute[6];
            }
            this.mNamespaces[0] = new Attribute(null, prefix, 0);
        } else {
            int len = this.mNsCount;
            if (prefix != null) {
                for (int i = 0; i < len; ++i) {
                    if (prefix != this.mNamespaces[i].mLocalName) continue;
                    return null;
                }
            }
            if (len >= this.mNamespaces.length) {
                if (this.mAttrCount + this.mNsCount >= this.mMaxAttributesPerElement) {
                    throw new XMLStreamException("Attribute limit (" + this.mMaxAttributesPerElement + ") exceeded");
                }
                this.mNamespaces = (Attribute[])DataUtil.growArrayBy50Pct(this.mNamespaces);
            }
            int uriStart = this.mNamespaceBuilder.getCharSize();
            Attribute curr = this.mNamespaces[len];
            if (curr == null) {
                this.mNamespaces[len] = new Attribute(null, prefix, uriStart);
            } else {
                curr.reset(null, prefix, uriStart);
            }
        }
        ++this.mNsCount;
        return this.mNamespaceBuilder;
    }

    public int resolveNamespaces(InputProblemReporter rep, StringVector ns) throws XMLStreamException {
        int hashCount;
        int attrCount;
        this.mNonDefCount = attrCount = this.mAttrCount;
        if (attrCount < 1) {
            this.mAttrSpillEnd = 0;
            this.mAttrHashSize = 0;
            return this.mXmlIdAttrIndex;
        }
        for (int i = 0; i < attrCount; ++i) {
            Attribute attr = this.mAttributes[i];
            String prefix = attr.mPrefix;
            if (prefix == null) continue;
            if (prefix == "xml") {
                attr.mNamespaceURI = "http://www.w3.org/XML/1998/namespace";
                continue;
            }
            String uri = ns.findLastFromMap(prefix);
            if (uri == null) {
                rep.throwParseError(ErrorConsts.ERR_NS_UNDECLARED_FOR_ATTR, prefix, attr.mLocalName);
            }
            attr.mNamespaceURI = uri;
        }
        int[] map = this.mAttrMap;
        int min = attrCount + (attrCount >> 2);
        for (hashCount = 4; hashCount < min; hashCount += hashCount) {
        }
        this.mAttrHashSize = hashCount;
        min = hashCount + (hashCount >> 4);
        if (map == null || map.length < min) {
            map = new int[min];
        } else {
            Arrays.fill(map, 0, hashCount, 0);
        }
        int mask = hashCount - 1;
        int spillIndex = hashCount;
        for (int i = 0; i < attrCount; ++i) {
            int index;
            Attribute attr = this.mAttributes[i];
            String name = attr.mLocalName;
            int hash = name.hashCode();
            String uri = attr.mNamespaceURI;
            if (uri != null) {
                hash ^= uri.hashCode();
            }
            if (map[index = hash & mask] == 0) {
                map[index] = i + 1;
                continue;
            }
            int currIndex = map[index] - 1;
            if ((map = this.spillAttr(uri, name, map, currIndex, spillIndex, attrCount, hash, hashCount)) == null) {
                this.throwDupAttr(rep, currIndex);
                continue;
            }
            map[++spillIndex] = i;
            ++spillIndex;
        }
        this.mAttrSpillEnd = spillIndex;
        this.mAttrMap = map;
        return this.mXmlIdAttrIndex;
    }

    protected void throwIndex(int index) {
        throw new IllegalArgumentException("Invalid index " + index + "; current element has only " + this.getCount() + " attributes");
    }

    public void writeAttribute(int index, XmlWriter xw) throws IOException, XMLStreamException {
        Attribute attr = this.mAttributes[index];
        String ln = attr.mLocalName;
        String prefix = attr.mPrefix;
        if (prefix == null || prefix.length() == 0) {
            xw.writeAttribute(ln, this.getValue(index));
        } else {
            xw.writeAttribute(prefix, ln, this.getValue(index));
        }
    }

    protected final void allocBuffers() {
        if (this.mAttributes == null) {
            this.mAttributes = new Attribute[8];
        }
        if (this.mValueBuilder == null) {
            this.mValueBuilder = new TextBuilder(12);
        }
    }

    private int[] spillAttr(String uri, String name, int[] map, int currIndex, int spillIndex, int attrCount, int hash, int hashCount) {
        String currURI;
        Attribute oldAttr = this.mAttributes[currIndex];
        if (oldAttr.mLocalName == name && ((currURI = oldAttr.mNamespaceURI) == uri || currURI != null && currURI.equals(uri))) {
            return null;
        }
        if (spillIndex + 1 >= map.length) {
            map = DataUtil.growArrayBy(map, 8);
        }
        for (int j = hashCount; j < spillIndex; j += 2) {
            String currURI2;
            if (map[j] != hash) continue;
            currIndex = map[j + 1];
            Attribute attr = this.mAttributes[currIndex];
            if (attr.mLocalName != name || (currURI2 = attr.mNamespaceURI) != uri && (currURI2 == null || !currURI2.equals(uri))) continue;
            return null;
        }
        map[spillIndex] = hash;
        return map;
    }

    private void initHashArea() {
        this.mAttrSpillEnd = 4;
        this.mAttrHashSize = 4;
        if (this.mAttrMap == null || this.mAttrMap.length < this.mAttrHashSize) {
            this.mAttrMap = new int[this.mAttrHashSize + 1];
        }
        this.mAttrMap[3] = 0;
        this.mAttrMap[2] = 0;
        this.mAttrMap[1] = 0;
        this.mAttrMap[0] = 0;
        this.allocBuffers();
    }

    protected void throwDupAttr(InputProblemReporter rep, int index) throws XMLStreamException {
        rep.throwParseError("Duplicate attribute '" + this.getQName(index) + "'.");
    }
}

