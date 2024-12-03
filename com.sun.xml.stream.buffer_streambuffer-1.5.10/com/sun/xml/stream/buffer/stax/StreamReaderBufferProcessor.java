/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.staxex.NamespaceContextEx
 *  org.jvnet.staxex.NamespaceContextEx$Binding
 *  org.jvnet.staxex.XMLStreamReaderEx
 */
package com.sun.xml.stream.buffer.stax;

import com.sun.xml.stream.buffer.AbstractProcessor;
import com.sun.xml.stream.buffer.AttributesHolder;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferMark;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.jvnet.staxex.NamespaceContextEx;
import org.jvnet.staxex.XMLStreamReaderEx;

public class StreamReaderBufferProcessor
extends AbstractProcessor
implements XMLStreamReaderEx {
    private static final int CACHE_SIZE = 16;
    protected ElementStackEntry[] _stack = new ElementStackEntry[16];
    protected ElementStackEntry _stackTop;
    protected int _depth;
    protected String[] _namespaceAIIsPrefix = new String[16];
    protected String[] _namespaceAIIsNamespaceName = new String[16];
    protected int _namespaceAIIsEnd;
    protected InternalNamespaceContext _nsCtx = new InternalNamespaceContext();
    protected int _eventType;
    protected AttributesHolder _attributeCache;
    protected CharSequence _charSequence;
    protected char[] _characters;
    protected int _textOffset;
    protected int _textLen;
    protected String _piTarget;
    protected String _piData;
    private static final int PARSING = 1;
    private static final int PENDING_END_DOCUMENT = 2;
    private static final int COMPLETED = 3;
    private int _completionState;

    public StreamReaderBufferProcessor() {
        for (int i = 0; i < this._stack.length; ++i) {
            this._stack[i] = new ElementStackEntry();
        }
        this._attributeCache = new AttributesHolder();
    }

    public StreamReaderBufferProcessor(XMLStreamBuffer buffer) throws XMLStreamException {
        this();
        this.setXMLStreamBuffer(buffer);
    }

    public void setXMLStreamBuffer(XMLStreamBuffer buffer) throws XMLStreamException {
        this.setBuffer(buffer, buffer.isFragment());
        this._completionState = 1;
        this._namespaceAIIsEnd = 0;
        this._characters = null;
        this._charSequence = null;
        this._eventType = 7;
    }

    public XMLStreamBuffer nextTagAndMark() throws XMLStreamException {
        do {
            int s;
            if (((s = this.peekStructure()) & 0xF0) == 32) {
                HashMap<String, String> inscope = new HashMap<String, String>(this._namespaceAIIsEnd);
                for (int i = 0; i < this._namespaceAIIsEnd; ++i) {
                    inscope.put(this._namespaceAIIsPrefix[i], this._namespaceAIIsNamespaceName[i]);
                }
                XMLStreamBufferMark mark = new XMLStreamBufferMark(inscope, this);
                this.next();
                return mark;
            }
            if ((s & 0xF0) != 16) continue;
            this.readStructure();
            XMLStreamBufferMark mark = new XMLStreamBufferMark(new HashMap<String, String>(this._namespaceAIIsEnd), this);
            this.next();
            return mark;
        } while (this.next() != 2);
        return null;
    }

    public Object getProperty(String name) {
        return null;
    }

    public int next() throws XMLStreamException {
        int eiiState;
        switch (this._completionState) {
            case 3: {
                throw new XMLStreamException("Invalid State");
            }
            case 2: {
                this._namespaceAIIsEnd = 0;
                this._completionState = 3;
                this._eventType = 8;
                return 8;
            }
        }
        switch (this._eventType) {
            case 2: {
                if (this._depth > 1) {
                    --this._depth;
                    this.popElementStack(this._depth);
                    break;
                }
                if (this._depth != 1) break;
                --this._depth;
            }
        }
        this._characters = null;
        this._charSequence = null;
        block25: while (true) {
            eiiState = this.readEiiState();
            switch (eiiState) {
                case 1: {
                    continue block25;
                }
                case 3: {
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    String prefix = this.getPrefixFromQName(this.readStructureString());
                    this.processElement(prefix, uri, localName, this.isInscope(this._depth));
                    this._eventType = 1;
                    return 1;
                }
                case 4: {
                    this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.isInscope(this._depth));
                    this._eventType = 1;
                    return 1;
                }
                case 5: {
                    this.processElement(null, this.readStructureString(), this.readStructureString(), this.isInscope(this._depth));
                    this._eventType = 1;
                    return 1;
                }
                case 6: {
                    this.processElement(null, null, this.readStructureString(), this.isInscope(this._depth));
                    this._eventType = 1;
                    return 1;
                }
                case 7: {
                    this._textLen = this.readStructure();
                    this._textOffset = this.readContentCharactersBuffer(this._textLen);
                    this._characters = this._contentCharactersBuffer;
                    this._eventType = 4;
                    return 4;
                }
                case 8: {
                    this._textLen = this.readStructure16();
                    this._textOffset = this.readContentCharactersBuffer(this._textLen);
                    this._characters = this._contentCharactersBuffer;
                    this._eventType = 4;
                    return 4;
                }
                case 9: {
                    this._characters = this.readContentCharactersCopy();
                    this._textLen = this._characters.length;
                    this._textOffset = 0;
                    this._eventType = 4;
                    return 4;
                }
                case 10: {
                    this._eventType = 4;
                    this._charSequence = this.readContentString();
                    this._eventType = 4;
                    return 4;
                }
                case 11: {
                    this._eventType = 4;
                    this._charSequence = (CharSequence)this.readContentObject();
                    this._eventType = 4;
                    return 4;
                }
                case 12: {
                    this._textLen = this.readStructure();
                    this._textOffset = this.readContentCharactersBuffer(this._textLen);
                    this._characters = this._contentCharactersBuffer;
                    this._eventType = 5;
                    return 5;
                }
                case 13: {
                    this._textLen = this.readStructure16();
                    this._textOffset = this.readContentCharactersBuffer(this._textLen);
                    this._characters = this._contentCharactersBuffer;
                    this._eventType = 5;
                    return 5;
                }
                case 14: {
                    this._characters = this.readContentCharactersCopy();
                    this._textLen = this._characters.length;
                    this._textOffset = 0;
                    this._eventType = 5;
                    return 5;
                }
                case 15: {
                    this._charSequence = this.readContentString();
                    this._eventType = 5;
                    return 5;
                }
                case 16: {
                    this._piTarget = this.readStructureString();
                    this._piData = this.readStructureString();
                    this._eventType = 3;
                    return 3;
                }
                case 17: {
                    if (this._depth > 1) {
                        this._eventType = 2;
                        return 2;
                    }
                    if (this._depth == 1) {
                        if (this._fragmentMode && --this._treeCount == 0) {
                            this._completionState = 2;
                        }
                        this._eventType = 2;
                        return 2;
                    }
                    this._namespaceAIIsEnd = 0;
                    this._completionState = 3;
                    this._eventType = 8;
                    return 8;
                }
            }
            break;
        }
        throw new XMLStreamException("Internal XSB error: Invalid State=" + eiiState);
    }

    public final void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (type != this._eventType) {
            throw new XMLStreamException("");
        }
        if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
            throw new XMLStreamException("");
        }
        if (localName != null && !localName.equals(this.getLocalName())) {
            throw new XMLStreamException("");
        }
    }

    public final String getElementTextTrim() throws XMLStreamException {
        return this.getElementText().trim();
    }

    public final String getElementText() throws XMLStreamException {
        if (this._eventType != 1) {
            throw new XMLStreamException("");
        }
        this.next();
        return this.getElementText(true);
    }

    public final String getElementText(boolean startElementRead) throws XMLStreamException {
        if (!startElementRead) {
            throw new XMLStreamException("");
        }
        int eventType = this.getEventType();
        StringBuilder content = new StringBuilder();
        while (eventType != 2) {
            if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
                content.append(this.getText());
            } else if (eventType != 3 && eventType != 5) {
                if (eventType == 8) {
                    throw new XMLStreamException("");
                }
                if (eventType == 1) {
                    throw new XMLStreamException("");
                }
                throw new XMLStreamException("");
            }
            eventType = this.next();
        }
        return content.toString();
    }

    public final int nextTag() throws XMLStreamException {
        this.next();
        return this.nextTag(true);
    }

    public final int nextTag(boolean currentTagRead) throws XMLStreamException {
        int eventType = this.getEventType();
        if (!currentTagRead) {
            eventType = this.next();
        }
        while (eventType == 4 && this.isWhiteSpace() || eventType == 12 && this.isWhiteSpace() || eventType == 6 || eventType == 3 || eventType == 5) {
            eventType = this.next();
        }
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException("");
        }
        return eventType;
    }

    public final boolean hasNext() {
        return this._eventType != 8;
    }

    public void close() throws XMLStreamException {
    }

    public final boolean isStartElement() {
        return this._eventType == 1;
    }

    public final boolean isEndElement() {
        return this._eventType == 2;
    }

    public final boolean isCharacters() {
        return this._eventType == 4;
    }

    public final boolean isWhiteSpace() {
        if (this.isCharacters() || this._eventType == 12) {
            char[] ch = this.getTextCharacters();
            int start = this.getTextStart();
            int length = this.getTextLength();
            for (int i = start; i < length; ++i) {
                char c = ch[i];
                if (c == ' ' || c == '\t' || c == '\r' || c == '\n') continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public final String getAttributeValue(String namespaceURI, String localName) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        return this._attributeCache.getValue(namespaceURI, localName);
    }

    public final int getAttributeCount() {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return this._attributeCache.getLength();
    }

    public final QName getAttributeName(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        String prefix = this._attributeCache.getPrefix(index);
        String localName = this._attributeCache.getLocalName(index);
        String uri = this._attributeCache.getURI(index);
        return new QName(uri, localName, prefix);
    }

    public final String getAttributeNamespace(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return StreamReaderBufferProcessor.fixEmptyString(this._attributeCache.getURI(index));
    }

    public final String getAttributeLocalName(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return this._attributeCache.getLocalName(index);
    }

    public final String getAttributePrefix(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return StreamReaderBufferProcessor.fixEmptyString(this._attributeCache.getPrefix(index));
    }

    public final String getAttributeType(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return this._attributeCache.getType(index);
    }

    public final String getAttributeValue(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException("");
        }
        return this._attributeCache.getValue(index);
    }

    public final boolean isAttributeSpecified(int index) {
        return false;
    }

    public final int getNamespaceCount() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._stackTop.namespaceAIIsEnd - this._stackTop.namespaceAIIsStart;
        }
        throw new IllegalStateException("");
    }

    public final String getNamespacePrefix(int index) {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._namespaceAIIsPrefix[this._stackTop.namespaceAIIsStart + index];
        }
        throw new IllegalStateException("");
    }

    public final String getNamespaceURI(int index) {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._namespaceAIIsNamespaceName[this._stackTop.namespaceAIIsStart + index];
        }
        throw new IllegalStateException("");
    }

    public final String getNamespaceURI(String prefix) {
        return this._nsCtx.getNamespaceURI(prefix);
    }

    public final NamespaceContextEx getNamespaceContext() {
        return this._nsCtx;
    }

    public final int getEventType() {
        return this._eventType;
    }

    public final String getText() {
        if (this._characters != null) {
            String s = new String(this._characters, this._textOffset, this._textLen);
            this._charSequence = s;
            return s;
        }
        if (this._charSequence != null) {
            return this._charSequence.toString();
        }
        throw new IllegalStateException();
    }

    public final char[] getTextCharacters() {
        if (this._characters != null) {
            return this._characters;
        }
        if (this._charSequence != null) {
            this._characters = this._charSequence.toString().toCharArray();
            this._textLen = this._characters.length;
            this._textOffset = 0;
            return this._characters;
        }
        throw new IllegalStateException();
    }

    public final int getTextStart() {
        if (this._characters != null) {
            return this._textOffset;
        }
        if (this._charSequence != null) {
            return 0;
        }
        throw new IllegalStateException();
    }

    public final int getTextLength() {
        if (this._characters != null) {
            return this._textLen;
        }
        if (this._charSequence != null) {
            return this._charSequence.length();
        }
        throw new IllegalStateException();
    }

    public final int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        if (this._characters == null) {
            if (this._charSequence != null) {
                this._characters = this._charSequence.toString().toCharArray();
                this._textLen = this._characters.length;
                this._textOffset = 0;
            } else {
                throw new IllegalStateException("");
            }
        }
        try {
            int remaining = this._textLen - sourceStart;
            int len = remaining > length ? length : remaining;
            System.arraycopy(this._characters, sourceStart += this._textOffset, target, targetStart, len);
            return len;
        }
        catch (IndexOutOfBoundsException e) {
            throw new XMLStreamException(e);
        }
    }

    public final CharSequence getPCDATA() {
        if (this._characters != null) {
            return new CharSequenceImpl(this._textOffset, this._textLen);
        }
        if (this._charSequence != null) {
            return this._charSequence;
        }
        throw new IllegalStateException();
    }

    public final String getEncoding() {
        return "UTF-8";
    }

    public final boolean hasText() {
        return this._characters != null || this._charSequence != null;
    }

    public final Location getLocation() {
        return new DummyLocation();
    }

    public final boolean hasName() {
        return this._eventType == 1 || this._eventType == 2;
    }

    public final QName getName() {
        return this._stackTop.getQName();
    }

    public final String getLocalName() {
        return this._stackTop.localName;
    }

    public final String getNamespaceURI() {
        return this._stackTop.uri;
    }

    public final String getPrefix() {
        return this._stackTop.prefix;
    }

    public final String getVersion() {
        return "1.0";
    }

    public final boolean isStandalone() {
        return false;
    }

    public final boolean standaloneSet() {
        return false;
    }

    public final String getCharacterEncodingScheme() {
        return "UTF-8";
    }

    public final String getPITarget() {
        if (this._eventType == 3) {
            return this._piTarget;
        }
        throw new IllegalStateException("");
    }

    public final String getPIData() {
        if (this._eventType == 3) {
            return this._piData;
        }
        throw new IllegalStateException("");
    }

    protected void processElement(String prefix, String uri, String localName, boolean inscope) {
        this.pushElementStack();
        this._stackTop.set(prefix, uri, localName);
        this._attributeCache.clear();
        int item = this.peekStructure();
        if ((item & 0xF0) == 64 || inscope) {
            item = this.processNamespaceAttributes(item, inscope);
        }
        if ((item & 0xF0) == 48) {
            this.processAttributes(item);
        }
    }

    private boolean isInscope(int depth) {
        return this._buffer.getInscopeNamespaces().size() > 0 && depth == 0;
    }

    private void resizeNamespaceAttributes() {
        String[] namespaceAIIsPrefix = new String[this._namespaceAIIsEnd * 2];
        System.arraycopy(this._namespaceAIIsPrefix, 0, namespaceAIIsPrefix, 0, this._namespaceAIIsEnd);
        this._namespaceAIIsPrefix = namespaceAIIsPrefix;
        String[] namespaceAIIsNamespaceName = new String[this._namespaceAIIsEnd * 2];
        System.arraycopy(this._namespaceAIIsNamespaceName, 0, namespaceAIIsNamespaceName, 0, this._namespaceAIIsEnd);
        this._namespaceAIIsNamespaceName = namespaceAIIsNamespaceName;
    }

    private int processNamespaceAttributes(int item, boolean inscope) {
        HashSet<String> prefixSet;
        this._stackTop.namespaceAIIsStart = this._namespaceAIIsEnd;
        HashSet<String> hashSet = prefixSet = inscope ? new HashSet<String>() : Collections.emptySet();
        while ((item & 0xF0) == 64) {
            if (this._namespaceAIIsEnd == this._namespaceAIIsPrefix.length) {
                this.resizeNamespaceAttributes();
            }
            switch (StreamReaderBufferProcessor.getNIIState(item)) {
                case 1: {
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = "";
                    this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = "";
                    if (!inscope) break;
                    prefixSet.add("");
                    break;
                }
                case 2: {
                    this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = this.readStructureString();
                    if (inscope) {
                        prefixSet.add(this._namespaceAIIsPrefix[this._namespaceAIIsEnd]);
                    }
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = "";
                    break;
                }
                case 3: {
                    this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = this.readStructureString();
                    if (inscope) {
                        prefixSet.add(this._namespaceAIIsPrefix[this._namespaceAIIsEnd]);
                    }
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = this.readStructureString();
                    break;
                }
                case 4: {
                    this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = "";
                    if (inscope) {
                        prefixSet.add("");
                    }
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = this.readStructureString();
                }
            }
            this.readStructure();
            item = this.peekStructure();
        }
        if (inscope) {
            for (Map.Entry<String, String> e : this._buffer.getInscopeNamespaces().entrySet()) {
                String key = StreamReaderBufferProcessor.fixNull(e.getKey());
                if (prefixSet.contains(key)) continue;
                if (this._namespaceAIIsEnd == this._namespaceAIIsPrefix.length) {
                    this.resizeNamespaceAttributes();
                }
                this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = key;
                this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = e.getValue();
            }
        }
        this._stackTop.namespaceAIIsEnd = this._namespaceAIIsEnd;
        return item;
    }

    private static String fixNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    private void processAttributes(int item) {
        do {
            switch (StreamReaderBufferProcessor.getAIIState(item)) {
                case 1: {
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    String prefix = this.getPrefixFromQName(this.readStructureString());
                    this._attributeCache.addAttributeWithPrefix(prefix, uri, localName, this.readStructureString(), this.readContentString());
                    break;
                }
                case 2: {
                    this._attributeCache.addAttributeWithPrefix(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 3: {
                    this._attributeCache.addAttributeWithPrefix("", this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 4: {
                    this._attributeCache.addAttributeWithPrefix("", "", this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                default: {
                    assert (false) : "Internal XSB Error: wrong attribute state, Item=" + item;
                    break;
                }
            }
            this.readStructure();
        } while (((item = this.peekStructure()) & 0xF0) == 48);
    }

    private void pushElementStack() {
        if (this._depth == this._stack.length) {
            ElementStackEntry[] tmp = this._stack;
            this._stack = new ElementStackEntry[this._stack.length * 3 / 2 + 1];
            System.arraycopy(tmp, 0, this._stack, 0, tmp.length);
            for (int i = tmp.length; i < this._stack.length; ++i) {
                this._stack[i] = new ElementStackEntry();
            }
        }
        this._stackTop = this._stack[this._depth++];
    }

    private void popElementStack(int depth) {
        this._stackTop = this._stack[depth - 1];
        this._namespaceAIIsEnd = this._stack[depth].namespaceAIIsStart;
    }

    private static String fixEmptyString(String s) {
        if (s.length() == 0) {
            return null;
        }
        return s;
    }

    private class DummyLocation
    implements Location {
        private DummyLocation() {
        }

        @Override
        public int getLineNumber() {
            return -1;
        }

        @Override
        public int getColumnNumber() {
            return -1;
        }

        @Override
        public int getCharacterOffset() {
            return -1;
        }

        @Override
        public String getPublicId() {
            return null;
        }

        @Override
        public String getSystemId() {
            return StreamReaderBufferProcessor.this._buffer.getSystemId();
        }
    }

    private final class InternalNamespaceContext
    implements NamespaceContextEx {
        private InternalNamespaceContext() {
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix cannot be null");
            }
            if (StreamReaderBufferProcessor.this._stringInterningFeature) {
                prefix = prefix.intern();
                for (int i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1; i >= 0; --i) {
                    if (prefix != StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i]) continue;
                    return StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[i];
                }
            } else {
                for (int i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1; i >= 0; --i) {
                    if (!prefix.equals(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i])) continue;
                    return StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[i];
                }
            }
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (prefix.equals("xmlns")) {
                return "http://www.w3.org/2000/xmlns/";
            }
            return null;
        }

        public String getPrefix(String namespaceURI) {
            Iterator i = this.getPrefixes(namespaceURI);
            if (i.hasNext()) {
                return (String)i.next();
            }
            return null;
        }

        public Iterator getPrefixes(final String namespaceURI) {
            if (namespaceURI == null) {
                throw new IllegalArgumentException("NamespaceURI cannot be null");
            }
            if (namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
                return Collections.singletonList("xml").iterator();
            }
            if (namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                return Collections.singletonList("xmlns").iterator();
            }
            return new Iterator(){
                private int i;
                private boolean requireFindNext;
                private String p;
                {
                    this.i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1;
                    this.requireFindNext = true;
                }

                private String findNext() {
                    while (this.i >= 0) {
                        if (namespaceURI.equals(StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.i]) && InternalNamespaceContext.this.getNamespaceURI(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.i]).equals(StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.i])) {
                            this.p = StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.i];
                            return this.p;
                        }
                        --this.i;
                    }
                    this.p = null;
                    return null;
                }

                @Override
                public boolean hasNext() {
                    if (this.requireFindNext) {
                        this.findNext();
                        this.requireFindNext = false;
                    }
                    return this.p != null;
                }

                public Object next() {
                    if (this.requireFindNext) {
                        this.findNext();
                    }
                    this.requireFindNext = true;
                    if (this.p == null) {
                        throw new NoSuchElementException();
                    }
                    return this.p;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public Iterator<NamespaceContextEx.Binding> iterator() {
            return new Iterator<NamespaceContextEx.Binding>(){
                private final int end;
                private int current;
                private boolean requireFindNext;
                private NamespaceContextEx.Binding namespace;
                {
                    this.current = this.end = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1;
                    this.requireFindNext = true;
                }

                private NamespaceContextEx.Binding findNext() {
                    while (this.current >= 0) {
                        int i;
                        String prefix = StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.current];
                        for (i = this.end; i > this.current && !prefix.equals(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i]); --i) {
                        }
                        if (i != this.current--) continue;
                        this.namespace = new BindingImpl(prefix, StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.current]);
                        return this.namespace;
                    }
                    this.namespace = null;
                    return null;
                }

                @Override
                public boolean hasNext() {
                    if (this.requireFindNext) {
                        this.findNext();
                        this.requireFindNext = false;
                    }
                    return this.namespace != null;
                }

                @Override
                public NamespaceContextEx.Binding next() {
                    if (this.requireFindNext) {
                        this.findNext();
                    }
                    this.requireFindNext = true;
                    if (this.namespace == null) {
                        throw new NoSuchElementException();
                    }
                    return this.namespace;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        private class BindingImpl
        implements NamespaceContextEx.Binding {
            final String _prefix;
            final String _namespaceURI;

            BindingImpl(String prefix, String namespaceURI) {
                this._prefix = prefix;
                this._namespaceURI = namespaceURI;
            }

            public String getPrefix() {
                return this._prefix;
            }

            public String getNamespaceURI() {
                return this._namespaceURI;
            }
        }
    }

    private final class ElementStackEntry {
        String prefix;
        String uri;
        String localName;
        QName qname;
        int namespaceAIIsStart;
        int namespaceAIIsEnd;

        private ElementStackEntry() {
        }

        public void set(String prefix, String uri, String localName) {
            this.prefix = prefix;
            this.uri = uri;
            this.localName = localName;
            this.qname = null;
            this.namespaceAIIsStart = this.namespaceAIIsEnd = StreamReaderBufferProcessor.this._namespaceAIIsEnd;
        }

        public QName getQName() {
            if (this.qname == null) {
                this.qname = new QName(this.fixNull(this.uri), this.localName, this.fixNull(this.prefix));
            }
            return this.qname;
        }

        private String fixNull(String s) {
            return s == null ? "" : s;
        }
    }

    private class CharSequenceImpl
    implements CharSequence {
        private final int _offset;
        private final int _length;

        CharSequenceImpl(int offset, int length) {
            this._offset = offset;
            this._length = length;
        }

        @Override
        public int length() {
            return this._length;
        }

        @Override
        public char charAt(int index) {
            if (index >= 0 && index < StreamReaderBufferProcessor.this._textLen) {
                return StreamReaderBufferProcessor.this._characters[StreamReaderBufferProcessor.this._textOffset + index];
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            int length = end - start;
            if (end < 0 || start < 0 || end > length || start > end) {
                throw new IndexOutOfBoundsException();
            }
            return new CharSequenceImpl(this._offset + start, length);
        }

        @Override
        public String toString() {
            return new String(StreamReaderBufferProcessor.this._characters, this._offset, this._length);
        }
    }
}

