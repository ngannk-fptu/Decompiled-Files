/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MissingMethodException;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.InvokerHelper;

public abstract class BuilderSupport
extends GroovyObjectSupport {
    private Object current;
    private Closure nameMappingClosure;
    private final BuilderSupport proxyBuilder;

    public BuilderSupport() {
        this.proxyBuilder = this;
    }

    public BuilderSupport(BuilderSupport proxyBuilder) {
        this(null, proxyBuilder);
    }

    public BuilderSupport(Closure nameMappingClosure, BuilderSupport proxyBuilder) {
        this.nameMappingClosure = nameMappingClosure;
        this.proxyBuilder = proxyBuilder;
    }

    public Object invokeMethod(String methodName) {
        return this.invokeMethod(methodName, null);
    }

    @Override
    public Object invokeMethod(String methodName, Object args) {
        Object name = this.getName(methodName);
        return this.doInvokeMethod(methodName, name, args);
    }

    protected Object doInvokeMethod(String methodName, Object name, Object args) {
        Object node = null;
        Closure closure = null;
        List list = InvokerHelper.asList(args);
        switch (list.size()) {
            case 0: {
                node = this.proxyBuilder.createNode(name);
                break;
            }
            case 1: {
                Object object = list.get(0);
                if (object instanceof Map) {
                    node = this.proxyBuilder.createNode(name, (Map)object);
                    break;
                }
                if (object instanceof Closure) {
                    closure = (Closure)object;
                    node = this.proxyBuilder.createNode(name);
                    break;
                }
                node = this.proxyBuilder.createNode(name, object);
                break;
            }
            case 2: {
                Object object1 = list.get(0);
                Object object2 = list.get(1);
                if (object1 instanceof Map) {
                    if (object2 instanceof Closure) {
                        closure = (Closure)object2;
                        node = this.proxyBuilder.createNode(name, (Map)object1);
                        break;
                    }
                    node = this.proxyBuilder.createNode(name, (Map)object1, object2);
                    break;
                }
                if (object2 instanceof Closure) {
                    closure = (Closure)object2;
                    node = this.proxyBuilder.createNode(name, object1);
                    break;
                }
                if (object2 instanceof Map) {
                    node = this.proxyBuilder.createNode(name, (Map)object2, object1);
                    break;
                }
                throw new MissingMethodException(name.toString(), this.getClass(), list.toArray(), false);
            }
            case 3: {
                Object arg0 = list.get(0);
                Object arg1 = list.get(1);
                Object arg2 = list.get(2);
                if (arg0 instanceof Map && arg2 instanceof Closure) {
                    closure = (Closure)arg2;
                    node = this.proxyBuilder.createNode(name, (Map)arg0, arg1);
                    break;
                }
                if (arg1 instanceof Map && arg2 instanceof Closure) {
                    closure = (Closure)arg2;
                    node = this.proxyBuilder.createNode(name, (Map)arg1, arg0);
                    break;
                }
                throw new MissingMethodException(name.toString(), this.getClass(), list.toArray(), false);
            }
            default: {
                throw new MissingMethodException(name.toString(), this.getClass(), list.toArray(), false);
            }
        }
        if (this.current != null) {
            this.proxyBuilder.setParent(this.current, node);
        }
        if (closure != null) {
            Object oldCurrent = this.getCurrent();
            this.setCurrent(node);
            this.setClosureDelegate(closure, node);
            try {
                closure.call();
            }
            catch (Exception e) {
                throw new GroovyRuntimeException(e);
            }
            this.setCurrent(oldCurrent);
        }
        this.proxyBuilder.nodeCompleted(this.current, node);
        return this.proxyBuilder.postNodeCompletion(this.current, node);
    }

    protected void setClosureDelegate(Closure closure, Object node) {
        closure.setDelegate(this);
    }

    protected abstract void setParent(Object var1, Object var2);

    protected abstract Object createNode(Object var1);

    protected abstract Object createNode(Object var1, Object var2);

    protected abstract Object createNode(Object var1, Map var2);

    protected abstract Object createNode(Object var1, Map var2, Object var3);

    protected Object getName(String methodName) {
        if (this.nameMappingClosure != null) {
            return this.nameMappingClosure.call((Object)methodName);
        }
        return methodName;
    }

    protected void nodeCompleted(Object parent, Object node) {
    }

    protected Object postNodeCompletion(Object parent, Object node) {
        return node;
    }

    protected Object getCurrent() {
        return this.current;
    }

    protected void setCurrent(Object current) {
        this.current = current;
    }
}

