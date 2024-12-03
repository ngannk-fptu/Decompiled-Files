/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;

public interface Attribute {
    public QName getQName();

    public String getText();

    public Attribute setText(String var1);

    public Factory getFactory();
}

