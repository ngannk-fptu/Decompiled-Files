/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.jaxp;

import java.io.IOException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.jaxp.JAXPExtensionsProvider;
import org.apache.xpath.jaxp.JAXPPrefixResolver;
import org.apache.xpath.jaxp.JAXPVariableStack;
import org.apache.xpath.jaxp.XPathExpressionImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XPathImpl
implements javax.xml.xpath.XPath {
    private XPathVariableResolver variableResolver;
    private XPathFunctionResolver functionResolver;
    private XPathVariableResolver origVariableResolver;
    private XPathFunctionResolver origFunctionResolver;
    private NamespaceContext namespaceContext = null;
    private JAXPPrefixResolver prefixResolver;
    private boolean featureSecureProcessing = false;
    private static Document d = null;

    XPathImpl(XPathVariableResolver vr, XPathFunctionResolver fr) {
        this.origVariableResolver = this.variableResolver = vr;
        this.origFunctionResolver = this.functionResolver = fr;
    }

    XPathImpl(XPathVariableResolver vr, XPathFunctionResolver fr, boolean featureSecureProcessing) {
        this.origVariableResolver = this.variableResolver = vr;
        this.origFunctionResolver = this.functionResolver = fr;
        this.featureSecureProcessing = featureSecureProcessing;
    }

    @Override
    public void setXPathVariableResolver(XPathVariableResolver resolver) {
        if (resolver == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPathVariableResolver"});
            throw new NullPointerException(fmsg);
        }
        this.variableResolver = resolver;
    }

    @Override
    public XPathVariableResolver getXPathVariableResolver() {
        return this.variableResolver;
    }

    @Override
    public void setXPathFunctionResolver(XPathFunctionResolver resolver) {
        if (resolver == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPathFunctionResolver"});
            throw new NullPointerException(fmsg);
        }
        this.functionResolver = resolver;
    }

    @Override
    public XPathFunctionResolver getXPathFunctionResolver() {
        return this.functionResolver;
    }

    @Override
    public void setNamespaceContext(NamespaceContext nsContext) {
        if (nsContext == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"NamespaceContext"});
            throw new NullPointerException(fmsg);
        }
        this.namespaceContext = nsContext;
        this.prefixResolver = new JAXPPrefixResolver(nsContext);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }

    private static DocumentBuilder getParser() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);
            return dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new Error(e.toString());
        }
    }

    private static Document getDummyDocument() {
        if (d == null) {
            DOMImplementation dim = XPathImpl.getParser().getDOMImplementation();
            d = dim.createDocument("http://java.sun.com/jaxp/xpath", "dummyroot", null);
        }
        return d;
    }

    private XObject eval(String expression, Object contextItem) throws TransformerException {
        XPath xpath = new XPath(expression, null, this.prefixResolver, 0);
        XPathContext xpathSupport = null;
        if (this.functionResolver != null) {
            JAXPExtensionsProvider jep = new JAXPExtensionsProvider(this.functionResolver, this.featureSecureProcessing);
            xpathSupport = new XPathContext(jep, false);
        } else {
            xpathSupport = new XPathContext(false);
        }
        XObject xobj = null;
        xpathSupport.setVarStack(new JAXPVariableStack(this.variableResolver));
        xobj = contextItem instanceof Node ? xpath.execute(xpathSupport, (Node)contextItem, (PrefixResolver)this.prefixResolver) : xpath.execute(xpathSupport, -1, (PrefixResolver)this.prefixResolver);
        return xobj;
    }

    @Override
    public Object evaluate(String expression, Object item, QName returnType) throws XPathExpressionException {
        if (expression == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPath expression"});
            throw new NullPointerException(fmsg);
        }
        if (returnType == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"returnType"});
            throw new NullPointerException(fmsg);
        }
        if (!this.isSupported(returnType)) {
            String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[]{returnType.toString()});
            throw new IllegalArgumentException(fmsg);
        }
        try {
            XObject resultObject = this.eval(expression, item);
            return this.getResultAsType(resultObject, returnType);
        }
        catch (NullPointerException npe) {
            throw new XPathExpressionException(npe);
        }
        catch (TransformerException te) {
            Throwable nestedException = te.getException();
            if (nestedException instanceof XPathFunctionException) {
                throw (XPathFunctionException)nestedException;
            }
            throw new XPathExpressionException(te);
        }
    }

    private boolean isSupported(QName returnType) {
        return returnType.equals(XPathConstants.STRING) || returnType.equals(XPathConstants.NUMBER) || returnType.equals(XPathConstants.BOOLEAN) || returnType.equals(XPathConstants.NODE) || returnType.equals(XPathConstants.NODESET);
    }

    private Object getResultAsType(XObject resultObject, QName returnType) throws TransformerException {
        if (returnType.equals(XPathConstants.STRING)) {
            return resultObject.str();
        }
        if (returnType.equals(XPathConstants.NUMBER)) {
            return new Double(resultObject.num());
        }
        if (returnType.equals(XPathConstants.BOOLEAN)) {
            return resultObject.bool() ? Boolean.TRUE : Boolean.FALSE;
        }
        if (returnType.equals(XPathConstants.NODESET)) {
            return resultObject.nodelist();
        }
        if (returnType.equals(XPathConstants.NODE)) {
            NodeIterator ni = resultObject.nodeset();
            return ni.nextNode();
        }
        String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[]{returnType.toString()});
        throw new IllegalArgumentException(fmsg);
    }

    @Override
    public String evaluate(String expression, Object item) throws XPathExpressionException {
        return (String)this.evaluate(expression, item, XPathConstants.STRING);
    }

    @Override
    public XPathExpression compile(String expression) throws XPathExpressionException {
        if (expression == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPath expression"});
            throw new NullPointerException(fmsg);
        }
        try {
            XPath xpath = new XPath(expression, null, this.prefixResolver, 0);
            XPathExpressionImpl ximpl = new XPathExpressionImpl(xpath, this.prefixResolver, this.functionResolver, this.variableResolver, this.featureSecureProcessing);
            return ximpl;
        }
        catch (TransformerException te) {
            throw new XPathExpressionException(te);
        }
    }

    @Override
    public Object evaluate(String expression, InputSource source, QName returnType) throws XPathExpressionException {
        if (source == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"source"});
            throw new NullPointerException(fmsg);
        }
        if (expression == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPath expression"});
            throw new NullPointerException(fmsg);
        }
        if (returnType == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"returnType"});
            throw new NullPointerException(fmsg);
        }
        if (!this.isSupported(returnType)) {
            String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[]{returnType.toString()});
            throw new IllegalArgumentException(fmsg);
        }
        try {
            Document document = XPathImpl.getParser().parse(source);
            XObject resultObject = this.eval(expression, document);
            return this.getResultAsType(resultObject, returnType);
        }
        catch (SAXException e) {
            throw new XPathExpressionException(e);
        }
        catch (IOException e) {
            throw new XPathExpressionException(e);
        }
        catch (TransformerException te) {
            Throwable nestedException = te.getException();
            if (nestedException instanceof XPathFunctionException) {
                throw (XPathFunctionException)nestedException;
            }
            throw new XPathExpressionException(te);
        }
    }

    @Override
    public String evaluate(String expression, InputSource source) throws XPathExpressionException {
        return (String)this.evaluate(expression, source, XPathConstants.STRING);
    }

    @Override
    public void reset() {
        this.variableResolver = this.origVariableResolver;
        this.functionResolver = this.origFunctionResolver;
        this.namespaceContext = null;
    }
}

