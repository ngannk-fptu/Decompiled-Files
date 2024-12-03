/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.connect;

import java.io.IOException;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.connect.ConnectContent;

@ConsumerType
public interface ConnectModule {
    public ConnectContent getContent() throws IOException;
}

