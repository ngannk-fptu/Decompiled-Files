/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.reader;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.json.impl.reader.XmlEventProvider;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jackson.JsonParser;

class MappedNotationEventProvider
extends XmlEventProvider {
    private final Map<String, String> jsonNs2XmlNs = new HashMap<String, String>();
    private final char nsSeparator;
    private final CharSequence nsSeparatorAsSequence;

    protected MappedNotationEventProvider(JsonParser parser, JSONConfiguration configuration, String rootName) throws XMLStreamException {
        super(parser, configuration, rootName);
        this.nsSeparator = configuration.getNsSeparator().charValue();
        this.nsSeparatorAsSequence = new StringBuffer(1).append(this.nsSeparator);
        Map<String, String> xml2JsonNs = configuration.getXml2JsonNs();
        if (xml2JsonNs != null) {
            for (Map.Entry<String, String> entry : xml2JsonNs.entrySet()) {
                this.jsonNs2XmlNs.put(entry.getValue(), entry.getKey());
            }
        }
    }

    @Override
    protected QName getAttributeQName(String jsonFieldName) {
        return this.getFieldQName(this.getAttributeName(jsonFieldName));
    }

    @Override
    protected QName getElementQName(String jsonFieldName) {
        return this.getFieldQName(jsonFieldName);
    }

    private QName getFieldQName(String jsonFieldName) {
        if (this.jsonNs2XmlNs.isEmpty() || !jsonFieldName.contains(this.nsSeparatorAsSequence)) {
            return new QName(jsonFieldName);
        }
        int dotIndex = jsonFieldName.indexOf(this.nsSeparator);
        String prefix = jsonFieldName.substring(0, dotIndex);
        String suffix = jsonFieldName.substring(dotIndex + 1);
        return this.jsonNs2XmlNs.containsKey(prefix) ? new QName(this.jsonNs2XmlNs.get(prefix), suffix) : new QName(jsonFieldName);
    }

    @Override
    protected boolean isAttribute(String jsonFieldName) {
        return jsonFieldName.startsWith("@") || this.getJsonConfiguration().getAttributeAsElements().contains(jsonFieldName);
    }
}

