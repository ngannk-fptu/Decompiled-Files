/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MixinNode;

public class InterfaceHelperClassNode
extends InnerClassNode {
    private List callSites = new ArrayList();

    public InterfaceHelperClassNode(ClassNode outerClass, String name, int modifiers, ClassNode superClass, List<String> callSites) {
        super(outerClass, name, modifiers, superClass, ClassHelper.EMPTY_TYPE_ARRAY, MixinNode.EMPTY_ARRAY);
        this.setCallSites(callSites);
    }

    public void setCallSites(List<String> cs) {
        this.callSites = cs != null ? cs : new ArrayList();
    }

    public List<String> getCallSites() {
        return this.callSites;
    }
}

