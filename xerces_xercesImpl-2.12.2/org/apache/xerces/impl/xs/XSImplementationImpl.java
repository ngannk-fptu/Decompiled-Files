/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.PSVIDOMImplementationImpl;
import org.apache.xerces.impl.xs.XSLoaderImpl;
import org.apache.xerces.impl.xs.util.LSInputListImpl;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.LSInputList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSException;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.LSInput;

public class XSImplementationImpl
extends PSVIDOMImplementationImpl
implements XSImplementation {
    static final XSImplementationImpl singleton = new XSImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    @Override
    public boolean hasFeature(String string, String string2) {
        return string.equalsIgnoreCase("XS-Loader") && (string2 == null || string2.equals("1.0")) || super.hasFeature(string, string2);
    }

    @Override
    public XSLoader createXSLoader(StringList stringList) throws XSException {
        XSLoaderImpl xSLoaderImpl = new XSLoaderImpl();
        if (stringList == null) {
            return xSLoaderImpl;
        }
        for (int i = 0; i < stringList.getLength(); ++i) {
            if (stringList.item(i).equals("1.0")) continue;
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{stringList.item(i)});
            throw new XSException(1, string);
        }
        return xSLoaderImpl;
    }

    @Override
    public StringList createStringList(String[] stringArray) {
        int n = stringArray != null ? stringArray.length : 0;
        return n != 0 ? new StringListImpl((String[])stringArray.clone(), n) : StringListImpl.EMPTY_LIST;
    }

    @Override
    public LSInputList createLSInputList(LSInput[] lSInputArray) {
        int n = lSInputArray != null ? lSInputArray.length : 0;
        return n != 0 ? new LSInputListImpl((LSInput[])lSInputArray.clone(), n) : LSInputListImpl.EMPTY_LIST;
    }

    @Override
    public StringList getRecognizedVersions() {
        StringListImpl stringListImpl = new StringListImpl(new String[]{"1.0"}, 1);
        return stringListImpl;
    }
}

