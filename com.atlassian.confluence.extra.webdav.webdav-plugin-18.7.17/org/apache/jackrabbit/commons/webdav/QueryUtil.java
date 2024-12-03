/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.webdav;

import java.util.List;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.commons.webdav.JcrRemotingConstants;
import org.apache.jackrabbit.util.XMLUtil;
import org.apache.jackrabbit.value.ValueHelper;
import org.w3c.dom.Element;

public class QueryUtil
implements JcrRemotingConstants {
    public static void parseResultPropertyValue(Object propValue, List<String> columnNames, List<String> selectorNames, List<Value> values, ValueFactory valueFactory) throws ValueFormatException, RepositoryException {
        if (propValue instanceof List) {
            for (Object o : (List)propValue) {
                if (!(o instanceof Element)) continue;
                QueryUtil.parseColumnElement((Element)o, columnNames, selectorNames, values, valueFactory);
            }
        } else if (propValue instanceof Element) {
            QueryUtil.parseColumnElement((Element)propValue, columnNames, selectorNames, values, valueFactory);
        } else {
            throw new IllegalArgumentException("SearchResultProperty requires a list of 'dcr:column' xml elements.");
        }
    }

    private static void parseColumnElement(Element columnElement, List<String> columnNames, List<String> selectorNames, List<Value> values, ValueFactory valueFactory) throws ValueFormatException, RepositoryException {
        String text;
        if (!"column".equals(columnElement.getLocalName()) && "http://www.day.com/jcr/webdav/1.0".equals(columnElement.getNamespaceURI())) {
            return;
        }
        columnNames.add(XMLUtil.getChildText(columnElement, "name", "http://www.day.com/jcr/webdav/1.0"));
        selectorNames.add(XMLUtil.getChildText(columnElement, "selectorName", "http://www.day.com/jcr/webdav/1.0"));
        Value jcrValue = null;
        Element valueElement = XMLUtil.getChildElement(columnElement, "value", "http://www.day.com/jcr/webdav/1.0");
        if (valueElement != null && (text = XMLUtil.getText(valueElement)) != null) {
            String typeStr = XMLUtil.getAttribute(valueElement, "type", "http://www.day.com/jcr/webdav/1.0");
            jcrValue = ValueHelper.deserialize(text, PropertyType.valueFromName(typeStr), true, valueFactory);
        }
        values.add(jcrValue);
    }
}

