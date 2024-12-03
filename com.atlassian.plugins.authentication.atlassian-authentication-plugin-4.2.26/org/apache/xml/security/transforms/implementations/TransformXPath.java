/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.transforms.implementations;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.JDKXPathFactory;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.XPathAPI;
import org.apache.xml.security.utils.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TransformXPath
extends TransformSpi {
    private static final Logger LOG = LoggerFactory.getLogger(TransformXPath.class);

    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/TR/1999/REC-xpath-19991116";
    }

    @Override
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws TransformationException {
        try {
            Element xpathElement = XMLUtils.selectDsNode(transformElement.getFirstChild(), "XPath", 0);
            if (xpathElement == null) {
                Object[] exArgs = new Object[]{"ds:XPath", "Transform"};
                throw new TransformationException("xml.WrongContent", exArgs);
            }
            Node xpathnode = xpathElement.getFirstChild();
            if (xpathnode == null) {
                throw new DOMException(3, "Text must be in ds:Xpath");
            }
            String str = XMLUtils.getStrFromNode(xpathnode);
            input.setNeedsToBeExpanded(this.needsCircumvent(str));
            XPathFactory xpathFactory = this.getXPathFactory();
            XPathAPI xpathAPIInstance = xpathFactory.newXPathAPI();
            input.addNodeFilter(new XPathNodeFilter(xpathElement, xpathnode, str, xpathAPIInstance));
            input.setNodeSet(true);
            return input;
        }
        catch (IOException | XMLParserException | DOMException ex) {
            throw new TransformationException(ex);
        }
    }

    protected XPathFactory getXPathFactory() {
        return new JDKXPathFactory();
    }

    private boolean needsCircumvent(String str) {
        return str.indexOf("namespace") != -1 || str.indexOf("name()") != -1;
    }

    private static class XPathNodeFilter
    implements NodeFilter {
        private final XPathAPI xPathAPI;
        private final Node xpathnode;
        private final Element xpathElement;
        private final String str;

        XPathNodeFilter(Element xpathElement, Node xpathnode, String str, XPathAPI xPathAPI) {
            this.xpathnode = xpathnode;
            this.str = str;
            this.xpathElement = xpathElement;
            this.xPathAPI = xPathAPI;
        }

        @Override
        public int isNodeInclude(Node currentNode) {
            try {
                boolean include = this.xPathAPI.evaluate(currentNode, this.xpathnode, this.str, this.xpathElement);
                if (include) {
                    return 1;
                }
                return 0;
            }
            catch (TransformerException e) {
                LOG.debug("Error evaluating XPath expression", (Throwable)e);
                return 0;
            }
        }

        @Override
        public int isNodeIncludeDO(Node n, int level) {
            return this.isNodeInclude(n);
        }
    }
}

