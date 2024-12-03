/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.Document;
import javax.xml.namespace.QName;

public interface TypedXmlWriter {
    public void commit();

    public void commit(boolean var1);

    public void block();

    public Document getDocument();

    public void _attribute(String var1, Object var2);

    public void _attribute(String var1, String var2, Object var3);

    public void _attribute(QName var1, Object var2);

    public void _namespace(String var1);

    public void _namespace(String var1, String var2);

    public void _namespace(String var1, boolean var2);

    public void _pcdata(Object var1);

    public void _cdata(Object var1);

    public void _comment(Object var1) throws UnsupportedOperationException;

    public <T extends TypedXmlWriter> T _element(String var1, Class<T> var2);

    public <T extends TypedXmlWriter> T _element(String var1, String var2, Class<T> var3);

    public <T extends TypedXmlWriter> T _element(QName var1, Class<T> var2);

    public <T extends TypedXmlWriter> T _element(Class<T> var1);

    public <T extends TypedXmlWriter> T _cast(Class<T> var1);
}

