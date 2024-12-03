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

public class JMXAccessorInvokeTask
extends JMXAccessorTask {
    private String operation;
    private List<Arg> args = new ArrayList<Arg>();

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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
        if (this.operation == null) {
            throw new BuildException("Must specify a 'operation' for call");
        }
        return this.jmxInvoke(jmxServerConnection, this.getName());
    }

    protected String jmxInvoke(MBeanServerConnection jmxServerConnection, String name) throws Exception {
        Object result;
        if (this.args == null) {
            result = jmxServerConnection.invoke(new ObjectName(name), this.operation, null, null);
        } else {
            Object[] argsA = new Object[this.args.size()];
            String[] sigA = new String[this.args.size()];
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
            result = jmxServerConnection.invoke(new ObjectName(name), this.operation, argsA, sigA);
        }
        if (result != null) {
            this.echoResult(this.operation, result);
            this.createProperty(result);
        }
        return null;
    }
}

