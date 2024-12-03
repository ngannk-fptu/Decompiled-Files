/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.filter;

import java.io.Serializable;
import javax.xml.namespace.QName;

public interface ParseFilter
extends Cloneable,
Serializable {
    public Object clone() throws CloneNotSupportedException;

    public boolean acceptable(QName var1);

    public boolean acceptable(QName var1, QName var2);

    public boolean getIgnoreComments();

    public boolean getIgnoreWhitespace();

    public boolean getIgnoreProcessingInstructions();

    public ParseFilter setIgnoreComments(boolean var1);

    public ParseFilter setIgnoreWhitespace(boolean var1);

    public ParseFilter setIgnoreProcessingInstructions(boolean var1);
}

