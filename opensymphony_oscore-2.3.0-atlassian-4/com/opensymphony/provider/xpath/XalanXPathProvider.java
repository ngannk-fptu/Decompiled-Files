/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xpath.XPathAPI
 */
package com.opensymphony.provider.xpath;

import com.opensymphony.provider.ProviderConfigurationException;
import com.opensymphony.provider.ProviderInvocationException;
import com.opensymphony.provider.XPathProvider;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XalanXPathProvider
implements XPathProvider {
    @Override
    public Node getNode(Node base, String xpath) throws ProviderInvocationException {
        try {
            return XPathAPI.selectSingleNode((Node)base, (String)xpath);
        }
        catch (TransformerException e) {
            throw new ProviderInvocationException(e);
        }
    }

    @Override
    public NodeList getNodes(Node base, String xpath) throws ProviderInvocationException {
        try {
            return XPathAPI.selectNodeList((Node)base, (String)xpath);
        }
        catch (TransformerException e) {
            throw new ProviderInvocationException(e);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ProviderConfigurationException {
    }
}

