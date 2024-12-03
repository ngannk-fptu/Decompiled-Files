/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.NamespaceMappings
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemElement;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XML11Char;
import org.xml.sax.SAXException;

public class ElemAttribute
extends ElemElement {
    static final long serialVersionUID = 8817220961566919187L;

    @Override
    public int getXSLToken() {
        return 48;
    }

    @Override
    public String getNodeName() {
        return "attribute";
    }

    @Override
    protected String resolvePrefix(SerializationHandler rhandler, String prefix, String nodeNamespace) throws TransformerException {
        if (null != prefix && (prefix.length() == 0 || prefix.equals("xmlns")) && (null == (prefix = rhandler.getPrefix(nodeNamespace)) || prefix.length() == 0 || prefix.equals("xmlns"))) {
            if (nodeNamespace.length() > 0) {
                NamespaceMappings prefixMapping = rhandler.getNamespaceMappings();
                prefix = prefixMapping.generateNextPrefix();
            } else {
                prefix = "";
            }
        }
        return prefix;
    }

    protected boolean validateNodeName(String nodeName) {
        if (null == nodeName) {
            return false;
        }
        if (nodeName.equals("xmlns")) {
            return false;
        }
        return XML11Char.isXML11ValidQName(nodeName);
    }

    @Override
    void constructNode(String nodeName, String prefix, String nodeNamespace, TransformerImpl transformer) throws TransformerException {
        if (null != nodeName && nodeName.length() > 0) {
            SerializationHandler rhandler = transformer.getSerializationHandler();
            String val = transformer.transformToString(this);
            try {
                String localName = QName.getLocalPart(nodeName);
                if (prefix != null && prefix.length() > 0) {
                    rhandler.addAttribute(nodeNamespace, localName, nodeName, "CDATA", val, true);
                } else {
                    rhandler.addAttribute("", localName, nodeName, "CDATA", val, true);
                }
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
        }
    }

    @Override
    public ElemTemplateElement appendChild(ElemTemplateElement newChild) {
        int type = newChild.getXSLToken();
        switch (type) {
            case 9: 
            case 17: 
            case 28: 
            case 30: 
            case 35: 
            case 36: 
            case 37: 
            case 42: 
            case 50: 
            case 72: 
            case 73: 
            case 74: 
            case 75: 
            case 78: {
                break;
            }
            default: {
                this.error("ER_CANNOT_ADD", new Object[]{newChild.getNodeName(), this.getNodeName()});
            }
        }
        return super.appendChild(newChild);
    }

    @Override
    public void setName(AVT v) {
        if (v.isSimple() && v.getSimpleString().equals("xmlns")) {
            throw new IllegalArgumentException();
        }
        super.setName(v);
    }
}

