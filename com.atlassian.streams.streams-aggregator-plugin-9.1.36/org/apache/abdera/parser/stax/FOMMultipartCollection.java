/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParseException
 */
package org.apache.abdera.parser.stax;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.stax.FOMCollection;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.MimeTypeParseException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMMultipartCollection
extends FOMCollection {
    protected FOMMultipartCollection(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMMultipartCollection(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMMultipartCollection(OMContainer parent, OMFactory factory) {
        super(COLLECTION, parent, factory);
    }

    public boolean acceptsMultipart(String mediaType) {
        Map<String, String> accept = this.getAcceptMultiparted();
        if (accept.size() == 0) {
            accept = Collections.singletonMap("application/atom+xml;type=entry", null);
        }
        for (Map.Entry<String, String> entry : accept.entrySet()) {
            if (!MimeTypeHelper.isMatch(entry.getKey(), mediaType) || entry.getValue() == null || !entry.getValue().equals("multipart-related")) continue;
            return true;
        }
        return false;
    }

    public boolean acceptsMultipart(MimeType mediaType) {
        return this.accepts(mediaType.toString());
    }

    public Map<String, String> getAcceptMultiparted() {
        HashMap<String, String> accept = new HashMap<String, String>();
        Iterator i = this.getChildrenWithName(ACCEPT);
        if (i == null || !i.hasNext()) {
            i = this.getChildrenWithName(PRE_RFC_ACCEPT);
        }
        while (i.hasNext()) {
            Element e = (Element)i.next();
            String t = e.getText();
            if (t == null) continue;
            if (e.getAttributeValue(ALTERNATE) != null && e.getAttributeValue(ALTERNATE).trim().length() > 0) {
                accept.put(t.trim(), e.getAttributeValue(ALTERNATE));
                continue;
            }
            accept.put(t.trim(), null);
        }
        return accept;
    }

    public Collection setAccept(String mediaRange, String alternate) {
        return this.setAccept(Collections.singletonMap(mediaRange, alternate));
    }

    public Collection setAccept(Map<String, String> mediaRanges) {
        this.complete();
        if (mediaRanges != null && mediaRanges.size() > 0) {
            this._removeChildren(ACCEPT, true);
            this._removeChildren(PRE_RFC_ACCEPT, true);
            if (mediaRanges.size() == 1 && mediaRanges.keySet().iterator().next().equals("")) {
                this.addExtension(ACCEPT);
            } else {
                for (Map.Entry<String, String> entry : mediaRanges.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("entry")) {
                        this.addSimpleExtension(ACCEPT, "application/atom+xml;type=entry");
                        continue;
                    }
                    try {
                        Element accept = this.addSimpleExtension(ACCEPT, new MimeType(entry.getKey()).toString());
                        if (entry.getValue() == null) continue;
                        accept.setAttributeValue(ALTERNATE, entry.getValue());
                    }
                    catch (javax.activation.MimeTypeParseException e) {
                        throw new MimeTypeParseException(e);
                    }
                }
            }
        } else {
            this._removeChildren(ACCEPT, true);
            this._removeChildren(PRE_RFC_ACCEPT, true);
        }
        return this;
    }

    public Collection addAccepts(String mediaRange, String alternate) {
        return this.addAccepts(Collections.singletonMap(mediaRange, alternate));
    }

    public Collection addAccepts(Map<String, String> mediaRanges) {
        this.complete();
        if (mediaRanges != null) {
            for (Map.Entry<String, String> entry : mediaRanges.entrySet()) {
                if (this.accepts(entry.getKey())) continue;
                try {
                    Element accept = this.addSimpleExtension(ACCEPT, new MimeType(entry.getKey()).toString());
                    if (entry.getValue() == null) continue;
                    accept.setAttributeValue(ALTERNATE, entry.getValue());
                }
                catch (Exception e) {}
            }
        }
        return this;
    }
}

