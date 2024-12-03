/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.dispatch.Dispatchable;

public class DispatchUtils {
    public static final void execute(Object task) throws BuildException {
        block19: {
            String methodName = "execute";
            Dispatchable dispatchable = null;
            try {
                UnknownElement ue;
                Object realThing;
                if (task instanceof Dispatchable) {
                    dispatchable = (Dispatchable)task;
                } else if (task instanceof UnknownElement && (realThing = (ue = (UnknownElement)task).getRealThing()) instanceof Dispatchable && realThing instanceof Task) {
                    dispatchable = (Dispatchable)realThing;
                }
                if (dispatchable != null) {
                    String mName = null;
                    try {
                        Class<?> c;
                        Method actionM;
                        String name = dispatchable.getActionParameterName();
                        if (name == null || name.trim().isEmpty()) {
                            throw new BuildException("Action Parameter Name must not be empty for Dispatchable Task.");
                        }
                        mName = "get" + name.trim().substring(0, 1).toUpperCase();
                        if (name.length() > 1) {
                            mName = mName + name.substring(1);
                        }
                        if ((actionM = (c = dispatchable.getClass()).getMethod(mName, new Class[0])) != null) {
                            Object o = actionM.invoke((Object)dispatchable, (Object[])null);
                            if (o == null) {
                                throw new BuildException("Dispatchable Task attribute '" + name.trim() + "' not set or value is empty.");
                            }
                            methodName = o.toString().trim();
                            if (methodName.isEmpty()) {
                                throw new BuildException("Dispatchable Task attribute '" + name.trim() + "' not set or value is empty.");
                            }
                            Method executeM = dispatchable.getClass().getMethod(methodName, new Class[0]);
                            if (executeM == null) {
                                throw new BuildException("No public " + methodName + "() in " + dispatchable.getClass());
                            }
                            executeM.invoke((Object)dispatchable, (Object[])null);
                            if (task instanceof UnknownElement) {
                                ((UnknownElement)task).setRealThing(null);
                            }
                        }
                        break block19;
                    }
                    catch (NoSuchMethodException nsme) {
                        throw new BuildException("No public " + mName + "() in " + task.getClass());
                    }
                }
                Method executeM = null;
                executeM = task.getClass().getMethod(methodName, new Class[0]);
                if (executeM == null) {
                    throw new BuildException("No public " + methodName + "() in " + task.getClass());
                }
                executeM.invoke(task, new Object[0]);
                if (task instanceof UnknownElement) {
                    ((UnknownElement)task).setRealThing(null);
                }
            }
            catch (InvocationTargetException ie) {
                Throwable t = ie.getTargetException();
                if (t instanceof BuildException) {
                    throw (BuildException)t;
                }
                throw new BuildException(t);
            }
            catch (IllegalAccessException | NoSuchMethodException e) {
                throw new BuildException(e);
            }
        }
    }
}

