/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.jssrc.internal;

import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.JsExprUtils;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

class JsCodeBuilder {
    private static final String SPACES = "                    ";
    private static final int INDENT_SIZE = 2;
    private final StringBuilder code;
    private final SoyJsSrcOptions.CodeStyle codeStyle;
    private String indent;
    private Deque<Pair<String, Boolean>> outputVars;
    private String currOutputVarName;
    private boolean currOutputVarIsInited;

    public JsCodeBuilder(SoyJsSrcOptions.CodeStyle codeStyle) {
        this.codeStyle = codeStyle;
        this.code = new StringBuilder();
        this.indent = "";
        this.outputVars = new ArrayDeque<Pair<String, Boolean>>();
        this.currOutputVarName = null;
        this.currOutputVarIsInited = false;
    }

    public void increaseIndent() throws SoySyntaxException {
        this.changeIndentHelper(1);
    }

    public void increaseIndentTwice() throws SoySyntaxException {
        this.changeIndentHelper(2);
    }

    public void decreaseIndent() throws SoySyntaxException {
        this.changeIndentHelper(-1);
    }

    public void decreaseIndentTwice() throws SoySyntaxException {
        this.changeIndentHelper(-2);
    }

    private void changeIndentHelper(int chg) throws SoySyntaxException {
        int newIndentDepth = this.indent.length() + chg * 2;
        if (newIndentDepth < 0) {
            throw SoySyntaxException.createWithoutMetaInfo("Indent is less than 0 spaces!");
        }
        if (newIndentDepth > 20) {
            throw SoySyntaxException.createWithoutMetaInfo("Indent is more than 20 spaces!");
        }
        this.indent = SPACES.substring(0, newIndentDepth);
    }

    public void pushOutputVar(String outputVarName) {
        this.outputVars.push(Pair.of(outputVarName, false));
        this.currOutputVarName = outputVarName;
        this.currOutputVarIsInited = false;
    }

    public void popOutputVar() {
        this.outputVars.pop();
        Pair<String, Boolean> topPair = this.outputVars.peek();
        if (topPair != null) {
            this.currOutputVarName = topPair.getFirst();
            this.currOutputVarIsInited = topPair.getSecond();
        } else {
            this.currOutputVarName = null;
            this.currOutputVarIsInited = false;
        }
    }

    public void setOutputVarInited() {
        this.outputVars.pop();
        this.outputVars.push(Pair.of(this.currOutputVarName, true));
        this.currOutputVarIsInited = true;
    }

    public String getOutputVarName() {
        return this.currOutputVarName;
    }

    public JsCodeBuilder append(String ... jsCodeFragments) {
        for (String jsCodeFragment : jsCodeFragments) {
            this.code.append(jsCodeFragment);
        }
        return this;
    }

    public JsCodeBuilder appendLine(String ... jsCodeFragments) {
        this.code.append(this.indent);
        this.append(jsCodeFragments);
        this.code.append("\n");
        return this;
    }

    public JsCodeBuilder appendLineStart(String ... jsCodeFragments) {
        this.code.append(this.indent);
        this.append(jsCodeFragments);
        return this;
    }

    public JsCodeBuilder appendLineEnd(String ... jsCodeFragments) {
        this.append(jsCodeFragments);
        this.code.append("\n");
        return this;
    }

    public JsCodeBuilder appendOutputVarName() {
        this.code.append(this.currOutputVarName);
        return this;
    }

    public void initOutputVarIfNecessary() {
        if (this.currOutputVarIsInited) {
            return;
        }
        if (this.codeStyle == SoyJsSrcOptions.CodeStyle.STRINGBUILDER) {
            this.appendLine("var ", this.currOutputVarName, " = new soy.StringBuilder();");
        } else {
            this.appendLine("var ", this.currOutputVarName, " = '';");
        }
        this.setOutputVarInited();
    }

    public void addToOutputVar(List<JsExpr> jsExprs) {
        if (this.codeStyle == SoyJsSrcOptions.CodeStyle.STRINGBUILDER) {
            StringBuilder commaSeparatedJsExprsSb = new StringBuilder();
            boolean isFirst = true;
            for (JsExpr jsExpr : jsExprs) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    commaSeparatedJsExprsSb.append(", ");
                }
                commaSeparatedJsExprsSb.append(jsExpr.getText());
            }
            if (this.currOutputVarIsInited) {
                this.appendLine(this.currOutputVarName, ".append(", commaSeparatedJsExprsSb.toString(), ");");
            } else {
                this.appendLine("var ", this.currOutputVarName, " = new soy.StringBuilder(", commaSeparatedJsExprsSb.toString(), ");");
                this.setOutputVarInited();
            }
        } else if (this.currOutputVarIsInited) {
            this.appendLine(this.currOutputVarName, " += ", JsExprUtils.concatJsExprs(jsExprs).getText(), ";");
        } else {
            String contents = JsExprUtils.concatJsExprsForceString(jsExprs).getText();
            this.appendLine("var ", this.currOutputVarName, " = ", contents, ";");
            this.setOutputVarInited();
        }
    }

    public String getCode() {
        return this.code.toString();
    }
}

