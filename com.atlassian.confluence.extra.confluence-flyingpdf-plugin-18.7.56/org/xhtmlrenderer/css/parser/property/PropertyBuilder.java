/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;

public interface PropertyBuilder {
    public List buildDeclarations(CSSName var1, List var2, int var3, boolean var4, boolean var5);

    public List buildDeclarations(CSSName var1, List var2, int var3, boolean var4);
}

