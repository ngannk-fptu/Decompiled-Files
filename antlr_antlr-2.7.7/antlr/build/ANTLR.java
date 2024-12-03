/*
 * Decompiled with CFR 0.152.
 */
package antlr.build;

import antlr.build.Tool;
import java.io.File;
import java.io.FilenameFilter;

public class ANTLR {
    public static String compiler = "javac";
    public static String jarName = "antlr.jar";
    public static String root = ".";
    public static String[] srcdir = new String[]{"antlr", "antlr/actions/cpp", "antlr/actions/java", "antlr/actions/csharp", "antlr/collections", "antlr/collections/impl", "antlr/debug", "antlr/ASdebug", "antlr/debug/misc", "antlr/preprocessor"};

    public ANTLR() {
        compiler = System.getProperty("antlr.build.compiler", compiler);
        root = System.getProperty("antlr.build.root", root);
    }

    public String getName() {
        return "ANTLR";
    }

    public void build(Tool tool) {
        if (!this.rootIsValidANTLRDir(tool)) {
            return;
        }
        tool.antlr(root + "/antlr/antlr.g");
        tool.antlr(root + "/antlr/tokdef.g");
        tool.antlr(root + "/antlr/preprocessor/preproc.g");
        tool.antlr(root + "/antlr/actions/java/action.g");
        tool.antlr(root + "/antlr/actions/cpp/action.g");
        tool.antlr(root + "/antlr/actions/csharp/action.g");
        for (int i = 0; i < srcdir.length; ++i) {
            String string = compiler + " -d " + root + " " + root + "/" + srcdir[i] + "/*.java";
            tool.system(string);
        }
    }

    public void jar(Tool tool) {
        if (!this.rootIsValidANTLRDir(tool)) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer(2000);
        stringBuffer.append("jar cvf " + root + "/" + jarName);
        for (int i = 0; i < srcdir.length; ++i) {
            stringBuffer.append(" " + root + "/" + srcdir[i] + "/*.class");
        }
        tool.system(stringBuffer.toString());
    }

    protected boolean rootIsValidANTLRDir(Tool tool) {
        if (root == null) {
            return false;
        }
        File file = new File(root);
        if (!file.exists()) {
            tool.error("Property antlr.build.root==" + root + " does not exist");
            return false;
        }
        if (!file.isDirectory()) {
            tool.error("Property antlr.build.root==" + root + " is not a directory");
            return false;
        }
        String[] stringArray = file.list(new FilenameFilter(){

            public boolean accept(File file, String string) {
                return file.isDirectory() && string.equals("antlr");
            }
        });
        if (stringArray == null || stringArray.length == 0) {
            tool.error("Property antlr.build.root==" + root + " does not appear to be a valid ANTLR project root (no antlr subdir)");
            return false;
        }
        File file2 = new File(root + "/antlr");
        String[] stringArray2 = file2.list();
        if (stringArray2 == null || stringArray2.length == 0) {
            tool.error("Property antlr.build.root==" + root + " does not appear to be a valid ANTLR project root (no .java files in antlr subdir");
            return false;
        }
        return true;
    }
}

