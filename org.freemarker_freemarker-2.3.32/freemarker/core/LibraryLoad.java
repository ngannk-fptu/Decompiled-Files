/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core._CoreStringUtils;
import freemarker.core._DelayedGetMessage;
import freemarker.core._DelayedJQuote;
import freemarker.core._MiscTemplateException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;

@Deprecated
public final class LibraryLoad
extends TemplateElement {
    private Expression importedTemplateNameExp;
    private String targetNsVarName;

    LibraryLoad(Template template, Expression templateName, String targetNsVarName) {
        this.targetNsVarName = targetNsVarName;
        this.importedTemplateNameExp = templateName;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        String fullImportedTemplateName;
        String importedTemplateName = this.importedTemplateNameExp.evalAndCoerceToPlainText(env);
        try {
            fullImportedTemplateName = env.toFullTemplateName(this.getTemplate().getName(), importedTemplateName);
        }
        catch (MalformedTemplateNameException e) {
            throw new _MiscTemplateException((Throwable)e, env, "Malformed template name ", new _DelayedJQuote(e.getTemplateName()), ":\n", e.getMalformednessDescription());
        }
        try {
            env.importLib(fullImportedTemplateName, this.targetNsVarName);
        }
        catch (IOException e) {
            throw new _MiscTemplateException((Throwable)e, env, "Template importing failed (for parameter value ", new _DelayedJQuote(importedTemplateName), "):\n", new _DelayedGetMessage(e));
        }
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(this.getNodeTypeSymbol());
        buf.append(' ');
        buf.append(this.importedTemplateNameExp.getCanonicalForm());
        buf.append(" as ");
        buf.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.targetNsVarName));
        if (canonical) {
            buf.append("/>");
        }
        return buf.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#import";
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.importedTemplateNameExp;
            }
            case 1: {
                return this.targetNsVarName;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.TEMPLATE_NAME;
            }
            case 1: {
                return ParameterRole.NAMESPACE;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    public String getTemplateName() {
        return this.importedTemplateNameExp.toString();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override
    boolean isShownInStackTrace() {
        return true;
    }
}

