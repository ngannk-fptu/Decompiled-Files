/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.Namespace;

public class MergedNsContext
implements NamespaceContext {
    final NamespaceContext mParentCtxt;
    final List mNamespaces;

    protected MergedNsContext(NamespaceContext namespaceContext, List list) {
        this.mParentCtxt = namespaceContext;
        this.mNamespaces = list == null ? Collections.EMPTY_LIST : list;
    }

    public static MergedNsContext construct(NamespaceContext namespaceContext, List list) {
        return new MergedNsContext(namespaceContext, list);
    }

    public String getNamespaceURI(String string) {
        String string2;
        if (string == null) {
            throw new IllegalArgumentException("Illegal to pass null prefix");
        }
        int n = this.mNamespaces.size();
        for (int i = 0; i < n; ++i) {
            Namespace namespace = (Namespace)this.mNamespaces.get(i);
            if (!string.equals(namespace.getPrefix())) continue;
            return namespace.getNamespaceURI();
        }
        if (this.mParentCtxt != null && (string2 = this.mParentCtxt.getNamespaceURI(string)) != null) {
            return string2;
        }
        if (string.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (string.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return null;
    }

    public String getPrefix(String string) {
        Object object;
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        int n = this.mNamespaces.size();
        for (int i = 0; i < n; ++i) {
            object = (Namespace)this.mNamespaces.get(i);
            if (!string.equals(object.getNamespaceURI())) continue;
            return object.getPrefix();
        }
        if (this.mParentCtxt != null) {
            String string2;
            String string3 = this.mParentCtxt.getPrefix(string);
            if (string3 != null && (string2 = this.getNamespaceURI(string3)).equals(string)) {
                return string3;
            }
            Iterator<String> iterator = this.mParentCtxt.getPrefixes(string);
            while (iterator.hasNext()) {
                String string4;
                object = iterator.next();
                if (((String)object).equals(string3) || !(string4 = this.getNamespaceURI((String)object)).equals(string)) continue;
                return object;
            }
        }
        if (string.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (string.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return null;
    }

    public Iterator getPrefixes(String string) {
        Object object;
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        ArrayList arrayList = null;
        int n = this.mNamespaces.size();
        for (int i = 0; i < n; ++i) {
            object = (Namespace)this.mNamespaces.get(i);
            if (!string.equals(object.getNamespaceURI())) continue;
            arrayList = this.addToList(arrayList, object.getPrefix());
        }
        if (this.mParentCtxt != null) {
            Iterator<String> iterator = this.mParentCtxt.getPrefixes(string);
            while (iterator.hasNext()) {
                String string2 = iterator.next();
                object = this.getNamespaceURI(string2);
                if (!((String)object).equals(string)) continue;
                arrayList = this.addToList(arrayList, string2);
            }
        }
        if (string.equals("http://www.w3.org/XML/1998/namespace")) {
            arrayList = this.addToList(arrayList, "xml");
        }
        if (string.equals("http://www.w3.org/2000/xmlns/")) {
            arrayList = this.addToList(arrayList, "xmlns");
        }
        return null;
    }

    protected ArrayList addToList(ArrayList arrayList, String string) {
        if (arrayList == null) {
            arrayList = new ArrayList<String>();
        }
        arrayList.add(string);
        return arrayList;
    }
}

