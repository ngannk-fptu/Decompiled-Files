/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.transformer.canonicalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.transformer.canonicalizer.CanonicalizerBase;

public class Canonicalizer11
extends CanonicalizerBase {
    public Canonicalizer11(boolean includeComments) {
        super(includeComments);
    }

    @Override
    protected List<XMLSecAttribute> getInitialUtilizedAttributes(XMLSecStartElement xmlSecStartElement, CanonicalizerBase.C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecAttribute> utilizedAttributes = Collections.emptyList();
        ArrayList<XMLSecAttribute> visibleAttributes = new ArrayList<XMLSecAttribute>();
        xmlSecStartElement.getAttributesFromCurrentScope(visibleAttributes);
        for (int i = 0; i < visibleAttributes.size(); ++i) {
            XMLSecAttribute comparableAttribute = (XMLSecAttribute)visibleAttributes.get(i);
            QName comparableAttributeName = comparableAttribute.getName();
            if (!"xml".equals(comparableAttributeName.getPrefix()) || "id".equals(comparableAttributeName.getLocalPart()) || "base".equals(comparableAttributeName.getLocalPart()) || outputStack.containsOnStack(comparableAttribute) != null) continue;
            if (utilizedAttributes == Collections.emptyList()) {
                utilizedAttributes = new ArrayList<XMLSecAttribute>(2);
            }
            utilizedAttributes.add(comparableAttribute);
            outputStack.peek().add(comparableAttribute);
        }
        List<XMLSecAttribute> elementAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i = 0; i < elementAttributes.size(); ++i) {
            XMLSecAttribute comparableAttribute = elementAttributes.get(i);
            QName attributeName = comparableAttribute.getName();
            if ("xml".equals(attributeName.getPrefix())) continue;
            if (utilizedAttributes == Collections.emptyList()) {
                utilizedAttributes = new ArrayList<XMLSecAttribute>(2);
            }
            utilizedAttributes.add(comparableAttribute);
        }
        return utilizedAttributes;
    }
}

