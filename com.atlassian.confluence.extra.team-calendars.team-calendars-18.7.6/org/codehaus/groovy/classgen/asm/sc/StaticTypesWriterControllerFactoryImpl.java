/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.classgen.asm.WriterControllerFactory;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesWriterController;

public class StaticTypesWriterControllerFactoryImpl
implements WriterControllerFactory {
    @Override
    public WriterController makeController(WriterController normalController) {
        return new StaticTypesWriterController(normalController);
    }
}

