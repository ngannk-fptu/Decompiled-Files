/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import java.util.List;
import javax.xml.namespace.QName;

public interface ContentCodingAwareRequest {
    public static final QName PRECONDITION_SUPPORTED = new QName("http://www.day.com/jcr/webdav/1.0", "supported-content-coding", "dcr");

    public String getAcceptableCodings();

    public List<String> getRequestContentCodings();
}

