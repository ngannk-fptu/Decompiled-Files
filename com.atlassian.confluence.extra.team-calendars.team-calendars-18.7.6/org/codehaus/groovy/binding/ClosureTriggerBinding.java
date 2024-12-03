/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import groovy.lang.Closure;
import groovy.lang.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Map;
import org.codehaus.groovy.binding.BindPath;
import org.codehaus.groovy.binding.BindPathSnooper;
import org.codehaus.groovy.binding.ClosureSourceBinding;
import org.codehaus.groovy.binding.DeadEndException;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyPathFullBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

public class ClosureTriggerBinding
implements TriggerBinding,
SourceBinding {
    Map<String, TriggerBinding> syntheticBindings;
    Closure closure;

    public ClosureTriggerBinding(Map<String, TriggerBinding> syntheticBindings) {
        this.syntheticBindings = syntheticBindings;
    }

    public Closure getClosure() {
        return this.closure;
    }

    public void setClosure(Closure closure) {
        this.closure = closure;
    }

    private BindPath createBindPath(String propertyName, BindPathSnooper snooper) {
        BindPath bp = new BindPath();
        bp.propertyName = propertyName;
        bp.updateLocalSyntheticProperties(this.syntheticBindings);
        ArrayList<BindPath> childPaths = new ArrayList<BindPath>();
        for (Map.Entry<String, BindPathSnooper> entry : snooper.fields.entrySet()) {
            childPaths.add(this.createBindPath(entry.getKey(), entry.getValue()));
        }
        bp.children = childPaths.toArray(new BindPath[childPaths.size()]);
        return bp;
    }

    @Override
    public FullBinding createBinding(SourceBinding source, TargetBinding target) {
        if (source != this) {
            throw new RuntimeException("Source binding must the Trigger Binding as well");
        }
        final BindPathSnooper delegate = new BindPathSnooper();
        try {
            final Class<?> closureClass = this.closure.getClass();
            Closure closureLocalCopy = AccessController.doPrivileged(new PrivilegedAction<Closure>(){

                @Override
                public Closure run() {
                    Constructor<?> constructor = closureClass.getConstructors()[0];
                    int paramCount = constructor.getParameterTypes().length;
                    Object[] args = new Object[paramCount];
                    args[0] = delegate;
                    for (int i = 1; i < paramCount; ++i) {
                        args[i] = new Reference<BindPathSnooper>(new BindPathSnooper());
                    }
                    try {
                        boolean acc = constructor.isAccessible();
                        constructor.setAccessible(true);
                        Closure localCopy = (Closure)constructor.newInstance(args);
                        if (!acc) {
                            constructor.setAccessible(false);
                        }
                        localCopy.setResolveStrategy(3);
                        for (Field f : closureClass.getDeclaredFields()) {
                            acc = f.isAccessible();
                            f.setAccessible(true);
                            if (f.getType() == Reference.class) {
                                delegate.fields.put(f.getName(), (BindPathSnooper)((Reference)f.get(localCopy)).get());
                            }
                            if (acc) continue;
                            f.setAccessible(false);
                        }
                        return localCopy;
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Error snooping closure", e);
                    }
                }
            });
            try {
                closureLocalCopy.call();
            }
            catch (DeadEndException deadEndException) {
                throw deadEndException;
            }
            catch (Exception exception) {
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
            throw new RuntimeException("A closure expression binding could not be created because of " + e.getClass().getName() + ":\n\t" + e.getMessage());
        }
        ArrayList<BindPath> rootPaths = new ArrayList<BindPath>();
        for (Map.Entry entry : delegate.fields.entrySet()) {
            BindPath bp = this.createBindPath((String)entry.getKey(), (BindPathSnooper)entry.getValue());
            bp.currentObject = this.closure;
            rootPaths.add(bp);
        }
        PropertyPathFullBinding fb = new PropertyPathFullBinding();
        fb.setSourceBinding(new ClosureSourceBinding(this.closure));
        fb.setTargetBinding(target);
        fb.bindPaths = rootPaths.toArray(new BindPath[rootPaths.size()]);
        return fb;
    }

    @Override
    public Object getSourceValue() {
        return this.closure.call();
    }
}

