/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Assertions;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.util.JavaEnvUtils;

public class CommandlineJava
implements Cloneable {
    private Commandline vmCommand = new Commandline();
    private Commandline javaCommand = new Commandline();
    private SysProperties sysProperties = new SysProperties();
    private Path classpath = null;
    private Path bootclasspath = null;
    private Path modulepath = null;
    private Path upgrademodulepath = null;
    private String vmVersion;
    private String maxMemory = null;
    private Assertions assertions = null;
    private ExecutableType executableType;
    private boolean cloneVm = false;

    public CommandlineJava() {
        this.setVm(JavaEnvUtils.getJreExecutable("java"));
        this.setVmversion(JavaEnvUtils.getJavaVersion());
    }

    public Commandline.Argument createArgument() {
        return this.javaCommand.createArgument();
    }

    public Commandline.Argument createVmArgument() {
        return this.vmCommand.createArgument();
    }

    public void addSysproperty(Environment.Variable sysp) {
        this.sysProperties.addVariable(sysp);
    }

    public void addSyspropertyset(PropertySet sysp) {
        this.sysProperties.addSyspropertyset(sysp);
    }

    public void addSysproperties(SysProperties sysp) {
        this.sysProperties.addSysproperties(sysp);
    }

    public void setVm(String vm) {
        this.vmCommand.setExecutable(vm);
    }

    public void setVmversion(String value) {
        this.vmVersion = value;
    }

    public void setCloneVm(boolean cloneVm) {
        this.cloneVm = cloneVm;
    }

    public Assertions getAssertions() {
        return this.assertions;
    }

    public void setAssertions(Assertions assertions) {
        this.assertions = assertions;
    }

    public void setJar(String jarpathname) {
        this.javaCommand.setExecutable(jarpathname);
        this.executableType = ExecutableType.JAR;
    }

    public String getJar() {
        if (this.executableType == ExecutableType.JAR) {
            return this.javaCommand.getExecutable();
        }
        return null;
    }

    public void setClassname(String classname) {
        if (this.executableType == ExecutableType.MODULE) {
            this.javaCommand.setExecutable(CommandlineJava.createModuleClassPair(CommandlineJava.parseModuleFromModuleClassPair(this.javaCommand.getExecutable()), classname), false);
        } else {
            this.javaCommand.setExecutable(classname);
            this.executableType = ExecutableType.CLASS;
        }
    }

    public String getClassname() {
        if (this.executableType != null) {
            switch (this.executableType) {
                case CLASS: {
                    return this.javaCommand.getExecutable();
                }
                case MODULE: {
                    return CommandlineJava.parseClassFromModuleClassPair(this.javaCommand.getExecutable());
                }
            }
        }
        return null;
    }

    public void setSourceFile(String sourceFile) {
        this.executableType = ExecutableType.SOURCE_FILE;
        this.javaCommand.setExecutable(sourceFile);
    }

    public String getSourceFile() {
        return this.executableType == ExecutableType.SOURCE_FILE ? this.javaCommand.getExecutable() : null;
    }

    public void setModule(String module) {
        if (this.executableType == null) {
            this.javaCommand.setExecutable(module);
        } else {
            switch (this.executableType) {
                case JAR: {
                    this.javaCommand.setExecutable(module, false);
                    break;
                }
                case CLASS: {
                    this.javaCommand.setExecutable(CommandlineJava.createModuleClassPair(module, this.javaCommand.getExecutable()), false);
                    break;
                }
                case MODULE: {
                    this.javaCommand.setExecutable(CommandlineJava.createModuleClassPair(module, CommandlineJava.parseClassFromModuleClassPair(this.javaCommand.getExecutable())), false);
                    break;
                }
            }
        }
        this.executableType = ExecutableType.MODULE;
    }

    public String getModule() {
        if (this.executableType == ExecutableType.MODULE) {
            return CommandlineJava.parseModuleFromModuleClassPair(this.javaCommand.getExecutable());
        }
        return null;
    }

    public Path createClasspath(Project p) {
        if (this.classpath == null) {
            this.classpath = new Path(p);
        }
        return this.classpath;
    }

    public Path createBootclasspath(Project p) {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(p);
        }
        return this.bootclasspath;
    }

    public Path createModulepath(Project p) {
        if (this.modulepath == null) {
            this.modulepath = new Path(p);
        }
        return this.modulepath;
    }

    public Path createUpgrademodulepath(Project p) {
        if (this.upgrademodulepath == null) {
            this.upgrademodulepath = new Path(p);
        }
        return this.upgrademodulepath;
    }

    public String getVmversion() {
        return this.vmVersion;
    }

    public String[] getCommandline() {
        LinkedList commands = new LinkedList();
        this.addCommandsToList(commands.listIterator());
        return commands.toArray(new String[0]);
    }

    private void addCommandsToList(ListIterator<String> listIterator) {
        Path bcp;
        this.getActualVMCommand().addCommandToList(listIterator);
        this.sysProperties.addDefinitionsToList(listIterator);
        if (this.isCloneVm()) {
            SysProperties clonedSysProperties = new SysProperties();
            PropertySet ps = new PropertySet();
            PropertySet.BuiltinPropertySetName sys = new PropertySet.BuiltinPropertySetName();
            sys.setValue("system");
            ps.appendBuiltin(sys);
            clonedSysProperties.addSyspropertyset(ps);
            clonedSysProperties.addDefinitionsToList(listIterator);
        }
        if ((bcp = this.calculateBootclasspath(true)).size() > 0) {
            listIterator.add("-Xbootclasspath:" + bcp.toString());
        }
        if (this.haveClasspath()) {
            listIterator.add("-classpath");
            listIterator.add(this.classpath.concatSystemClasspath("ignore").toString());
        }
        if (this.haveModulepath()) {
            listIterator.add("--module-path");
            listIterator.add(this.modulepath.concatSystemClasspath("ignore").toString());
        }
        if (this.haveUpgrademodulepath()) {
            listIterator.add("--upgrade-module-path");
            listIterator.add(this.upgrademodulepath.concatSystemClasspath("ignore").toString());
        }
        if (this.getAssertions() != null) {
            this.getAssertions().applyAssertions(listIterator);
        }
        if (this.executableType == ExecutableType.JAR) {
            listIterator.add("-jar");
        } else if (this.executableType == ExecutableType.MODULE) {
            listIterator.add("-m");
        }
        this.javaCommand.addCommandToList(listIterator);
    }

    public void setMaxmemory(String max) {
        this.maxMemory = max;
    }

    public String toString() {
        return Commandline.toString(this.getCommandline());
    }

    public String describeCommand() {
        return Commandline.describeCommand(this.getCommandline());
    }

    public String describeJavaCommand() {
        return Commandline.describeCommand(this.getJavaCommand());
    }

    protected Commandline getActualVMCommand() {
        Commandline actualVMCommand = (Commandline)this.vmCommand.clone();
        if (this.maxMemory != null) {
            if (this.vmVersion.startsWith("1.1")) {
                actualVMCommand.createArgument().setValue("-mx" + this.maxMemory);
            } else {
                actualVMCommand.createArgument().setValue("-Xmx" + this.maxMemory);
            }
        }
        return actualVMCommand;
    }

    @Deprecated
    public int size() {
        int size = this.getActualVMCommand().size() + this.javaCommand.size() + this.sysProperties.size();
        if (this.isCloneVm()) {
            size += System.getProperties().size();
        }
        if (this.haveClasspath()) {
            size += 2;
        }
        if (this.calculateBootclasspath(true).size() > 0) {
            ++size;
        }
        if (this.executableType == ExecutableType.JAR || this.executableType == ExecutableType.MODULE) {
            ++size;
        }
        if (this.getAssertions() != null) {
            size += this.getAssertions().size();
        }
        return size;
    }

    public Commandline getJavaCommand() {
        return this.javaCommand;
    }

    public Commandline getVmCommand() {
        return this.getActualVMCommand();
    }

    public Path getClasspath() {
        return this.classpath;
    }

    public Path getBootclasspath() {
        return this.bootclasspath;
    }

    public Path getModulepath() {
        return this.modulepath;
    }

    public Path getUpgrademodulepath() {
        return this.upgrademodulepath;
    }

    public void setSystemProperties() throws BuildException {
        this.sysProperties.setSystem();
    }

    public void restoreSystemProperties() throws BuildException {
        this.sysProperties.restoreSystem();
    }

    public SysProperties getSystemProperties() {
        return this.sysProperties;
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            CommandlineJava c = (CommandlineJava)super.clone();
            c.vmCommand = (Commandline)this.vmCommand.clone();
            c.javaCommand = (Commandline)this.javaCommand.clone();
            c.sysProperties = (SysProperties)this.sysProperties.clone();
            if (this.classpath != null) {
                c.classpath = (Path)this.classpath.clone();
            }
            if (this.bootclasspath != null) {
                c.bootclasspath = (Path)this.bootclasspath.clone();
            }
            if (this.modulepath != null) {
                c.modulepath = (Path)this.modulepath.clone();
            }
            if (this.upgrademodulepath != null) {
                c.upgrademodulepath = (Path)this.upgrademodulepath.clone();
            }
            if (this.assertions != null) {
                c.assertions = (Assertions)this.assertions.clone();
            }
            return c;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    public void clearJavaArgs() {
        this.javaCommand.clearArgs();
    }

    public boolean haveClasspath() {
        Path fullClasspath = this.classpath == null ? null : this.classpath.concatSystemClasspath("ignore");
        return fullClasspath != null && !fullClasspath.toString().trim().isEmpty();
    }

    protected boolean haveBootclasspath(boolean log) {
        return this.calculateBootclasspath(log).size() > 0;
    }

    public boolean haveModulepath() {
        Path fullClasspath = this.modulepath != null ? this.modulepath.concatSystemClasspath("ignore") : null;
        return fullClasspath != null && !fullClasspath.toString().trim().isEmpty();
    }

    public boolean haveUpgrademodulepath() {
        Path fullClasspath = this.upgrademodulepath != null ? this.upgrademodulepath.concatSystemClasspath("ignore") : null;
        return fullClasspath != null && !fullClasspath.toString().trim().isEmpty();
    }

    private Path calculateBootclasspath(boolean log) {
        if (this.vmVersion.startsWith("1.1")) {
            if (this.bootclasspath != null && log) {
                this.bootclasspath.log("Ignoring bootclasspath as the target VM doesn't support it.");
            }
        } else {
            Path b = this.bootclasspath;
            if (b == null) {
                b = new Path(null);
            }
            return b.concatSystemBootClasspath(this.isCloneVm() ? "last" : "ignore");
        }
        return new Path(null);
    }

    private boolean isCloneVm() {
        return this.cloneVm || Boolean.parseBoolean(System.getProperty("ant.build.clonevm"));
    }

    private static String createModuleClassPair(String module, String classname) {
        return classname == null ? module : String.format("%s/%s", module, classname);
    }

    private static String parseModuleFromModuleClassPair(String moduleClassPair) {
        if (moduleClassPair == null) {
            return null;
        }
        String[] moduleAndClass = moduleClassPair.split("/");
        return moduleAndClass[0];
    }

    private static String parseClassFromModuleClassPair(String moduleClassPair) {
        if (moduleClassPair == null) {
            return null;
        }
        String[] moduleAndClass = moduleClassPair.split("/");
        return moduleAndClass.length == 2 ? moduleAndClass[1] : null;
    }

    public static class SysProperties
    extends Environment
    implements Cloneable {
        Properties sys = null;
        private Vector<PropertySet> propertySets = new Vector();

        @Override
        public String[] getVariables() throws BuildException {
            LinkedList definitions = new LinkedList();
            this.addDefinitionsToList(definitions.listIterator());
            if (definitions.isEmpty()) {
                return null;
            }
            return definitions.toArray(new String[0]);
        }

        public void addDefinitionsToList(ListIterator<String> listIt) {
            String[] props = super.getVariables();
            if (props != null) {
                for (String prop : props) {
                    listIt.add("-D" + prop);
                }
            }
            Properties propertySetProperties = this.mergePropertySets();
            for (String key : propertySetProperties.stringPropertyNames()) {
                listIt.add("-D" + key + "=" + propertySetProperties.getProperty(key));
            }
        }

        public int size() {
            Properties p = this.mergePropertySets();
            return this.variables.size() + p.size();
        }

        public void setSystem() throws BuildException {
            try {
                this.sys = System.getProperties();
                Properties p = new Properties();
                for (String name : this.sys.stringPropertyNames()) {
                    String value = this.sys.getProperty(name);
                    if (value == null) continue;
                    p.put(name, value);
                }
                p.putAll((Map<?, ?>)this.mergePropertySets());
                for (Environment.Variable v : this.variables) {
                    v.validate();
                    p.put(v.getKey(), v.getValue());
                }
                System.setProperties(p);
            }
            catch (SecurityException e) {
                throw new BuildException("Cannot modify system properties", e);
            }
        }

        public void restoreSystem() throws BuildException {
            if (this.sys == null) {
                throw new BuildException("Unbalanced nesting of SysProperties");
            }
            try {
                System.setProperties(this.sys);
                this.sys = null;
            }
            catch (SecurityException e) {
                throw new BuildException("Cannot modify system properties", e);
            }
        }

        public Object clone() throws CloneNotSupportedException {
            try {
                SysProperties c = (SysProperties)super.clone();
                c.variables = (Vector)this.variables.clone();
                c.propertySets = (Vector)this.propertySets.clone();
                return c;
            }
            catch (CloneNotSupportedException e) {
                return null;
            }
        }

        public void addSyspropertyset(PropertySet ps) {
            this.propertySets.addElement(ps);
        }

        public void addSysproperties(SysProperties ps) {
            this.variables.addAll(ps.variables);
            this.propertySets.addAll(ps.propertySets);
        }

        private Properties mergePropertySets() {
            Properties p = new Properties();
            for (PropertySet ps : this.propertySets) {
                p.putAll((Map<?, ?>)ps.getProperties());
            }
            return p;
        }
    }

    private static enum ExecutableType {
        CLASS,
        JAR,
        MODULE,
        SOURCE_FILE;

    }
}

