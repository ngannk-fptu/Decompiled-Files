/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import org.apache.regexp.RECompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;

public class recompile {
    public static void main(String[] stringArray) {
        RECompiler rECompiler = new RECompiler();
        if (stringArray.length <= 0 || stringArray.length % 2 != 0) {
            System.out.println("Usage: recompile <patternname> <pattern>");
            System.exit(0);
        }
        int n = 0;
        while (n < stringArray.length) {
            try {
                String string = stringArray[n];
                String string2 = stringArray[n + 1];
                String string3 = string + "PatternInstructions";
                System.out.print("\n    // Pre-compiled regular expression '" + string2 + "'\n" + "    private static char[] " + string3 + " = \n    {");
                REProgram rEProgram = rECompiler.compile(string2);
                int n2 = 7;
                char[] cArray = rEProgram.getInstructions();
                int n3 = 0;
                while (n3 < cArray.length) {
                    if (n3 % n2 == 0) {
                        System.out.print("\n        ");
                    }
                    String string4 = Integer.toHexString(cArray[n3]);
                    while (string4.length() < 4) {
                        string4 = "0" + string4;
                    }
                    System.out.print("0x" + string4 + ", ");
                    ++n3;
                }
                System.out.println("\n    };");
                System.out.println("\n    private static RE " + string + "Pattern = new RE(new REProgram(" + string3 + "));");
            }
            catch (RESyntaxException rESyntaxException) {
                System.out.println("Syntax error in expression \"" + stringArray[n] + "\": " + rESyntaxException.toString());
            }
            catch (Exception exception) {
                System.out.println("Unexpected exception: " + exception.toString());
            }
            catch (Error error) {
                System.out.println("Internal error: " + error.toString());
            }
            n += 2;
        }
    }
}

