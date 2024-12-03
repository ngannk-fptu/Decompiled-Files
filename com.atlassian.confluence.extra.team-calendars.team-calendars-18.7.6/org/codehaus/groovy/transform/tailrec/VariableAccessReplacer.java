/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.transform.tailrec.AstHelper;
import org.codehaus.groovy.transform.tailrec.VariableExpressionReplacer;
import org.codehaus.groovy.transform.tailrec.VariableReplacedListener;

public class VariableAccessReplacer
implements GroovyObject {
    private Map<String, Map> nameAndTypeMapping;
    private VariableReplacedListener listener;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public VariableAccessReplacer() {
        MetaClass metaClass;
        VariableReplacedListener variableReplacedListener;
        Map map;
        this.nameAndTypeMapping = map = ScriptBytecodeAdapter.createMap(new Object[0]);
        this.listener = variableReplacedListener = VariableReplacedListener.NULL;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public void replaceIn(ASTNode root) {
        public class _replaceIn_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _replaceIn_closure1(Object _outerInstance, Object _thisObject) {
                super(_outerInstance, _thisObject);
            }

            public Object doCall(VariableExpression expr) {
                return ((VariableAccessReplacer)ScriptBytecodeAdapter.castToType(this.getThisObject(), VariableAccessReplacer.class)).getNameAndTypeMapping().containsKey(expr.getName());
            }

            public Object call(VariableExpression expr) {
                return this.doCall(expr);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _replaceIn_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        _replaceIn_closure1 whenParam = new _replaceIn_closure1(this, this);
        public class _replaceIn_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _replaceIn_closure2(Object _outerInstance, Object _thisObject) {
                super(_outerInstance, _thisObject);
            }

            public Object doCall(VariableExpression expr) {
                Map nameAndType = (Map)ScriptBytecodeAdapter.castToType(DefaultGroovyMethods.getAt(((VariableAccessReplacer)ScriptBytecodeAdapter.castToType(this.getThisObject(), VariableAccessReplacer.class)).getNameAndTypeMapping(), expr.getName()), Map.class);
                VariableExpression newVar = AstHelper.createVariableReference(nameAndType);
                ((VariableAccessReplacer)ScriptBytecodeAdapter.castToType(this.getThisObject(), VariableAccessReplacer.class)).getListener().variableReplaced(expr, newVar);
                return newVar;
            }

            public Object call(VariableExpression expr) {
                return this.doCall(expr);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _replaceIn_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        _replaceIn_closure2 replaceWithLocalVariable = new _replaceIn_closure2(this, this);
        VariableExpressionReplacer variableExpressionReplacer = new VariableExpressionReplacer();
        _replaceIn_closure1 _replaceIn_closure12 = whenParam;
        variableExpressionReplacer.setWhen(_replaceIn_closure12);
        _replaceIn_closure2 _replaceIn_closure22 = replaceWithLocalVariable;
        variableExpressionReplacer.setReplaceWith(_replaceIn_closure22);
        variableExpressionReplacer.replaceIn(root);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != VariableAccessReplacer.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public static /* synthetic */ Map<String, Map> pfaccess$0(VariableAccessReplacer $that) {
        return $that.getNameAndTypeMapping();
    }

    public static /* synthetic */ VariableReplacedListener pfaccess$1(VariableAccessReplacer $that) {
        return $that.getListener();
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

    public Map<String, Map> getNameAndTypeMapping() {
        return this.nameAndTypeMapping;
    }

    public void setNameAndTypeMapping(Map<String, Map> map) {
        this.nameAndTypeMapping = map;
    }

    public VariableReplacedListener getListener() {
        return this.listener;
    }

    public void setListener(VariableReplacedListener variableReplacedListener) {
        this.listener = variableReplacedListener;
    }
}

