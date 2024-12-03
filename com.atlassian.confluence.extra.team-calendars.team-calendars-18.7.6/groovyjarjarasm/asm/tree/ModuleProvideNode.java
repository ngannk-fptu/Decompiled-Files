/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.tree;

import groovyjarjarasm.asm.ModuleVisitor;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ModuleProvideNode {
    public String service;
    public List<String> providers;

    public ModuleProvideNode(String service, List<String> providers) {
        this.service = service;
        this.providers = providers;
    }

    public void accept(ModuleVisitor mv) {
        mv.visitProvide(this.service, this.providers.toArray(new String[0]));
    }
}

