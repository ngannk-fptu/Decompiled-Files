/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.StopException;
import freemarker.core.TemplateElement;
import freemarker.debug.impl.DebuggerService;
import freemarker.template.TemplateException;
import java.io.IOException;

@Deprecated
public class DebugBreak
extends TemplateElement {
    public DebugBreak(TemplateElement nestedBlock) {
        this.addChild(nestedBlock);
        this.copyLocationFrom(nestedBlock);
    }

    @Override
    protected TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        if (!DebuggerService.suspendEnvironment(env, this.getTemplate().getSourceName(), this.getChild(0).getBeginLine())) {
            return this.getChild(0).accept(env);
        }
        throw new StopException(env, "Stopped by debugger");
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            StringBuilder sb = new StringBuilder();
            sb.append("<#-- ");
            sb.append("debug break");
            if (this.getChildCount() == 0) {
                sb.append(" /-->");
            } else {
                sb.append(" -->");
                sb.append(this.getChild(0).getCanonicalForm());
                sb.append("<#--/ debug break -->");
            }
            return sb.toString();
        }
        return "debug break";
    }

    @Override
    String getNodeTypeSymbol() {
        return "#debug_break";
    }

    @Override
    int getParameterCount() {
        return 0;
    }

    @Override
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}

