/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant.jmx;

import java.util.ArrayList;
import java.util.List;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.apache.catalina.ant.jmx.Arg;
import org.apache.catalina.ant.jmx.JMXAccessorTask;
import org.apache.tools.ant.BuildException;

public class JMXAccessorCreateTask
extends JMXAccessorTask {
    private String className;
    private String classLoader;
    private List<Arg> args = new ArrayList<Arg>();

    public String getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(String classLoaderName) {
        this.classLoader = classLoaderName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void addArg(Arg arg) {
        this.args.add(arg);
    }

    public List<Arg> getArgs() {
        return this.args;
    }

    public void setArgs(List<Arg> args) {
        this.args = args;
    }

    @Override
    public String jmxExecute(MBeanServerConnection jmxServerConnection) throws Exception {
        if (this.getName() == null) {
            throw new BuildException("Must specify a 'name'");
        }
        if (this.className == null) {
            throw new BuildException("Must specify a 'className' for get");
        }
        this.jmxCreate(jmxServerConnection, this.getName());
        return null;
    }

    protected void jmxCreate(MBeanServerConnection jmxServerConnection, String name) throws Exception {
        Object[] argsA = null;
        String[] sigA = null;
        if (this.args != null) {
            argsA = new Object[this.args.size()];
            sigA = new String[this.args.size()];
            for (int i = 0; i < this.args.size(); ++i) {
                Arg arg = this.args.get(i);
                if (arg.getType() == null) {
                    arg.setType("java.lang.String");
                    sigA[i] = arg.getType();
                    argsA[i] = arg.getValue();
                    continue;
                }
                sigA[i] = arg.getType();
                argsA[i] = this.convertStringToType(arg.getValue(), arg.getType());
            }
        }
        if (this.classLoader != null && !this.classLoader.isEmpty()) {
            if (this.isEcho()) {
                this.handleOutput("create MBean " + name + " from class " + this.className + " with classLoader " + this.classLoader);
            }
            if (this.args == null) {
                jmxServerConnection.createMBean(this.className, new ObjectName(name), new ObjectName(this.classLoader));
            } else {
                jmxServerConnection.createMBean(this.className, new ObjectName(name), new ObjectName(this.classLoader), argsA, sigA);
            }
        } else {
            if (this.isEcho()) {
                this.handleOutput("create MBean " + name + " from class " + this.className);
            }
            if (this.args == null) {
                jmxServerConnection.createMBean(this.className, new ObjectName(name));
            } else {
                jmxServerConnection.createMBean(this.className, new ObjectName(name), argsA, sigA);
            }
        }
    }
}

