/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.macro.Macro;
import org.radeox.macro.parameter.MacroParameter;

public abstract class BaseMacro
implements Macro {
    protected InitialRenderContext initialContext;
    protected String description = " ";
    protected String[] paramDescription = new String[]{"unexplained, lazy programmer, probably [funzel]"};

    public abstract String getName();

    public String getDescription() {
        return this.description;
    }

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public void setInitialContext(InitialRenderContext context) {
        this.initialContext = context;
    }

    public abstract void execute(Writer var1, MacroParameter var2) throws IllegalArgumentException, IOException;

    public String toString() {
        return this.getName();
    }

    public int compareTo(Object object) {
        Macro macro = (Macro)object;
        return this.getName().compareTo(macro.getName());
    }
}

