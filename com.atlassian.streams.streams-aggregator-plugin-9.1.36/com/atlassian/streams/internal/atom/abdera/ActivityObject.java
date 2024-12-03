/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.internal.atom.abdera;

import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.internal.atom.abdera.AtomConstants;
import com.google.common.base.Preconditions;
import java.net.URI;
import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;

public class ActivityObject
extends ExtensibleElementWrapper {
    public ActivityObject(Element internal) {
        super(internal);
    }

    public ActivityObject(Factory factory, QName qname) {
        super(factory, qname);
    }

    public void setId(String id) {
        Preconditions.checkNotNull((Object)id, (Object)"id");
        IRIElement idElem = this.getFactory().newID();
        idElem.setValue(id);
        this.addExtension(idElem);
    }

    public void setTitle(String title) {
        Preconditions.checkNotNull((Object)title, (Object)"title");
        Text titleElem = this.getFactory().newTitle();
        titleElem.setValue(title);
        this.addExtension(titleElem);
    }

    public void setSummary(String summary) {
        Preconditions.checkNotNull((Object)summary, (Object)"summary");
        Text summaryElem = this.getFactory().newSummary();
        summaryElem.setValue(summary);
        this.addExtension(summaryElem);
    }

    public void setContent(String content) {
        Preconditions.checkNotNull((Object)content, (Object)"content");
        Content contentElem = this.getFactory().newContent(Content.Type.HTML);
        contentElem.setValue(content);
        this.addExtension(contentElem);
    }

    public void setAlternateLink(URI link) {
        Preconditions.checkNotNull((Object)link, (Object)"link");
        Link linkElem = this.getFactory().newLink();
        linkElem.setRel("alternate");
        linkElem.setHref(link.toASCIIString());
        this.addExtension(linkElem);
    }

    public void setObjectType(ActivityObjectType objectType) {
        this.addSimpleExtension(AtomConstants.ACTIVITY_OBJECT_TYPE, ((ActivityObjectType)Preconditions.checkNotNull((Object)objectType, (Object)"objectType")).iri().toASCIIString());
    }

    public void setSource(Source source) {
        this.addExtension(source);
    }
}

