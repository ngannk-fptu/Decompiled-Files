/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.serializer.SerializationHandler;

public interface Translet {
    public void transform(DOM var1, SerializationHandler var2) throws TransletException;

    public void transform(DOM var1, SerializationHandler[] var2) throws TransletException;

    public void transform(DOM var1, DTMAxisIterator var2, SerializationHandler var3) throws TransletException;

    public Object addParameter(String var1, Object var2);

    public void buildKeys(DOM var1, DTMAxisIterator var2, SerializationHandler var3, int var4) throws TransletException;

    public void addAuxiliaryClass(Class var1);

    public Class getAuxiliaryClass(String var1);

    public String[] getNamesArray();

    public String[] getUrisArray();

    public int[] getTypesArray();

    public String[] getNamespaceArray();
}

