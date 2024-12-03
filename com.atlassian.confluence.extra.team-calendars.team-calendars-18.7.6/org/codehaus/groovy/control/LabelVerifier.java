/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.SourceUnit;

public class LabelVerifier
extends ClassCodeVisitorSupport {
    private SourceUnit source;
    private LinkedList<String> visitedLabels;
    private LinkedList<ContinueStatement> continueLabels;
    private LinkedList<BreakStatement> breakLabels;
    boolean inLoop = false;
    boolean inSwitch = false;

    public LabelVerifier(SourceUnit src) {
        this.source = src;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    private void init() {
        this.visitedLabels = new LinkedList();
        this.continueLabels = new LinkedList();
        this.breakLabels = new LinkedList();
        this.inLoop = false;
        this.inSwitch = false;
    }

    @Override
    protected void visitClassCodeContainer(Statement code) {
        this.init();
        super.visitClassCodeContainer(code);
        this.assertNoLabelsMissed();
    }

    @Override
    public void visitStatement(Statement statement) {
        List<String> labels = statement.getStatementLabels();
        if (labels != null) {
            for (String label : labels) {
                Iterator iter;
                if (this.breakLabels != null) {
                    iter = this.breakLabels.iterator();
                    while (iter.hasNext()) {
                        if (!((BreakStatement)iter.next()).getLabel().equals(label)) continue;
                        iter.remove();
                    }
                }
                if (this.continueLabels != null) {
                    iter = this.continueLabels.iterator();
                    while (iter.hasNext()) {
                        if (!((ContinueStatement)iter.next()).getLabel().equals(label)) continue;
                        iter.remove();
                    }
                }
                if (this.visitedLabels == null) continue;
                this.visitedLabels.add(label);
            }
        }
        super.visitStatement(statement);
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        boolean oldInLoop = this.inLoop;
        this.inLoop = true;
        super.visitForLoop(forLoop);
        this.inLoop = oldInLoop;
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        boolean oldInLoop = this.inLoop;
        this.inLoop = true;
        super.visitDoWhileLoop(loop);
        this.inLoop = oldInLoop;
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        boolean oldInLoop = this.inLoop;
        this.inLoop = true;
        super.visitWhileLoop(loop);
        this.inLoop = oldInLoop;
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
        boolean hasNamedLabel;
        String label = statement.getLabel();
        boolean bl = hasNamedLabel = label != null;
        if (!(hasNamedLabel || this.inLoop || this.inSwitch)) {
            this.addError("the break statement is only allowed inside loops or switches", statement);
        } else if (hasNamedLabel && !this.inLoop) {
            this.addError("the break statement with named label is only allowed inside loops", statement);
        }
        if (label != null) {
            boolean found = false;
            for (String element : this.visitedLabels) {
                if (!element.equals(label)) continue;
                found = true;
                break;
            }
            if (!found) {
                this.breakLabels.add(statement);
            }
        }
        super.visitBreakStatement(statement);
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
        boolean hasNamedLabel;
        String label = statement.getLabel();
        boolean bl = hasNamedLabel = label != null;
        if (!hasNamedLabel && !this.inLoop) {
            this.addError("the continue statement is only allowed inside loops", statement);
        }
        if (label != null) {
            boolean found = false;
            for (String element : this.visitedLabels) {
                if (!element.equals(label)) continue;
                found = true;
                break;
            }
            if (!found) {
                this.continueLabels.add(statement);
            }
        }
        super.visitContinueStatement(statement);
    }

    protected void assertNoLabelsMissed() {
        for (ContinueStatement continueStatement : this.continueLabels) {
            this.addError("continue to missing label", continueStatement);
        }
        for (BreakStatement breakStatement : this.breakLabels) {
            this.addError("break to missing label", breakStatement);
        }
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        boolean oldInSwitch = this.inSwitch;
        this.inSwitch = true;
        super.visitSwitch(statement);
        this.inSwitch = oldInSwitch;
    }
}

