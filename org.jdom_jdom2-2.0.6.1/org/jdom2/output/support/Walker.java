/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import org.jdom2.Content;

public interface Walker {
    public boolean isAllText();

    public boolean isAllWhitespace();

    public boolean hasNext();

    public Content next();

    public String text();

    public boolean isCDATA();
}

