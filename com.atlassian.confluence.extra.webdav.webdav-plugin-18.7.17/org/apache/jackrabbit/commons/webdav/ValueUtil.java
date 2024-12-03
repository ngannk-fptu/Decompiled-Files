/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.webdav;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.util.XMLUtil;
import org.apache.jackrabbit.value.ValueHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class ValueUtil {
    public static Element valueToXml(Value jcrValue, Document document) throws RepositoryException {
        String type = PropertyType.nameFromValue(jcrValue.getType());
        String serializedValue = ValueHelper.serialize(jcrValue, true);
        Element xmlValue = document.createElementNS("http://www.day.com/jcr/webdav/1.0", "dcr:value");
        Text txt = document.createTextNode(serializedValue);
        xmlValue.appendChild(txt);
        Attr attr = document.createAttributeNS("http://www.day.com/jcr/webdav/1.0", "dcr:type");
        attr.setValue(type);
        xmlValue.setAttributeNodeNS(attr);
        return xmlValue;
    }

    public static Value[] valuesFromXml(Object propValue, int defaultType, ValueFactory valueFactory) throws RepositoryException {
        Value[] jcrValues;
        ArrayList<Element> valueElements = new ArrayList<Element>();
        if (propValue == null) {
            jcrValues = new Value[]{};
        } else {
            if (ValueUtil.isValueElement(propValue)) {
                valueElements.add((Element)propValue);
            } else if (propValue instanceof List) {
                for (Object el : (List)propValue) {
                    if (!ValueUtil.isValueElement(el)) continue;
                    valueElements.add((Element)el);
                }
            }
            jcrValues = new Value[valueElements.size()];
            int i = 0;
            for (Element element : valueElements) {
                jcrValues[i] = ValueUtil.getJcrValue(element, defaultType, valueFactory);
                ++i;
            }
        }
        return jcrValues;
    }

    private static boolean isValueElement(Object obj) {
        return obj instanceof Element && "value".equals(((Element)obj).getLocalName());
    }

    private static Value getJcrValue(Element valueElement, int defaultType, ValueFactory valueFactory) throws ValueFormatException, RepositoryException {
        if (valueElement == null) {
            return null;
        }
        String value = XMLUtil.getText(valueElement, "");
        String typeStr = XMLUtil.getAttribute(valueElement, "type", "http://www.day.com/jcr/webdav/1.0");
        int type = typeStr == null ? defaultType : PropertyType.valueFromName(typeStr);
        return ValueHelper.deserialize(value, type, true, valueFactory);
    }

    public static long[] lengthsFromXml(Object propValue) throws RepositoryException {
        long[] lengths;
        ArrayList<Element> lengthElements = new ArrayList<Element>();
        if (propValue == null) {
            lengths = new long[]{};
        } else {
            if (ValueUtil.isLengthElement(propValue)) {
                lengthElements.add((Element)propValue);
            } else if (propValue instanceof List) {
                for (Object el : (List)propValue) {
                    if (!ValueUtil.isLengthElement(el)) continue;
                    lengthElements.add((Element)el);
                }
            }
            lengths = new long[lengthElements.size()];
            int i = 0;
            for (Element element : lengthElements) {
                lengths[i] = Long.parseLong(XMLUtil.getText(element, "0"));
                ++i;
            }
        }
        return lengths;
    }

    private static boolean isLengthElement(Object obj) {
        return obj instanceof Element && "length".equals(((Element)obj).getLocalName());
    }
}

