/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.message.BasicHeader
 */
package org.bedework.util.http;

import java.util.ArrayList;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class Headers
extends ArrayList<Header> {
    public Headers add(String name, String val) {
        this.add(new BasicHeader(name, val));
        return this;
    }

    public Header[] asArray() {
        return this.toArray(new Header[this.size()]);
    }
}

