/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import java.io.Serializable;
import java.util.Iterator;

public interface Part
extends Serializable {
    public String[] getMimeHeader(String var1);

    public void addMimeHeader(String var1, String var2);

    public String getContentLocation();

    public void setContentLocation(String var1);

    public void setContentId(String var1);

    public String getContentId();

    public Iterator getMatchingMimeHeaders(String[] var1);

    public Iterator getNonMatchingMimeHeaders(String[] var1);

    public String getContentType();

    public String getContentIdRef();
}

