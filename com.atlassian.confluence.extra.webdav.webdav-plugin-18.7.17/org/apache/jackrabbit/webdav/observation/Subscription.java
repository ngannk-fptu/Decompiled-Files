/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.observation;

import org.apache.jackrabbit.webdav.xml.XmlSerializable;

public interface Subscription
extends XmlSerializable {
    public String getSubscriptionId();

    public boolean eventsProvideNodeTypeInformation();

    public boolean eventsProvideNoLocalFlag();
}

