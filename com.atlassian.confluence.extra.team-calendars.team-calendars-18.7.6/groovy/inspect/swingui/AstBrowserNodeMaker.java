/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import java.util.List;

public interface AstBrowserNodeMaker<T> {
    public T makeNode(Object var1);

    public T makeNodeWithProperties(Object var1, List<List<String>> var2);
}

