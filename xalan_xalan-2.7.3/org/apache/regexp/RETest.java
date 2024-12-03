/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.apache.regexp.RE;
import org.apache.regexp.REDebugCompiler;
import org.apache.regexp.REProgram;

public class RETest {
    RE r = new RE();
    REDebugCompiler compiler = new REDebugCompiler();
    static final boolean showSuccesses = false;
    char[] re1Instructions;
    REProgram re1;
    String expr;
    int n;
    int failures;

    public RETest() {
        char[] cArray = new char[29];
        cArray[0] = 124;
        cArray[2] = 26;
        cArray[3] = 124;
        cArray[5] = 13;
        cArray[6] = 65;
        cArray[7] = '\u0001';
        cArray[8] = 4;
        cArray[9] = 97;
        cArray[10] = 124;
        cArray[12] = 3;
        cArray[13] = 71;
        cArray[15] = 65526;
        cArray[16] = 124;
        cArray[18] = 3;
        cArray[19] = 78;
        cArray[21] = 3;
        cArray[22] = 65;
        cArray[23] = '\u0001';
        cArray[24] = 4;
        cArray[25] = 98;
        cArray[26] = 69;
        this.re1Instructions = cArray;
        this.re1 = new REProgram(this.re1Instructions);
        this.n = 0;
        this.failures = 0;
    }

    public RETest(String[] stringArray) {
        char[] cArray = new char[29];
        cArray[0] = 124;
        cArray[2] = 26;
        cArray[3] = 124;
        cArray[5] = 13;
        cArray[6] = 65;
        cArray[7] = '\u0001';
        cArray[8] = 4;
        cArray[9] = 97;
        cArray[10] = 124;
        cArray[12] = 3;
        cArray[13] = 71;
        cArray[15] = 65526;
        cArray[16] = 124;
        cArray[18] = 3;
        cArray[19] = 78;
        cArray[21] = 3;
        cArray[22] = 65;
        cArray[23] = '\u0001';
        cArray[24] = 4;
        cArray[25] = 98;
        cArray[26] = 69;
        this.re1Instructions = cArray;
        this.re1 = new REProgram(this.re1Instructions);
        this.n = 0;
        this.failures = 0;
        try {
            if (stringArray.length == 2) {
                this.runInteractiveTests(stringArray[1]);
            } else if (stringArray.length == 1) {
                this.runAutomatedTests(stringArray[0]);
            } else {
                System.out.println("Usage: RETest ([-i] [regex]) ([/path/to/testfile.txt])");
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    void die(String string) {
        this.say("FATAL ERROR: " + string);
        System.exit(0);
    }

    void fail(String string) {
        ++this.failures;
        this.say("\n");
        this.say("*******************************************************");
        this.say("*********************  FAILURE!  **********************");
        this.say("*******************************************************");
        this.say("\n");
        this.say(string);
        this.say("");
        this.compiler.dumpProgram(new PrintWriter(System.out));
        this.say("\n");
    }

    public static void main(String[] stringArray) {
        try {
            RETest.test();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void runAutomatedTests(String string) throws Exception {
        File file;
        long l = System.currentTimeMillis();
        RE rE = new RE(this.re1);
        this.say("a*b");
        this.say("aaaab = " + rE.match("aaab"));
        this.showParens(rE);
        this.say("b = " + rE.match("b"));
        this.showParens(rE);
        this.say("c = " + rE.match("c"));
        this.showParens(rE);
        this.say("ccccaaaaab = " + rE.match("ccccaaaaab"));
        this.showParens(rE);
        rE = new RE("a*b");
        Object[] objectArray = rE.split("xxxxaabxxxxbyyyyaaabzzz");
        rE = new RE("x+");
        objectArray = rE.grep(objectArray);
        int n = 0;
        while (true) {
            if (n >= objectArray.length) {
                rE = new RE("a*b");
                String string2 = rE.subst("aaaabfooaaabgarplyaaabwackyb", "-");
                System.out.println("s = " + string2);
                file = new File(string);
                if (file.exists()) break;
                throw new Exception("Could not find: " + string);
            }
            System.out.println("s[" + n + "] = " + (String)objectArray[n]);
            ++n;
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        try {
            while (bufferedReader.ready()) {
                String string3;
                String string4 = "";
                while (bufferedReader.ready() && (string4 = bufferedReader.readLine()) != null && !(string4 = string4.trim()).startsWith("#")) {
                    if (string4.equals("")) continue;
                    System.out.println("Script error.  Line = " + string4);
                    System.exit(0);
                }
                if (!bufferedReader.ready()) break;
                this.expr = bufferedReader.readLine();
                ++this.n;
                this.say("");
                this.say(String.valueOf(this.n) + ". " + this.expr);
                this.say("");
                try {
                    rE.setProgram(this.compiler.compile(this.expr));
                }
                catch (Exception exception) {
                    string3 = bufferedReader.readLine().trim();
                    if (string3.equals("ERR")) {
                        this.say("   Match: ERR");
                        this.success("Produces an error (" + exception.toString() + "), as expected.");
                        continue;
                    }
                    this.fail("Produces the unexpected error \"" + exception.getMessage() + "\"");
                }
                catch (Error error) {
                    this.fail("Compiler threw fatal error \"" + error.getMessage() + "\"");
                    error.printStackTrace();
                }
                String string5 = bufferedReader.readLine().trim();
                this.say("   Match against: '" + string5 + "'");
                if (string5.equals("ERR")) {
                    this.fail("Was expected to be an error, but wasn't.");
                    continue;
                }
                try {
                    boolean bl = rE.match(string5);
                    string3 = bufferedReader.readLine().trim();
                    if (bl) {
                        this.say("   Match: YES");
                        if (string3.equals("NO")) {
                            this.fail("Matched \"" + string5 + "\", when not expected to.");
                            continue;
                        }
                        if (string3.equals("YES")) {
                            this.success("Matched \"" + string5 + "\", as expected:");
                            this.say("   Paren count: " + rE.getParenCount());
                            int n2 = 0;
                            while (n2 < rE.getParenCount()) {
                                String string6 = bufferedReader.readLine().trim();
                                this.say("   Paren " + n2 + " : " + rE.getParen(n2));
                                if (!string6.equals(rE.getParen(n2))) {
                                    this.fail("Register " + n2 + " should be = \"" + string6 + "\", but is \"" + rE.getParen(n2) + "\" instead.");
                                }
                                ++n2;
                            }
                            continue;
                        }
                        this.die("Test script error!");
                        continue;
                    }
                    this.say("   Match: NO");
                    if (string3.equals("YES")) {
                        this.fail("Did not match \"" + string5 + "\", when expected to.");
                        continue;
                    }
                    if (string3.equals("NO")) {
                        this.success("Did not match \"" + string5 + "\", as expected.");
                        continue;
                    }
                    this.die("Test script error!");
                }
                catch (Exception exception) {
                    this.fail("Matcher threw exception: " + exception.toString());
                    exception.printStackTrace();
                }
                catch (Error error) {
                    this.fail("Matcher threw fatal error \"" + error.getMessage() + "\"");
                    error.printStackTrace();
                }
            }
        }
        catch (Throwable throwable) {
            Object var11_20 = null;
            bufferedReader.close();
            throw throwable;
        }
        Object var11_19 = null;
        bufferedReader.close();
        System.out.println("\n\nMatch time = " + (System.currentTimeMillis() - l) + " ms.");
        System.out.println("\nTests complete.  " + this.n + " tests, " + this.failures + " failure(s).");
    }

    void runInteractiveTests(String string) {
        try {
            this.r.setProgram(this.compiler.compile(string));
            this.say("\n" + string + "\n");
            this.compiler.dumpProgram(new PrintWriter(System.out));
            while (true) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("> ");
                System.out.flush();
                String string2 = bufferedReader.readLine();
                if (this.r.match(string2)) {
                    this.say("Match successful.");
                } else {
                    this.say("Match failed.");
                }
                this.showParens(this.r);
            }
        }
        catch (Exception exception) {
            this.say("Error: " + exception.toString());
            exception.printStackTrace();
            return;
        }
    }

    void say(String string) {
        System.out.println(string);
    }

    void show() {
        this.say("\n-----------------------\n");
        this.say("Expression #" + this.n + " \"" + this.expr + "\" ");
    }

    void showParens(RE rE) {
        int n = 0;
        while (n < rE.getParenCount()) {
            this.say("$" + n + " = " + rE.getParen(n));
            ++n;
        }
    }

    void success(String string) {
    }

    public static boolean test() throws Exception {
        RETest rETest = new RETest();
        rETest.runAutomatedTests("docs/RETest.txt");
        return rETest.failures == 0;
    }
}

