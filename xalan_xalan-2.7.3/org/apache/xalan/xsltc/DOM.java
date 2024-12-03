/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc;

import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.serializer.SerializationHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface DOM {
    public static final int FIRST_TYPE = 0;
    public static final int NO_TYPE = -1;
    public static final int NULL = 0;
    public static final int RETURN_CURRENT = 0;
    public static final int RETURN_PARENT = 1;
    public static final int SIMPLE_RTF = 0;
    public static final int ADAPTIVE_RTF = 1;
    public static final int TREE_RTF = 2;

    public DTMAxisIterator getIterator();

    public String getStringValue();

    public DTMAxisIterator getChildren(int var1);

    public DTMAxisIterator getTypedChildren(int var1);

    public DTMAxisIterator getAxisIterator(int var1);

    public DTMAxisIterator getTypedAxisIterator(int var1, int var2);

    public DTMAxisIterator getNthDescendant(int var1, int var2, boolean var3);

    public DTMAxisIterator getNamespaceAxisIterator(int var1, int var2);

    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator var1, int var2, String var3, boolean var4);

    public DTMAxisIterator orderNodes(DTMAxisIterator var1, int var2);

    public String getNodeName(int var1);

    public String getNodeNameX(int var1);

    public String getNamespaceName(int var1);

    public int getExpandedTypeID(int var1);

    public int getNamespaceType(int var1);

    public int getParent(int var1);

    public int getAttributeNode(int var1, int var2);

    public String getStringValueX(int var1);

    public void copy(int var1, SerializationHandler var2) throws TransletException;

    public void copy(DTMAxisIterator var1, SerializationHandler var2) throws TransletException;

    public String shallowCopy(int var1, SerializationHandler var2) throws TransletException;

    public boolean lessThan(int var1, int var2);

    public void characters(int var1, SerializationHandler var2) throws TransletException;

    public Node makeNode(int var1);

    public Node makeNode(DTMAxisIterator var1);

    public NodeList makeNodeList(int var1);

    public NodeList makeNodeList(DTMAxisIterator var1);

    public String getLanguage(int var1);

    public int getSize();

    public String getDocumentURI(int var1);

    public void setFilter(StripFilter var1);

    public void setupMapping(String[] var1, String[] var2, int[] var3, String[] var4);

    public boolean isElement(int var1);

    public boolean isAttribute(int var1);

    public String lookupNamespace(int var1, String var2) throws TransletException;

    public int getNodeIdent(int var1);

    public int getNodeHandle(int var1);

    public DOM getResultTreeFrag(int var1, int var2);

    public DOM getResultTreeFrag(int var1, int var2, boolean var3);

    public SerializationHandler getOutputDomBuilder();

    public int getNSType(int var1);

    public int getDocument();

    public String getUnparsedEntityURI(String var1);

    public Hashtable getElementsWithIDs();
}

