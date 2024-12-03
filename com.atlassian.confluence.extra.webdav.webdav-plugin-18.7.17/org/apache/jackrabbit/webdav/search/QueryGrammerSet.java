/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.search;

import java.util.HashSet;
import java.util.Set;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.search.SearchConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class QueryGrammerSet
extends AbstractDavProperty<Set<? extends XmlSerializable>>
implements SearchConstants {
    private final Set<Grammer> queryGrammers = new HashSet<Grammer>();

    public QueryGrammerSet() {
        super(QUERY_GRAMMER_SET, true);
    }

    public void addQueryLanguage(String grammerName, Namespace namespace) {
        this.queryGrammers.add(new Grammer(grammerName, namespace));
    }

    public String[] getQueryLanguages() {
        int size = this.queryGrammers.size();
        if (size > 0) {
            String[] qLangStr = new String[size];
            Grammer[] grammers = this.queryGrammers.toArray(new Grammer[size]);
            for (int i = 0; i < grammers.length; ++i) {
                qLangStr[i] = grammers[i].namespace.getURI() + grammers[i].localName;
            }
            return qLangStr;
        }
        return new String[0];
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        for (Grammer qGrammer : this.queryGrammers) {
            elem.appendChild(qGrammer.toXml(document));
        }
        return elem;
    }

    @Override
    public Set<? extends XmlSerializable> getValue() {
        return this.queryGrammers;
    }

    private class Grammer
    implements XmlSerializable {
        private final String localName;
        private final Namespace namespace;
        private final int hashCode;

        Grammer(String localName, Namespace namespace) {
            this.localName = localName;
            this.namespace = namespace;
            this.hashCode = DomUtil.getExpandedName(localName, namespace).hashCode();
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Grammer) {
                return obj.hashCode() == this.hashCode();
            }
            return false;
        }

        @Override
        public Element toXml(Document document) {
            Element sqg = DomUtil.createElement(document, "supported-query-grammar", SearchConstants.NAMESPACE);
            Element grammer = DomUtil.addChildElement(sqg, "grammar", SearchConstants.NAMESPACE);
            DomUtil.addChildElement(grammer, this.localName, this.namespace);
            return sqg;
        }
    }
}

