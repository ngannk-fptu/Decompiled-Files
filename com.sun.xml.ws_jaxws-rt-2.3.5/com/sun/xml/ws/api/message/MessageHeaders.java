/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.message;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Header;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;

public interface MessageHeaders {
    public void understood(Header var1);

    public void understood(QName var1);

    public void understood(String var1, String var2);

    public Header get(String var1, String var2, boolean var3);

    public Header get(QName var1, boolean var2);

    public Iterator<Header> getHeaders(String var1, String var2, boolean var3);

    public Iterator<Header> getHeaders(String var1, boolean var2);

    public Iterator<Header> getHeaders(QName var1, boolean var2);

    public Iterator<Header> getHeaders();

    public boolean hasHeaders();

    public boolean add(Header var1);

    public Header remove(QName var1);

    public Header remove(String var1, String var2);

    public void replace(Header var1, Header var2);

    public boolean addOrReplace(Header var1);

    public Set<QName> getUnderstoodHeaders();

    public Set<QName> getNotUnderstoodHeaders(Set<String> var1, Set<QName> var2, WSBinding var3);

    public boolean isUnderstood(Header var1);

    public boolean isUnderstood(QName var1);

    public boolean isUnderstood(String var1, String var2);

    public List<Header> asList();
}

