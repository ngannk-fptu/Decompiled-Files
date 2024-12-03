/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import java.util.LinkedList;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.asm.MopWriter;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesWriterController;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class StaticCompilationMopWriter
extends MopWriter {
    public static final MopWriter.Factory FACTORY = new MopWriter.Factory(){

        @Override
        public MopWriter create(WriterController controller) {
            return new StaticCompilationMopWriter(controller);
        }
    };
    private final StaticTypesWriterController controller;

    public StaticCompilationMopWriter(WriterController wc) {
        super(wc);
        this.controller = (StaticTypesWriterController)wc;
    }

    @Override
    public void createMopMethods() {
        ClassNode classNode = this.controller.getClassNode();
        LinkedList requiredMopMethods = (LinkedList)classNode.getNodeMetaData((Object)StaticTypesMarker.SUPER_MOP_METHOD_REQUIRED);
        if (requiredMopMethods != null) {
            this.generateMopCalls(requiredMopMethods, false);
        }
    }
}

