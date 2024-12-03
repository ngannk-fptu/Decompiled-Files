/*
 * Decompiled with CFR 0.152.
 */
package antlr.build;

import antlr.Utils;
import antlr.build.StreamScarfer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class Tool {
    public String os = System.getProperty("os.name");
    static /* synthetic */ Class class$antlr$build$Tool;

    public static void main(String[] stringArray) {
        if (stringArray.length != 1) {
            System.err.println("usage: java antlr.build.Tool action");
            return;
        }
        String string = "antlr.build.ANTLR";
        String string2 = stringArray[0];
        Tool tool = new Tool();
        tool.perform(string, string2);
    }

    public void perform(String string, String string2) {
        if (string == null || string2 == null) {
            this.error("missing app or action");
            return;
        }
        Class clazz = null;
        Method method = null;
        Object object = null;
        try {
            object = Utils.createInstanceOf(string);
        }
        catch (Exception exception) {
            try {
                if (!string.startsWith("antlr.build.")) {
                    clazz = Utils.loadClass("antlr.build." + string);
                }
                this.error("no such application " + string, exception);
            }
            catch (Exception exception2) {
                this.error("no such application " + string, exception2);
            }
        }
        if (clazz == null || object == null) {
            return;
        }
        try {
            method = clazz.getMethod(string2, class$antlr$build$Tool == null ? (class$antlr$build$Tool = Tool.class$("antlr.build.Tool")) : class$antlr$build$Tool);
            method.invoke(object, this);
        }
        catch (Exception exception) {
            this.error("no such action for application " + string, exception);
        }
    }

    public void system(String string) {
        Runtime runtime = Runtime.getRuntime();
        try {
            this.log(string);
            Process process = null;
            process = !this.os.startsWith("Windows") ? runtime.exec(new String[]{"sh", "-c", string}) : runtime.exec(string);
            StreamScarfer streamScarfer = new StreamScarfer(process.getErrorStream(), "stderr", this);
            StreamScarfer streamScarfer2 = new StreamScarfer(process.getInputStream(), "stdout", this);
            streamScarfer.start();
            streamScarfer2.start();
            int n = process.waitFor();
        }
        catch (Exception exception) {
            this.error("cannot exec " + string, exception);
        }
    }

    public void antlr(String string) {
        String string2 = null;
        try {
            string2 = new File(string).getParent();
            if (string2 != null) {
                string2 = new File(string2).getCanonicalPath();
            }
        }
        catch (IOException iOException) {
            this.error("Invalid grammar file: " + string);
        }
        if (string2 != null) {
            this.log("java antlr.Tool -o " + string2 + " " + string);
            antlr.Tool tool = new antlr.Tool();
            tool.doEverything(new String[]{"-o", string2, string});
        }
    }

    public void stdout(String string) {
        System.out.println(string);
    }

    public void stderr(String string) {
        System.err.println(string);
    }

    public void error(String string) {
        System.err.println("antlr.build.Tool: " + string);
    }

    public void log(String string) {
        System.out.println("executing: " + string);
    }

    public void error(String string, Exception exception) {
        System.err.println("antlr.build.Tool: " + string);
        exception.printStackTrace(System.err);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

