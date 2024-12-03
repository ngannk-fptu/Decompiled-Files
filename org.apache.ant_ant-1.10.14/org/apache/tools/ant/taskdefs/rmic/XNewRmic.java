/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.taskdefs.rmic.ForkingSunRmic;
import org.apache.tools.ant.types.Commandline;

public class XNewRmic
extends ForkingSunRmic {
    public static final String COMPILER_NAME = "xnew";

    @Override
    protected Commandline setupRmicCommand() {
        String[] options = new String[]{"-Xnew"};
        return super.setupRmicCommand(options);
    }
}

