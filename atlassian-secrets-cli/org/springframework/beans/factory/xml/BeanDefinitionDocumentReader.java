/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Document;

public interface BeanDefinitionDocumentReader {
    public void registerBeanDefinitions(Document var1, XmlReaderContext var2) throws BeanDefinitionStoreException;
}

