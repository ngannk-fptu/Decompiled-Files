/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom;

import org.w3c.dom.Node;

public interface Entity
extends Node {
    public String getPublicId();

    public String getSystemId();

    public String getNotationName();

    public String getInputEncoding();

    public String getXmlEncoding();

    public String getXmlVersion();
}

