/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.StringUtils;

public class ModulePathInfo {
    public final Set<String> modulePath = new LinkedHashSet<String>();
    public final Set<String> addModules = new LinkedHashSet<String>();
    public final Set<String> patchModules = new LinkedHashSet<String>();
    public final Set<String> addExports = new LinkedHashSet<String>();
    public final Set<String> addOpens = new LinkedHashSet<String>();
    public final Set<String> addReads = new LinkedHashSet<String>();
    private final List<Set<String>> fields = Arrays.asList(this.modulePath, this.addModules, this.patchModules, this.addExports, this.addOpens, this.addReads);
    private static final List<String> argSwitches = Arrays.asList("--module-path=", "--add-modules=", "--patch-module=", "--add-exports=", "--add-opens=", "--add-reads=");
    private static final List<Character> argPartSeparatorChars = Arrays.asList(Character.valueOf(File.pathSeparatorChar), Character.valueOf(','), Character.valueOf('\u0000'), Character.valueOf('\u0000'), Character.valueOf('\u0000'), Character.valueOf('\u0000'));
    private final AtomicBoolean gotRuntimeInfo = new AtomicBoolean();

    void getRuntimeInfo(ReflectionUtils reflectionUtils) {
        if (!this.gotRuntimeInfo.getAndSet(true)) {
            List commandlineArguments;
            Class<?> managementFactory = reflectionUtils.classForNameOrNull("java.lang.management.ManagementFactory");
            Object runtimeMXBean = managementFactory == null ? null : reflectionUtils.invokeStaticMethod(false, managementFactory, "getRuntimeMXBean");
            List list = commandlineArguments = runtimeMXBean == null ? null : (List)reflectionUtils.invokeMethod(false, runtimeMXBean, "getInputArguments");
            if (commandlineArguments != null) {
                for (String arg : commandlineArguments) {
                    for (int i = 0; i < this.fields.size(); ++i) {
                        String argSwitch = argSwitches.get(i);
                        if (!arg.startsWith(argSwitch)) continue;
                        String argParam = arg.substring(argSwitch.length());
                        Set<String> argField = this.fields.get(i);
                        char sepChar = argPartSeparatorChars.get(i).charValue();
                        if (sepChar == '\u0000') {
                            argField.add(argParam);
                            continue;
                        }
                        argField.addAll(Arrays.asList(JarUtils.smartPathSplit(argParam, sepChar, null)));
                    }
                }
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(1024);
        if (!this.modulePath.isEmpty()) {
            buf.append("--module-path=");
            buf.append(StringUtils.join(File.pathSeparator, this.modulePath));
        }
        if (!this.addModules.isEmpty()) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("--add-modules=");
            buf.append(StringUtils.join(",", this.addModules));
        }
        for (String patchModulesEntry : this.patchModules) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("--patch-module=");
            buf.append(patchModulesEntry);
        }
        for (String addExportsEntry : this.addExports) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("--add-exports=");
            buf.append(addExportsEntry);
        }
        for (String addOpensEntry : this.addOpens) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("--add-opens=");
            buf.append(addOpensEntry);
        }
        for (String addReadsEntry : this.addReads) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("--add-reads=");
            buf.append(addReadsEntry);
        }
        return buf.toString();
    }
}

