/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api;

import java.io.IOException;
import java.io.InputStream;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public interface JackrabbitNodeTypeManager
extends NodeTypeManager {
    public static final String TEXT_XML = "text/xml";
    public static final String TEXT_X_JCR_CND = "text/x-jcr-cnd";

    public NodeType[] registerNodeTypes(InputSource var1) throws SAXException, RepositoryException;

    public NodeType[] registerNodeTypes(InputStream var1, String var2) throws IOException, RepositoryException;

    @Override
    public boolean hasNodeType(String var1) throws RepositoryException;
}

