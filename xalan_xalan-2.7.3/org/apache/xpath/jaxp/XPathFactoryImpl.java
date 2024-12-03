/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.jaxp;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.jaxp.XPathImpl;

public class XPathFactoryImpl
extends XPathFactory {
    private static final String CLASS_NAME = "XPathFactoryImpl";
    private XPathFunctionResolver xPathFunctionResolver = null;
    private XPathVariableResolver xPathVariableResolver = null;
    private boolean featureSecureProcessing = false;

    @Override
    public boolean isObjectModelSupported(String objectModel) {
        if (objectModel == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_NULL", new Object[]{this.getClass().getName()});
            throw new NullPointerException(fmsg);
        }
        if (objectModel.length() == 0) {
            String fmsg = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_EMPTY", new Object[]{this.getClass().getName()});
            throw new IllegalArgumentException(fmsg);
        }
        return objectModel.equals("http://java.sun.com/jaxp/xpath/dom");
    }

    @Override
    public XPath newXPath() {
        return new XPathImpl(this.xPathVariableResolver, this.xPathFunctionResolver, this.featureSecureProcessing);
    }

    @Override
    public void setFeature(String name, boolean value) throws XPathFactoryConfigurationException {
        if (name == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_FEATURE_NAME_NULL", new Object[]{CLASS_NAME, value ? Boolean.TRUE : Boolean.FALSE});
            throw new NullPointerException(fmsg);
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this.featureSecureProcessing = value;
            return;
        }
        String fmsg = XSLMessages.createXPATHMessage("ER_FEATURE_UNKNOWN", new Object[]{name, CLASS_NAME, value ? Boolean.TRUE : Boolean.FALSE});
        throw new XPathFactoryConfigurationException(fmsg);
    }

    @Override
    public boolean getFeature(String name) throws XPathFactoryConfigurationException {
        if (name == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_GETTING_NULL_FEATURE", new Object[]{CLASS_NAME});
            throw new NullPointerException(fmsg);
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.featureSecureProcessing;
        }
        String fmsg = XSLMessages.createXPATHMessage("ER_GETTING_UNKNOWN_FEATURE", new Object[]{name, CLASS_NAME});
        throw new XPathFactoryConfigurationException(fmsg);
    }

    @Override
    public void setXPathFunctionResolver(XPathFunctionResolver resolver) {
        if (resolver == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_NULL_XPATH_FUNCTION_RESOLVER", new Object[]{CLASS_NAME});
            throw new NullPointerException(fmsg);
        }
        this.xPathFunctionResolver = resolver;
    }

    @Override
    public void setXPathVariableResolver(XPathVariableResolver resolver) {
        if (resolver == null) {
            String fmsg = XSLMessages.createXPATHMessage("ER_NULL_XPATH_VARIABLE_RESOLVER", new Object[]{CLASS_NAME});
            throw new NullPointerException(fmsg);
        }
        this.xPathVariableResolver = resolver;
    }
}

