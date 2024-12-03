/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TypeAdapter;
import org.apache.tools.ant.dispatch.DispatchUtils;
import org.apache.tools.ant.dispatch.Dispatchable;

public class TaskAdapter
extends Task
implements TypeAdapter {
    private Object proxy;

    public TaskAdapter() {
    }

    public TaskAdapter(Object proxy) {
        this();
        this.setProxy(proxy);
    }

    public static void checkTaskClass(Class<?> taskClass, Project project) {
        if (!Dispatchable.class.isAssignableFrom(taskClass)) {
            try {
                Method executeM = taskClass.getMethod("execute", new Class[0]);
                if (!Void.TYPE.equals(executeM.getReturnType())) {
                    String message = "return type of execute() should be void but was \"" + executeM.getReturnType() + "\" in " + taskClass;
                    project.log(message, 1);
                }
            }
            catch (NoSuchMethodException e) {
                String message = "No public execute() in " + taskClass;
                project.log(message, 0);
                throw new BuildException(message);
            }
            catch (LinkageError e) {
                String message = "Could not load " + taskClass + ": " + e;
                project.log(message, 0);
                throw new BuildException(message, e);
            }
        }
    }

    @Override
    public void checkProxyClass(Class<?> proxyClass) {
        TaskAdapter.checkTaskClass(proxyClass, this.getProject());
    }

    @Override
    public void execute() throws BuildException {
        try {
            Method setLocationM = this.proxy.getClass().getMethod("setLocation", Location.class);
            if (setLocationM != null) {
                setLocationM.invoke(this.proxy, this.getLocation());
            }
        }
        catch (NoSuchMethodException setLocationM) {
        }
        catch (Exception ex) {
            this.log("Error setting location in " + this.proxy.getClass(), 0);
            throw new BuildException(ex);
        }
        try {
            Method setProjectM = this.proxy.getClass().getMethod("setProject", Project.class);
            if (setProjectM != null) {
                setProjectM.invoke(this.proxy, this.getProject());
            }
        }
        catch (NoSuchMethodException setProjectM) {
        }
        catch (Exception ex) {
            this.log("Error setting project in " + this.proxy.getClass(), 0);
            throw new BuildException(ex);
        }
        try {
            DispatchUtils.execute(this.proxy);
        }
        catch (BuildException be) {
            throw be;
        }
        catch (Exception ex) {
            this.log("Error in " + this.proxy.getClass(), 3);
            throw new BuildException(ex);
        }
    }

    @Override
    public void setProxy(Object o) {
        this.proxy = o;
    }

    @Override
    public Object getProxy() {
        return this.proxy;
    }
}

