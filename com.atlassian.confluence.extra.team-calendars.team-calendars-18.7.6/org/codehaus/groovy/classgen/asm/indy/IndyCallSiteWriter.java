/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.indy;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.classgen.asm.CallSiteWriter;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.classgen.asm.indy.InvokeDynamicWriter;

public class IndyCallSiteWriter
extends CallSiteWriter {
    private WriterController controller;

    public IndyCallSiteWriter(WriterController controller) {
        super(controller);
        this.controller = controller;
    }

    @Override
    public void generateCallSiteArray() {
    }

    @Override
    public void makeCallSite(Expression receiver, String message, Expression arguments, boolean safe, boolean implicitThis, boolean callCurrent, boolean callStatic) {
    }

    @Override
    public void makeSingleArgumentCall(Expression receiver, String message, Expression arguments) {
    }

    @Override
    public void prepareCallSite(String message) {
    }

    @Override
    public void makeSiteEntry() {
    }

    @Override
    public void makeCallSiteArrayInitializer() {
    }

    @Override
    public void makeGetPropertySite(Expression receiver, String name, boolean safe, boolean implicitThis) {
        InvokeDynamicWriter idw = (InvokeDynamicWriter)this.controller.getInvocationWriter();
        idw.writeGetProperty(receiver, name, safe, implicitThis, false);
    }

    @Override
    public void makeGroovyObjectGetPropertySite(Expression receiver, String name, boolean safe, boolean implicitThis) {
        InvokeDynamicWriter idw = (InvokeDynamicWriter)this.controller.getInvocationWriter();
        idw.writeGetProperty(receiver, name, safe, implicitThis, true);
    }
}

