/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.stream.buffer;

import com.sun.xml.stream.buffer.AbstractCreatorProcessor;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import java.util.Map;

public class XMLStreamBufferMark
extends XMLStreamBuffer {
    public XMLStreamBufferMark(Map<String, String> inscopeNamespaces, AbstractCreatorProcessor src) {
        if (inscopeNamespaces != null) {
            this._inscopeNamespaces = inscopeNamespaces;
        }
        this._structure = src._currentStructureFragment;
        this._structurePtr = src._structurePtr;
        this._structureStrings = src._currentStructureStringFragment;
        this._structureStringsPtr = src._structureStringsPtr;
        this._contentCharactersBuffer = src._currentContentCharactersBufferFragment;
        this._contentCharactersBufferPtr = src._contentCharactersBufferPtr;
        this._contentObjects = src._currentContentObjectFragment;
        this._contentObjectsPtr = src._contentObjectsPtr;
        this.treeCount = 1;
    }
}

