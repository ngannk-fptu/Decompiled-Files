/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.classgen.BytecodeSequence;

public class ReturnAdder {
    private static final ReturnStatementListener DEFAULT_LISTENER = new ReturnStatementListener(){

        @Override
        public void returnStatementAdded(ReturnStatement returnStatement) {
        }
    };
    private final boolean doAdd;
    private final ReturnStatementListener listener;

    public ReturnAdder() {
        this.doAdd = true;
        this.listener = DEFAULT_LISTENER;
    }

    public ReturnAdder(ReturnStatementListener listener) {
        this.listener = listener;
        this.doAdd = false;
    }

    @Deprecated
    public static void addReturnIfNeeded(MethodNode node) {
        ReturnAdder adder = new ReturnAdder();
        adder.visitMethod(node);
    }

    public void visitMethod(MethodNode node) {
        Statement statement = node.getCode();
        if (!node.isVoidMethod()) {
            if (statement != null) {
                Statement code = this.addReturnsIfNeeded(statement, node.getVariableScope());
                if (this.doAdd) {
                    node.setCode(code);
                }
            }
        } else if (!node.isAbstract() && node.getReturnType().redirect() != ClassHelper.VOID_TYPE && !(statement instanceof BytecodeSequence)) {
            BlockStatement newBlock = new BlockStatement();
            Statement code = node.getCode();
            if (code instanceof BlockStatement) {
                newBlock.setVariableScope(((BlockStatement)code).getVariableScope());
            }
            if (statement instanceof BlockStatement) {
                newBlock.addStatements(((BlockStatement)statement).getStatements());
            } else {
                newBlock.addStatement(statement);
            }
            ReturnStatement returnStatement = ReturnStatement.RETURN_NULL_OR_VOID;
            this.listener.returnStatementAdded(returnStatement);
            newBlock.addStatement(returnStatement);
            newBlock.setSourcePosition(statement);
            if (this.doAdd) {
                node.setCode(newBlock);
            }
        }
    }

    private Statement addReturnsIfNeeded(Statement statement, VariableScope scope) {
        if (statement instanceof ReturnStatement || statement instanceof BytecodeSequence || statement instanceof ThrowStatement) {
            return statement;
        }
        if (statement instanceof EmptyStatement) {
            ReturnStatement returnStatement = new ReturnStatement(ConstantExpression.NULL);
            this.listener.returnStatementAdded(returnStatement);
            return returnStatement;
        }
        if (statement instanceof ExpressionStatement) {
            ExpressionStatement expStmt = (ExpressionStatement)statement;
            Expression expr = expStmt.getExpression();
            ReturnStatement ret = new ReturnStatement(expr);
            ret.setSourcePosition(expr);
            ret.setStatementLabel(statement.getStatementLabel());
            this.listener.returnStatementAdded(ret);
            return ret;
        }
        if (statement instanceof SynchronizedStatement) {
            SynchronizedStatement sync = (SynchronizedStatement)statement;
            Statement code = this.addReturnsIfNeeded(sync.getCode(), scope);
            if (this.doAdd) {
                sync.setCode(code);
            }
            return sync;
        }
        if (statement instanceof IfStatement) {
            IfStatement ifs = (IfStatement)statement;
            Statement ifBlock = this.addReturnsIfNeeded(ifs.getIfBlock(), scope);
            Statement elseBlock = this.addReturnsIfNeeded(ifs.getElseBlock(), scope);
            if (this.doAdd) {
                ifs.setIfBlock(ifBlock);
                ifs.setElseBlock(elseBlock);
            }
            return ifs;
        }
        if (statement instanceof SwitchStatement) {
            SwitchStatement swi = (SwitchStatement)statement;
            for (CaseStatement caseStatement : swi.getCaseStatements()) {
                Statement code = this.adjustSwitchCaseCode(caseStatement.getCode(), scope, false);
                if (!this.doAdd) continue;
                caseStatement.setCode(code);
            }
            Statement defaultStatement = this.adjustSwitchCaseCode(swi.getDefaultStatement(), scope, true);
            if (this.doAdd) {
                swi.setDefaultStatement(defaultStatement);
            }
            return swi;
        }
        if (statement instanceof TryCatchStatement) {
            boolean hasFinally;
            TryCatchStatement trys = (TryCatchStatement)statement;
            final boolean[] missesReturn = new boolean[1];
            new ReturnAdder(new ReturnStatementListener(){

                @Override
                public void returnStatementAdded(ReturnStatement returnStatement) {
                    missesReturn[0] = true;
                }
            }).addReturnsIfNeeded(trys.getFinallyStatement(), scope);
            boolean bl = hasFinally = !(trys.getFinallyStatement() instanceof EmptyStatement);
            if (hasFinally && !missesReturn[0]) {
                return trys;
            }
            Statement tryStatement = this.addReturnsIfNeeded(trys.getTryStatement(), scope);
            if (this.doAdd) {
                trys.setTryStatement(tryStatement);
            }
            int len = trys.getCatchStatements().size();
            for (int i = 0; i != len; ++i) {
                CatchStatement catchStatement = trys.getCatchStatement(i);
                Statement code = this.addReturnsIfNeeded(catchStatement.getCode(), scope);
                if (!this.doAdd) continue;
                catchStatement.setCode(code);
            }
            return trys;
        }
        if (statement instanceof BlockStatement) {
            BlockStatement block = (BlockStatement)statement;
            List<Statement> list = block.getStatements();
            if (!list.isEmpty()) {
                int idx = list.size() - 1;
                Statement last = this.addReturnsIfNeeded(list.get(idx), block.getVariableScope());
                if (this.doAdd) {
                    list.set(idx, last);
                }
                if (!ReturnAdder.statementReturns(last)) {
                    ReturnStatement returnStatement = new ReturnStatement(ConstantExpression.NULL);
                    this.listener.returnStatementAdded(returnStatement);
                    if (this.doAdd) {
                        list.add(returnStatement);
                    }
                }
            } else {
                ReturnStatement ret = new ReturnStatement(ConstantExpression.NULL);
                ret.setSourcePosition(block);
                this.listener.returnStatementAdded(ret);
                return ret;
            }
            BlockStatement newBlock = new BlockStatement(list, block.getVariableScope());
            newBlock.setSourcePosition(block);
            return newBlock;
        }
        if (statement == null) {
            ReturnStatement returnStatement = new ReturnStatement(ConstantExpression.NULL);
            this.listener.returnStatementAdded(returnStatement);
            return returnStatement;
        }
        ArrayList<Statement> list = new ArrayList<Statement>();
        list.add(statement);
        ReturnStatement returnStatement = new ReturnStatement(ConstantExpression.NULL);
        this.listener.returnStatementAdded(returnStatement);
        list.add(returnStatement);
        BlockStatement newBlock = new BlockStatement(list, new VariableScope(scope));
        newBlock.setSourcePosition(statement);
        return newBlock;
    }

    private Statement adjustSwitchCaseCode(Statement statement, VariableScope scope, boolean defaultCase) {
        List<Statement> list;
        if (statement instanceof BlockStatement && !(list = ((BlockStatement)statement).getStatements()).isEmpty()) {
            int idx = list.size() - 1;
            Statement last = list.get(idx);
            if (last instanceof BreakStatement) {
                if (this.doAdd) {
                    list.remove(idx);
                    return this.addReturnsIfNeeded(statement, scope);
                }
                BlockStatement newStmt = new BlockStatement();
                for (int i = 0; i < idx; ++i) {
                    newStmt.addStatement(list.get(i));
                }
                return this.addReturnsIfNeeded(newStmt, scope);
            }
            if (defaultCase) {
                return this.addReturnsIfNeeded(statement, scope);
            }
        }
        return statement;
    }

    private static boolean statementReturns(Statement last) {
        return last instanceof ReturnStatement || last instanceof BlockStatement || last instanceof IfStatement || last instanceof ExpressionStatement || last instanceof EmptyStatement || last instanceof TryCatchStatement || last instanceof BytecodeSequence || last instanceof ThrowStatement || last instanceof SynchronizedStatement;
    }

    public static interface ReturnStatementListener {
        public void returnStatementAdded(ReturnStatement var1);
    }
}

