/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.ParserOptions;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class FOMStAXFilter
extends XMLStreamReaderWrapper {
    private final ParserOptions parserOptions;
    private boolean ignoreWhitespace = false;
    private boolean ignoreComments = false;
    private boolean ignorePI = false;
    private int depthInSkipElement;
    private int altEventType;
    private QName altQName;
    private String altText;
    private int[] attributeMap;
    private int attributeCount;

    FOMStAXFilter(XMLStreamReader parent, ParserOptions parserOptions) {
        super(parent);
        ParseFilter parseFilter;
        this.parserOptions = parserOptions;
        if (parserOptions != null && (parseFilter = parserOptions.getParseFilter()) != null) {
            this.ignoreWhitespace = parseFilter.getIgnoreWhitespace();
            this.ignoreComments = parseFilter.getIgnoreComments();
            this.ignorePI = parseFilter.getIgnoreProcessingInstructions();
            this.attributeMap = new int[8];
        }
        this.resetEvent();
    }

    private void resetEvent() {
        this.altEventType = -1;
        this.altQName = null;
        this.altText = null;
        this.attributeCount = -1;
    }

    private void translateQName() {
        Map<QName, QName> map;
        if (this.parserOptions.isQNameAliasMappingEnabled() && (map = this.parserOptions.getQNameAliasMap()) != null) {
            this.altQName = map.get(super.getName());
        }
    }

    private void mapAttributes() {
        this.attributeCount = 0;
        int orgAttCount = super.getAttributeCount();
        if (orgAttCount > 0) {
            QName elementQName = super.getName();
            ParseFilter filter = this.parserOptions.getParseFilter();
            for (int i = 0; i < orgAttCount; ++i) {
                if (!filter.acceptable(elementQName, super.getAttributeName(i))) continue;
                if (this.attributeCount == this.attributeMap.length) {
                    int[] newAttributeMap = new int[this.attributeMap.length * 2];
                    System.arraycopy(this.attributeMap, 0, newAttributeMap, 0, this.attributeMap.length);
                    this.attributeMap = newAttributeMap;
                }
                this.attributeMap[this.attributeCount++] = i;
            }
        }
    }

    public int next() throws XMLStreamException {
        int eventType;
        this.resetEvent();
        block14: while (true) {
            eventType = super.next();
            if (this.depthInSkipElement > 0) {
                switch (eventType) {
                    case 1: {
                        ++this.depthInSkipElement;
                        break;
                    }
                    case 2: {
                        --this.depthInSkipElement;
                    }
                }
                continue;
            }
            switch (eventType) {
                case 11: {
                    continue block14;
                }
                case 1: {
                    ParseFilter filter = this.parserOptions.getParseFilter();
                    if (filter != null && !filter.acceptable(super.getName())) {
                        this.depthInSkipElement = 1;
                        continue block14;
                    }
                    this.translateQName();
                    if (this.attributeMap == null) break block14;
                    this.mapAttributes();
                    break block14;
                }
                case 2: {
                    this.translateQName();
                    break block14;
                }
                case 6: {
                    if (!this.ignoreWhitespace) break block14;
                    continue block14;
                }
                case 5: {
                    if (!this.ignoreComments) break block14;
                    continue block14;
                }
                case 3: {
                    if (!this.ignorePI) break block14;
                    continue block14;
                }
                case 4: 
                case 12: {
                    if (!this.ignoreWhitespace || !this.isWhiteSpace()) break block14;
                    continue block14;
                }
                case 9: {
                    String val = this.parserOptions.resolveEntity(this.getLocalName());
                    if (val == null) {
                        throw new ParseException("Unresolved undeclared entity: " + this.getLocalName());
                    }
                    this.altEventType = 4;
                    this.altText = val;
                }
            }
            break;
        }
        return this.altEventType != -1 ? this.altEventType : eventType;
    }

    public int getEventType() {
        return this.altEventType != -1 ? this.altEventType : super.getEventType();
    }

    public String getText() {
        return this.altText != null ? this.altText : super.getText();
    }

    public String getNamespaceURI() {
        return this.altQName != null ? this.altQName.getNamespaceURI() : super.getNamespaceURI();
    }

    public String getLocalName() {
        return this.altQName != null ? this.altQName.getLocalPart() : super.getLocalName();
    }

    public QName getName() {
        return this.altQName != null ? this.altQName : super.getName();
    }

    public int getAttributeCount() {
        return this.attributeCount != -1 ? this.attributeCount : super.getAttributeCount();
    }

    public String getAttributeNamespace(int index) {
        return this.attributeCount != -1 ? super.getAttributeNamespace(this.attributeMap[index]) : super.getAttributeNamespace(index);
    }

    public String getAttributeLocalName(int index) {
        return this.attributeCount != -1 ? super.getAttributeLocalName(this.attributeMap[index]) : super.getAttributeLocalName(index);
    }

    public String getAttributePrefix(int index) {
        return this.attributeCount != -1 ? super.getAttributePrefix(this.attributeMap[index]) : super.getAttributePrefix(index);
    }

    public QName getAttributeName(int index) {
        return this.attributeCount != -1 ? super.getAttributeName(this.attributeMap[index]) : super.getAttributeName(index);
    }

    public String getAttributeType(int index) {
        return this.attributeCount != -1 ? super.getAttributeType(this.attributeMap[index]) : super.getAttributeType(index);
    }

    public String getAttributeValue(int index) {
        return this.attributeCount != -1 ? super.getAttributeValue(this.attributeMap[index]) : super.getAttributeValue(index);
    }
}

