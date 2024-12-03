/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.LocalContext;
import freemarker.core.Macro;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateNullModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class BodyInstruction
extends TemplateElement {
    private List bodyParameters;

    BodyInstruction(List bodyParameters) {
        this.bodyParameters = bodyParameters;
    }

    List getBodyParameters() {
        return this.bodyParameters;
    }

    @Override
    TemplateElement[] accept(Environment env) throws IOException, TemplateException {
        Context bodyContext = new Context(env);
        env.invokeNestedContent(bodyContext);
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        if (this.bodyParameters != null) {
            for (int i = 0; i < this.bodyParameters.size(); ++i) {
                sb.append(' ');
                sb.append(((Expression)this.bodyParameters.get(i)).getCanonicalForm());
            }
        }
        if (canonical) {
            sb.append('>');
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#nested";
    }

    @Override
    int getParameterCount() {
        return this.bodyParameters != null ? this.bodyParameters.size() : 0;
    }

    @Override
    Object getParameterValue(int idx) {
        this.checkIndex(idx);
        return this.bodyParameters.get(idx);
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        this.checkIndex(idx);
        return ParameterRole.PASSED_VALUE;
    }

    private void checkIndex(int idx) {
        if (this.bodyParameters == null || idx >= this.bodyParameters.size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    boolean isShownInStackTrace() {
        return true;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    class Context
    implements LocalContext {
        Macro.Context invokingMacroContext;
        Environment.Namespace bodyVars;

        Context(Environment env) throws TemplateException {
            this.invokingMacroContext = env.getCurrentMacroContext();
            List<String> bodyParameterNames = this.invokingMacroContext.nestedContentParameterNames;
            if (BodyInstruction.this.bodyParameters != null) {
                for (int i = 0; i < BodyInstruction.this.bodyParameters.size(); ++i) {
                    Expression exp = (Expression)BodyInstruction.this.bodyParameters.get(i);
                    TemplateModel tm = exp.eval(env);
                    if (bodyParameterNames == null || i >= bodyParameterNames.size()) continue;
                    String bodyParameterName = bodyParameterNames.get(i);
                    if (this.bodyVars == null) {
                        this.bodyVars = new Environment.Namespace(env);
                    }
                    this.bodyVars.put(bodyParameterName, tm != null ? tm : (BodyInstruction.this.getTemplate().getConfiguration().getFallbackOnNullLoopVariable() ? null : TemplateNullModel.INSTANCE));
                }
            }
        }

        @Override
        public TemplateModel getLocalVariable(String name) throws TemplateModelException {
            return this.bodyVars == null ? null : this.bodyVars.get(name);
        }

        @Override
        public Collection getLocalVariableNames() {
            List<String> bodyParameterNames = this.invokingMacroContext.nestedContentParameterNames;
            return bodyParameterNames == null ? Collections.EMPTY_LIST : bodyParameterNames;
        }
    }
}

