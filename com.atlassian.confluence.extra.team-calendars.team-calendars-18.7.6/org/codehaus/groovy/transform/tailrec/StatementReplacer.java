/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.util.ArrayList;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class StatementReplacer
extends CodeVisitorSupport
implements GroovyObject {
    private Closure<Boolean> when;
    private Closure<Statement> replaceWith;
    private int closureLevel;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public StatementReplacer() {
        MetaClass metaClass;
        int n;
        _closure2 _closure210;
        _closure1 _closure110;
        this.when = _closure110 = new _closure1(this, this);
        this.replaceWith = _closure210 = new _closure2(this, this);
        this.closureLevel = n = 0;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public void replaceIn(ASTNode root) {
        root.visit(this);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        int n = this.closureLevel;
        this.closureLevel = n + 1;
        try {
            super.visitClosureExpression(expression);
        }
        finally {
            int n2 = this.closureLevel;
            this.closureLevel = n2 - 1;
        }
    }

    @Override
    public void visitBlockStatement(BlockStatement block) {
        Reference<BlockStatement> block2 = new Reference<BlockStatement>(block);
        ArrayList<Statement> copyOfStatements = new ArrayList<Statement>(block2.get().getStatements());
        public class _visitBlockStatement_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference block;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _visitBlockStatement_closure3(Object _outerInstance, Object _thisObject, Reference block) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.block = reference = block;
            }

            public Object doCall(Statement statement, int index) {
                Reference<Integer> index2 = new Reference<Integer>(index);
                public class _closure9
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference block;
                    private /* synthetic */ Reference index;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;

                    public _closure9(Object _outerInstance, Object _thisObject, Reference block, Reference index) {
                        super(_outerInstance, _thisObject);
                        Reference reference;
                        Reference reference2;
                        this.block = reference2 = block;
                        this.index = reference = index;
                    }

                    public Object doCall(Statement node) {
                        Statement statement = node;
                        DefaultGroovyMethods.putAt(((BlockStatement)this.block.get()).getStatements(), DefaultTypeTransformation.intUnbox(this.index.get()), statement);
                        return statement;
                    }

                    public Object call(Statement node) {
                        return this.doCall(node);
                    }

                    public BlockStatement getBlock() {
                        return (BlockStatement)ScriptBytecodeAdapter.castToType(this.block.get(), BlockStatement.class);
                    }

                    public int getIndex() {
                        return DefaultTypeTransformation.intUnbox(this.index.get());
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure9.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }
                }
                ((StatementReplacer)this.getThisObject()).replaceIfNecessary(statement, new _closure9(this, this.getThisObject(), this.block, index2));
                return null;
            }

            public Object call(Statement statement, int index) {
                Reference<Integer> index2 = new Reference<Integer>(index);
                return this.doCall(statement, DefaultTypeTransformation.intUnbox(index2.get()));
            }

            public BlockStatement getBlock() {
                return (BlockStatement)ScriptBytecodeAdapter.castToType(this.block.get(), BlockStatement.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitBlockStatement_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        DefaultGroovyMethods.eachWithIndex(copyOfStatements, (Closure)new _visitBlockStatement_closure3(this, this, block2));
        super.visitBlockStatement(block2.get());
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        Reference<IfStatement> ifElse2 = new Reference<IfStatement>(ifElse);
        public class _visitIfElse_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference ifElse;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _visitIfElse_closure4(Object _outerInstance, Object _thisObject, Reference ifElse) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.ifElse = reference = ifElse;
            }

            public Object doCall(Statement s) {
                Statement statement = s;
                ((IfStatement)this.ifElse.get()).setIfBlock(statement);
                return statement;
            }

            public Object call(Statement s) {
                return this.doCall(s);
            }

            public IfStatement getIfElse() {
                return (IfStatement)ScriptBytecodeAdapter.castToType(this.ifElse.get(), IfStatement.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitIfElse_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        this.replaceIfNecessary(ifElse2.get().getIfBlock(), new _visitIfElse_closure4(this, this, ifElse2));
        public class _visitIfElse_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference ifElse;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _visitIfElse_closure5(Object _outerInstance, Object _thisObject, Reference ifElse) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.ifElse = reference = ifElse;
            }

            public Object doCall(Statement s) {
                Statement statement = s;
                ((IfStatement)this.ifElse.get()).setElseBlock(statement);
                return statement;
            }

            public Object call(Statement s) {
                return this.doCall(s);
            }

            public IfStatement getIfElse() {
                return (IfStatement)ScriptBytecodeAdapter.castToType(this.ifElse.get(), IfStatement.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitIfElse_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        this.replaceIfNecessary(ifElse2.get().getElseBlock(), new _visitIfElse_closure5(this, this, ifElse2));
        super.visitIfElse(ifElse2.get());
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        Reference<ForStatement> forLoop2 = new Reference<ForStatement>(forLoop);
        public class _visitForLoop_closure6
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference forLoop;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _visitForLoop_closure6(Object _outerInstance, Object _thisObject, Reference forLoop) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.forLoop = reference = forLoop;
            }

            public Object doCall(Statement s) {
                Statement statement = s;
                ((ForStatement)this.forLoop.get()).setLoopBlock(statement);
                return statement;
            }

            public Object call(Statement s) {
                return this.doCall(s);
            }

            public ForStatement getForLoop() {
                return (ForStatement)ScriptBytecodeAdapter.castToType(this.forLoop.get(), ForStatement.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitForLoop_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        this.replaceIfNecessary(forLoop2.get().getLoopBlock(), new _visitForLoop_closure6(this, this, forLoop2));
        super.visitForLoop(forLoop2.get());
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        Reference<WhileStatement> loop2 = new Reference<WhileStatement>(loop);
        public class _visitWhileLoop_closure7
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference loop;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _visitWhileLoop_closure7(Object _outerInstance, Object _thisObject, Reference loop) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.loop = reference = loop;
            }

            public Object doCall(Statement s) {
                Statement statement = s;
                ((WhileStatement)this.loop.get()).setLoopBlock(statement);
                return statement;
            }

            public Object call(Statement s) {
                return this.doCall(s);
            }

            public WhileStatement getLoop() {
                return (WhileStatement)ScriptBytecodeAdapter.castToType(this.loop.get(), WhileStatement.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitWhileLoop_closure7.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        this.replaceIfNecessary(loop2.get().getLoopBlock(), new _visitWhileLoop_closure7(this, this, loop2));
        super.visitWhileLoop(loop2.get());
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        Reference<DoWhileStatement> loop2 = new Reference<DoWhileStatement>(loop);
        public class _visitDoWhileLoop_closure8
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference loop;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _visitDoWhileLoop_closure8(Object _outerInstance, Object _thisObject, Reference loop) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.loop = reference = loop;
            }

            public Object doCall(Statement s) {
                Statement statement = s;
                ((DoWhileStatement)this.loop.get()).setLoopBlock(statement);
                return statement;
            }

            public Object call(Statement s) {
                return this.doCall(s);
            }

            public DoWhileStatement getLoop() {
                return (DoWhileStatement)ScriptBytecodeAdapter.castToType(this.loop.get(), DoWhileStatement.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitDoWhileLoop_closure8.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        this.replaceIfNecessary(loop2.get().getLoopBlock(), new _visitDoWhileLoop_closure8(this, this, loop2));
        super.visitDoWhileLoop(loop2.get());
    }

    private void replaceIfNecessary(Statement nodeToCheck, Closure replacementCode) {
        if (this.conditionFulfilled(nodeToCheck)) {
            Statement replacement = (Statement)ScriptBytecodeAdapter.castToType(this.replaceWith.call(new Object[]{nodeToCheck}), Statement.class);
            replacement.setSourcePosition(nodeToCheck);
            replacement.copyNodeMetaData(nodeToCheck);
            replacementCode.call((Object)replacement);
        }
    }

    private boolean conditionFulfilled(ASTNode nodeToCheck) {
        if (this.when.getMaximumNumberOfParameters() < 2) {
            return DefaultTypeTransformation.booleanUnbox(this.when.call(new Object[]{nodeToCheck}));
        }
        return DefaultTypeTransformation.booleanUnbox(this.when.call(nodeToCheck, this.isInClosure()));
    }

    private boolean isInClosure() {
        return this.closureLevel > 0;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StatementReplacer.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public Closure<Boolean> getWhen() {
        return this.when;
    }

    public void setWhen(Closure<Boolean> closure) {
        this.when = closure;
    }

    public Closure<Statement> getReplaceWith() {
        return this.replaceWith;
    }

    public void setReplaceWith(Closure<Statement> closure) {
        this.replaceWith = closure;
    }

    public int getClosureLevel() {
        return this.closureLevel;
    }

    public void setClosureLevel(int n) {
        this.closureLevel = n;
    }

    public class _closure1
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;

        public _closure1(Object _outerInstance, Object _thisObject) {
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Statement node) {
            return false;
        }

        public Object call(Statement node) {
            return this.doCall(node);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure1.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }
    }

    public class _closure2
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;

        public _closure2(Object _outerInstance, Object _thisObject) {
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Statement statement) {
            return statement;
        }

        public Object call(Statement statement) {
            return this.doCall(statement);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure2.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }
    }
}

