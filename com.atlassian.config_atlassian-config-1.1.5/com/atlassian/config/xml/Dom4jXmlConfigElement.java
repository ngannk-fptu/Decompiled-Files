/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Branch
 *  org.dom4j.DocumentHelper
 *  org.dom4j.Element
 */
package com.atlassian.config.xml;

import com.atlassian.config.AbstractConfigElement;
import com.atlassian.config.xml.AbstractDom4jXmlConfigurationPersister;
import org.dom4j.Branch;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public abstract class Dom4jXmlConfigElement<T>
extends AbstractConfigElement<T, Element> {
    private Element context;
    private boolean useCData = ((AbstractDom4jXmlConfigurationPersister)this.getConfiguration()).isUseCData();

    public Dom4jXmlConfigElement(String name, Element context, AbstractDom4jXmlConfigurationPersister config) {
        super(name, context, config);
    }

    protected Element getOrMakeElement(String path) {
        Element element = (Element)this.context.selectSingleNode(this.getPropertyName());
        if (element == null) {
            element = DocumentHelper.makeElement((Branch)this.context, (String)this.getPropertyName());
        } else {
            element.clearContent();
        }
        return element;
    }

    @Override
    public Element getContext() {
        return this.context;
    }

    @Override
    public void setContext(Element context) {
        this.context = context;
    }

    protected boolean isUsingCData() {
        return this.useCData;
    }
}

