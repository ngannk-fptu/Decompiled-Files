/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.evt;

import javax.xml.stream.events.DTD;

public interface DTD2
extends DTD {
    public String getRootName();

    public String getSystemId();

    public String getPublicId();

    public String getInternalSubset();
}

