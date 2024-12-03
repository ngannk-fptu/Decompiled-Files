/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConfluenceXmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.beans.factory.FactoryBean;

@Deprecated
public class XmlOutputFactoryFactoryBean
implements FactoryBean<XMLOutputFactory> {
    private final boolean fragmentOutput;
    private XMLOutputFactory xmlOutputFactory;

    public XmlOutputFactoryFactoryBean(boolean fragmentOutput) {
        this.fragmentOutput = fragmentOutput;
    }

    public XMLOutputFactory getObject() {
        if (this.xmlOutputFactory == null) {
            this.xmlOutputFactory = this.fragmentOutput ? ConfluenceXmlOutputFactory.createFragmentXmlOutputFactory() : ConfluenceXmlOutputFactory.create();
        }
        return this.xmlOutputFactory;
    }

    public Class getObjectType() {
        return XmlOutputFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

