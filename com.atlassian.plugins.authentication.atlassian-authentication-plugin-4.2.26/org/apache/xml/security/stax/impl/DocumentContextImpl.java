/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;

public class DocumentContextImpl
implements DocumentContext,
Cloneable {
    private String encoding;
    private String baseURI;
    private final Map<Integer, XMLSecurityConstants.ContentType> contentTypeMap = new TreeMap<Integer, XMLSecurityConstants.ContentType>();
    private final Map<Object, Integer> processorToIndexMap = new HashMap<Object, Integer>();

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getBaseURI() {
        return this.baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override
    public synchronized void setIsInEncryptedContent(int index, Object key) {
        this.contentTypeMap.put(index, XMLSecurityConstants.ContentType.ENCRYPTION);
        this.processorToIndexMap.put(key, index);
    }

    @Override
    public synchronized void unsetIsInEncryptedContent(Object key) {
        Integer index = this.processorToIndexMap.remove(key);
        this.contentTypeMap.remove(index);
    }

    @Override
    public boolean isInEncryptedContent() {
        return this.contentTypeMap.containsValue((Object)XMLSecurityConstants.ContentType.ENCRYPTION);
    }

    @Override
    public synchronized void setIsInSignedContent(int index, Object key) {
        this.contentTypeMap.put(index, XMLSecurityConstants.ContentType.SIGNATURE);
        this.processorToIndexMap.put(key, index);
    }

    @Override
    public synchronized void unsetIsInSignedContent(Object key) {
        Integer index = this.processorToIndexMap.remove(key);
        this.contentTypeMap.remove(index);
    }

    @Override
    public boolean isInSignedContent() {
        return this.contentTypeMap.containsValue((Object)XMLSecurityConstants.ContentType.SIGNATURE);
    }

    @Override
    public List<XMLSecurityConstants.ContentType> getProtectionOrder() {
        return new ArrayList<XMLSecurityConstants.ContentType>(this.contentTypeMap.values());
    }

    @Override
    public Map<Integer, XMLSecurityConstants.ContentType> getContentTypeMap() {
        return Collections.unmodifiableMap(this.contentTypeMap);
    }

    protected void setContentTypeMap(Map<Integer, XMLSecurityConstants.ContentType> contentTypeMap) {
        this.contentTypeMap.putAll(contentTypeMap);
    }

    public DocumentContextImpl clone() throws CloneNotSupportedException {
        DocumentContextImpl documentContext = (DocumentContextImpl)super.clone();
        documentContext.setEncoding(this.encoding);
        documentContext.setBaseURI(this.baseURI);
        documentContext.setContentTypeMap(this.getContentTypeMap());
        return documentContext;
    }
}

