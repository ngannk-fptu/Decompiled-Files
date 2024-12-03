/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.ExtensibleDOMImplementation;

public interface DomExtension {
    public float getPriority();

    public String getAuthor();

    public String getContactAddress();

    public String getURL();

    public String getDescription();

    public void registerTags(ExtensibleDOMImplementation var1);
}

