/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;

public class Assertions
extends DataType
implements Cloneable {
    private Boolean enableSystemAssertions;
    private ArrayList<BaseAssertion> assertionList = new ArrayList();

    public void addEnable(EnabledAssertion assertion) {
        this.checkChildrenAllowed();
        this.assertionList.add(assertion);
    }

    public void addDisable(DisabledAssertion assertion) {
        this.checkChildrenAllowed();
        this.assertionList.add(assertion);
    }

    public void setEnableSystemAssertions(Boolean enableSystemAssertions) {
        this.checkAttributesAllowed();
        this.enableSystemAssertions = enableSystemAssertions;
    }

    @Override
    public void setRefid(Reference ref) {
        if (!this.assertionList.isEmpty() || this.enableSystemAssertions != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(ref);
    }

    private Assertions getFinalReference() {
        if (this.getRefid() == null) {
            return this;
        }
        Object o = this.getRefid().getReferencedObject(this.getProject());
        if (!(o instanceof Assertions)) {
            throw new BuildException("reference is of wrong type");
        }
        return (Assertions)o;
    }

    public int size() {
        Assertions clause = this.getFinalReference();
        return clause.getFinalSize();
    }

    private int getFinalSize() {
        return this.assertionList.size() + (this.enableSystemAssertions != null ? 1 : 0);
    }

    public void applyAssertions(List<String> commandList) {
        this.getProject().log("Applying assertions", 4);
        Assertions clause = this.getFinalReference();
        if (Boolean.TRUE.equals(clause.enableSystemAssertions)) {
            this.getProject().log("Enabling system assertions", 4);
            commandList.add("-enablesystemassertions");
        } else if (Boolean.FALSE.equals(clause.enableSystemAssertions)) {
            this.getProject().log("disabling system assertions", 4);
            commandList.add("-disablesystemassertions");
        }
        for (BaseAssertion assertion : clause.assertionList) {
            String arg = assertion.toCommand();
            this.getProject().log("adding assertion " + arg, 4);
            commandList.add(arg);
        }
    }

    public void applyAssertions(CommandlineJava command) {
        Assertions clause = this.getFinalReference();
        if (Boolean.TRUE.equals(clause.enableSystemAssertions)) {
            Assertions.addVmArgument(command, "-enablesystemassertions");
        } else if (Boolean.FALSE.equals(clause.enableSystemAssertions)) {
            Assertions.addVmArgument(command, "-disablesystemassertions");
        }
        for (BaseAssertion assertion : clause.assertionList) {
            String arg = assertion.toCommand();
            Assertions.addVmArgument(command, arg);
        }
    }

    public void applyAssertions(ListIterator<String> commandIterator) {
        this.getProject().log("Applying assertions", 4);
        Assertions clause = this.getFinalReference();
        if (Boolean.TRUE.equals(clause.enableSystemAssertions)) {
            this.getProject().log("Enabling system assertions", 4);
            commandIterator.add("-enablesystemassertions");
        } else if (Boolean.FALSE.equals(clause.enableSystemAssertions)) {
            this.getProject().log("disabling system assertions", 4);
            commandIterator.add("-disablesystemassertions");
        }
        for (BaseAssertion assertion : clause.assertionList) {
            String arg = assertion.toCommand();
            this.getProject().log("adding assertion " + arg, 4);
            commandIterator.add(arg);
        }
    }

    private static void addVmArgument(CommandlineJava command, String arg) {
        Commandline.Argument argument = command.createVmArgument();
        argument.setValue(arg);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Assertions that = (Assertions)super.clone();
        that.assertionList = new ArrayList<BaseAssertion>(this.assertionList);
        return that;
    }

    public static abstract class BaseAssertion {
        private String packageName;
        private String className;

        public void setClass(String className) {
            this.className = className;
        }

        public void setPackage(String packageName) {
            this.packageName = packageName;
        }

        protected String getClassName() {
            return this.className;
        }

        protected String getPackageName() {
            return this.packageName;
        }

        public abstract String getCommandPrefix();

        public String toCommand() {
            if (this.getPackageName() != null && this.getClassName() != null) {
                throw new BuildException("Both package and class have been set");
            }
            StringBuilder command = new StringBuilder(this.getCommandPrefix());
            if (this.getPackageName() != null) {
                command.append(':');
                command.append(this.getPackageName());
                if (!command.toString().endsWith("...")) {
                    command.append("...");
                }
            } else if (this.getClassName() != null) {
                command.append(':');
                command.append(this.getClassName());
            }
            return command.toString();
        }
    }

    public static class DisabledAssertion
    extends BaseAssertion {
        @Override
        public String getCommandPrefix() {
            return "-da";
        }
    }

    public static class EnabledAssertion
    extends BaseAssertion {
        @Override
        public String getCommandPrefix() {
            return "-ea";
        }
    }
}

