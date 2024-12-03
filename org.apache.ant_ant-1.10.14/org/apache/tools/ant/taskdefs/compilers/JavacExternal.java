/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.compilers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public class JavacExternal
extends DefaultCompilerAdapter {
    private static List<String> ARGS_FOLLOWED_BY_PATH = Arrays.asList("-cp", "-classpath", "--class-path", "-endorseddirs", "-extdirs", "--module-path", "-p", "--module-source-path", "--processor-module-path", "--processor-path", "-processorpath", "-sourcepath", "--source-path", "-bootclasspath", "--boot-class-path", "--upgrade-module-path");

    @Override
    public boolean execute() throws BuildException {
        this.attributes.log("Using external javac compiler", 3);
        Commandline cmd = new Commandline();
        cmd.setExecutable(this.getJavac().getJavacExecutable());
        if (this.assumeJava1_3Plus()) {
            this.setupModernJavacCommandlineSwitches(cmd);
        } else {
            this.setupJavacCommandlineSwitches(cmd, true);
        }
        int openVmsFirstFileName = this.assumeJava1_2Plus() ? cmd.size() : -1;
        this.logAndAddFilesToCompile(cmd);
        if (Os.isFamily("openvms")) {
            return this.execOnVMS(cmd, openVmsFirstFileName);
        }
        String[] commandLine = cmd.getCommandline();
        int firstFileName = this.assumeJava1_2Plus() ? this.moveArgFileEligibleOptionsToEnd(commandLine) : -1;
        return this.executeExternalCompile(commandLine, firstFileName, true) == 0;
    }

    private int moveArgFileEligibleOptionsToEnd(String[] commandLine) {
        int nonArgFileOptionIdx;
        int argsToMove;
        for (nonArgFileOptionIdx = 1; nonArgFileOptionIdx < commandLine.length && (argsToMove = JavacExternal.numberOfArgsNotEligibleForFile(commandLine, nonArgFileOptionIdx)) > 0; nonArgFileOptionIdx += argsToMove) {
        }
        for (int i = nonArgFileOptionIdx + 1; i < commandLine.length; ++i) {
            int argsToMove2 = JavacExternal.numberOfArgsNotEligibleForFile(commandLine, i);
            if (argsToMove2 <= 0) continue;
            String[] options = Arrays.copyOfRange(commandLine, i, i + argsToMove2);
            if (i - nonArgFileOptionIdx >= 0) {
                System.arraycopy(commandLine, nonArgFileOptionIdx, commandLine, nonArgFileOptionIdx + argsToMove2, i - nonArgFileOptionIdx);
            }
            System.arraycopy(options, 0, commandLine, nonArgFileOptionIdx, argsToMove2);
            nonArgFileOptionIdx += argsToMove2;
            i += argsToMove2 - 1;
        }
        return nonArgFileOptionIdx;
    }

    private static int numberOfArgsNotEligibleForFile(String[] args, int currentIndex) {
        String currentOption = args[currentIndex];
        if (!JavacExternal.isArgFileEligible(currentOption)) {
            return 1;
        }
        if (currentIndex + 1 < args.length && JavacExternal.isArgFollowedByPath(currentOption) && JavacExternal.containsWildcards(args[currentIndex + 1])) {
            return 2;
        }
        return 0;
    }

    private static boolean containsWildcards(String path) {
        return path.contains("*") || path.contains("?");
    }

    private static boolean isArgFileEligible(String option) {
        return !option.startsWith("-J") && !option.startsWith("@") && (!option.startsWith("-Xbootclasspath/") || !JavacExternal.containsWildcards(option));
    }

    private static boolean isArgFollowedByPath(String option) {
        return ARGS_FOLLOWED_BY_PATH.contains(option);
    }

    private boolean execOnVMS(Commandline cmd, int firstFileName) {
        File vmsFile = null;
        try {
            vmsFile = JavaEnvUtils.createVmsJavaOptionFile(cmd.getArguments());
            String[] commandLine = new String[]{cmd.getExecutable(), "-V", vmsFile.getPath()};
            boolean bl = 0 == this.executeExternalCompile(commandLine, firstFileName, true);
            return bl;
        }
        catch (IOException e) {
            throw new BuildException("Failed to create a temporary file for \"-V\" switch");
        }
        finally {
            FileUtils.delete(vmsFile);
        }
    }
}

