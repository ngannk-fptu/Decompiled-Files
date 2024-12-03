/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.BodyTagSupport
 */
package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.struts2.util.ComponentUtils;
import org.apache.struts2.views.jsp.TagUtils;

public class StrutsBodyTagSupport
extends BodyTagSupport {
    private static final long serialVersionUID = -1201668454354226175L;
    private boolean performClearTagStateForTagPoolingServers = false;

    protected ValueStack getStack() {
        return TagUtils.getStack(this.pageContext);
    }

    protected String findString(String expr) {
        return (String)this.findValue(expr, String.class);
    }

    protected Object findValue(String expr) {
        expr = ComponentUtils.stripExpression(expr);
        return this.getStack().findValue(expr);
    }

    protected Object findValue(String expr, Class<?> toType) {
        if (toType == String.class) {
            return TextParseUtil.translateVariables('%', expr, this.getStack());
        }
        expr = ComponentUtils.stripExpression(expr);
        return this.getStack().findValue(expr, toType);
    }

    /*
     * Exception decompiling
     */
    protected String toString(Throwable t) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    protected String getBody() {
        if (this.bodyContent == null) {
            return "";
        }
        return this.bodyContent.getString().trim();
    }

    public int doEndTag() throws JspException {
        this.clearTagStateForTagPoolingServers();
        return super.doEndTag();
    }

    public void release() {
        boolean originalPerformClearTagState = this.getPerformClearTagStateForTagPoolingServers();
        if (originalPerformClearTagState) {
            this.clearTagStateForTagPoolingServers();
        } else {
            this.setPerformClearTagStateForTagPoolingServers(true);
            this.clearTagStateForTagPoolingServers();
            this.setPerformClearTagStateForTagPoolingServers(originalPerformClearTagState);
        }
        super.release();
    }

    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        this.performClearTagStateForTagPoolingServers = performClearTagStateForTagPoolingServers;
    }

    protected boolean getPerformClearTagStateForTagPoolingServers() {
        return this.performClearTagStateForTagPoolingServers;
    }

    protected void clearTagStateForTagPoolingServers() {
        if (!this.getPerformClearTagStateForTagPoolingServers()) {
            return;
        }
        this.setBodyContent(null);
        this.setId(null);
    }
}

