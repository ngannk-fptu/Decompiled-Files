/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import org.apache.regexp.RE;
import org.apache.regexp.REDebugCompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RETestCase;

public class RETest {
    static final boolean showSuccesses = false;
    static final String NEW_LINE = System.getProperty("line.separator");
    REDebugCompiler compiler = new REDebugCompiler();
    int testCount = 0;
    int failures = 0;

    public static void main(String[] stringArray) {
        try {
            if (!RETest.test(stringArray)) {
                System.exit(1);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

    public static boolean test(String[] stringArray) throws Exception {
        RETest rETest = new RETest();
        if (stringArray.length == 2) {
            rETest.runInteractiveTests(stringArray[1]);
        } else if (stringArray.length == 1) {
            rETest.runAutomatedTests(stringArray[0]);
        } else {
            System.out.println("Usage: RETest ([-i] [regex]) ([/path/to/testfile.txt])");
            System.out.println("By Default will run automated tests from file 'docs/RETest.txt' ...");
            System.out.println();
            rETest.runAutomatedTests("docs/RETest.txt");
        }
        return rETest.failures == 0;
    }

    void runInteractiveTests(String string) {
        RE rE = new RE();
        try {
            rE.setProgram(this.compiler.compile(string));
            this.say("" + NEW_LINE + "" + string + "" + NEW_LINE + "");
            PrintWriter printWriter = new PrintWriter(System.out);
            this.compiler.dumpProgram(printWriter);
            printWriter.flush();
            boolean bl = true;
            while (bl) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("> ");
                System.out.flush();
                String string2 = bufferedReader.readLine();
                if (string2 != null) {
                    if (rE.match(string2)) {
                        this.say("Match successful.");
                    } else {
                        this.say("Match failed.");
                    }
                    this.showParens(rE);
                    continue;
                }
                bl = false;
                System.out.println();
            }
        }
        catch (Exception exception) {
            this.say("Error: " + exception.toString());
            exception.printStackTrace();
        }
    }

    void die(String string) {
        this.say("FATAL ERROR: " + string);
        System.exit(-1);
    }

    void fail(StringBuffer stringBuffer, String string) {
        System.out.print(stringBuffer.toString());
        this.fail(string);
    }

    void fail(String string) {
        ++this.failures;
        this.say("" + NEW_LINE + "");
        this.say("*******************************************************");
        this.say("*********************  FAILURE!  **********************");
        this.say("*******************************************************");
        this.say("" + NEW_LINE + "");
        this.say(string);
        this.say("");
        if (this.compiler != null) {
            PrintWriter printWriter = new PrintWriter(System.out);
            this.compiler.dumpProgram(printWriter);
            printWriter.flush();
            this.say("" + NEW_LINE + "");
        }
    }

    void say(String string) {
        System.out.println(string);
    }

    void showParens(RE rE) {
        int n = 0;
        while (n < rE.getParenCount()) {
            this.say("$" + n + " = " + rE.getParen(n));
            ++n;
        }
    }

    void runAutomatedTests(String string) throws Exception {
        long l = System.currentTimeMillis();
        this.testPrecompiledRE();
        this.testSplitAndGrep();
        this.testSubst();
        this.testOther();
        File file = new File(string);
        if (!file.exists()) {
            throw new Exception("Could not find: " + string);
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        try {
            while (bufferedReader.ready()) {
                RETestCase rETestCase = this.getNextTestCase(bufferedReader);
                if (rETestCase == null) continue;
                rETestCase.runTest();
            }
            Object var8_6 = null;
        }
        catch (Throwable throwable) {
            Object var8_7 = null;
            bufferedReader.close();
            throw throwable;
        }
        bufferedReader.close();
        this.say(NEW_LINE + NEW_LINE + "Match time = " + (System.currentTimeMillis() - l) + " ms.");
        if (this.failures > 0) {
            this.say("*************** THERE ARE FAILURES! *******************");
        }
        this.say("Tests complete.  " + this.testCount + " tests, " + this.failures + " failure(s).");
    }

    void testOther() throws Exception {
        RE rE = new RE("(a*)b");
        this.say("Serialized/deserialized (a*)b");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(128);
        new ObjectOutputStream(byteArrayOutputStream).writeObject(rE);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        rE = (RE)new ObjectInputStream(byteArrayInputStream).readObject();
        if (!rE.match("aaab")) {
            this.fail("Did not match 'aaab' with deserialized RE.");
        } else {
            this.say("aaaab = true");
            this.showParens(rE);
        }
        byteArrayOutputStream.reset();
        this.say("Deserialized (a*)b");
        new ObjectOutputStream(byteArrayOutputStream).writeObject(rE);
        byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        rE = (RE)new ObjectInputStream(byteArrayInputStream).readObject();
        if (rE.getParenCount() != 0) {
            this.fail("Has parens after deserialization.");
        }
        if (!rE.match("aaab")) {
            this.fail("Did not match 'aaab' with deserialized RE.");
        } else {
            this.say("aaaab = true");
            this.showParens(rE);
        }
        rE = new RE("abc(\\w*)");
        this.say("MATCH_CASEINDEPENDENT abc(\\w*)");
        rE.setMatchFlags(1);
        this.say("abc(d*)");
        if (!rE.match("abcddd")) {
            this.fail("Did not match 'abcddd'.");
        } else {
            this.say("abcddd = true");
            this.showParens(rE);
        }
        if (!rE.match("aBcDDdd")) {
            this.fail("Did not match 'aBcDDdd'.");
        } else {
            this.say("aBcDDdd = true");
            this.showParens(rE);
        }
        if (!rE.match("ABCDDDDD")) {
            this.fail("Did not match 'ABCDDDDD'.");
        } else {
            this.say("ABCDDDDD = true");
            this.showParens(rE);
        }
        rE = new RE("(A*)b\\1");
        rE.setMatchFlags(1);
        if (!rE.match("AaAaaaBAAAAAA")) {
            this.fail("Did not match 'AaAaaaBAAAAAA'.");
        } else {
            this.say("AaAaaaBAAAAAA = true");
            this.showParens(rE);
        }
        rE = new RE("[A-Z]*");
        rE.setMatchFlags(1);
        if (!rE.match("CaBgDe12")) {
            this.fail("Did not match 'CaBgDe12'.");
        } else {
            this.say("CaBgDe12 = true");
            this.showParens(rE);
        }
        rE = new RE("^abc$");
        if (rE.match("\nabc")) {
            this.fail("\"\\nabc\" matches \"^abc$\"");
        }
        if (!(rE = new RE("^abc$", 2)).match("\nabc")) {
            this.fail("\"\\nabc\" doesn't match \"^abc$\"");
        }
        if (!rE.match("\rabc")) {
            this.fail("\"\\rabc\" doesn't match \"^abc$\"");
        }
        if (!rE.match("\r\nabc")) {
            this.fail("\"\\r\\nabc\" doesn't match \"^abc$\"");
        }
        if (!rE.match("\u0085abc")) {
            this.fail("\"\\u0085abc\" doesn't match \"^abc$\"");
        }
        if (!rE.match("\u2028abc")) {
            this.fail("\"\\u2028abc\" doesn't match \"^abc$\"");
        }
        if (!rE.match("\u2029abc")) {
            this.fail("\"\\u2029abc\" doesn't match \"^abc$\"");
        }
        if ((rE = new RE("^a.*b$", 2)).match("a\nb")) {
            this.fail("\"a\\nb\" matches \"^a.*b$\"");
        }
        if (rE.match("a\rb")) {
            this.fail("\"a\\rb\" matches \"^a.*b$\"");
        }
        if (rE.match("a\r\nb")) {
            this.fail("\"a\\r\\nb\" matches \"^a.*b$\"");
        }
        if (rE.match("a\u0085b")) {
            this.fail("\"a\\u0085b\" matches \"^a.*b$\"");
        }
        if (rE.match("a\u2028b")) {
            this.fail("\"a\\u2028b\" matches \"^a.*b$\"");
        }
        if (rE.match("a\u2029b")) {
            this.fail("\"a\\u2029b\" matches \"^a.*b$\"");
        }
    }

    private void testPrecompiledRE() {
        char[] cArray = new char[]{'|', '\u0000', '\u001a', '|', '\u0000', '\r', 'A', '\u0001', '\u0004', 'a', '|', '\u0000', '\u0003', 'G', '\u0000', '\ufff6', '|', '\u0000', '\u0003', 'N', '\u0000', '\u0003', 'A', '\u0001', '\u0004', 'b', 'E', '\u0000', '\u0000'};
        REProgram rEProgram = new REProgram(cArray);
        RE rE = new RE(rEProgram);
        this.say("a*b");
        boolean bl = rE.match("aaab");
        this.say("aaab = " + bl);
        this.showParens(rE);
        if (!bl) {
            this.fail("\"aaab\" doesn't match to precompiled \"a*b\"");
        }
        bl = rE.match("b");
        this.say("b = " + bl);
        this.showParens(rE);
        if (!bl) {
            this.fail("\"b\" doesn't match to precompiled \"a*b\"");
        }
        bl = rE.match("c");
        this.say("c = " + bl);
        this.showParens(rE);
        if (bl) {
            this.fail("\"c\" matches to precompiled \"a*b\"");
        }
        bl = rE.match("ccccaaaaab");
        this.say("ccccaaaaab = " + bl);
        this.showParens(rE);
        if (!bl) {
            this.fail("\"ccccaaaaab\" doesn't match to precompiled \"a*b\"");
        }
    }

    private void testSplitAndGrep() {
        String[] stringArray = new String[]{"xxxx", "xxxx", "yyyy", "zzz"};
        RE rE = new RE("a*b");
        Object[] objectArray = rE.split("xxxxaabxxxxbyyyyaaabzzz");
        int n = 0;
        while (n < stringArray.length && n < objectArray.length) {
            this.assertEquals("Wrong splitted part", stringArray[n], objectArray[n]);
            ++n;
        }
        this.assertEquals("Wrong number of splitted parts", stringArray.length, objectArray.length);
        rE = new RE("x+");
        stringArray = new String[]{"xxxx", "xxxx"};
        objectArray = rE.grep(objectArray);
        int n2 = 0;
        while (n2 < objectArray.length) {
            this.say("s[" + n2 + "] = " + (String)objectArray[n2]);
            this.assertEquals("Grep fails", stringArray[n2], (String)objectArray[n2]);
            ++n2;
        }
        this.assertEquals("Wrong number of string found by grep", stringArray.length, objectArray.length);
    }

    private void testSubst() {
        RE rE = new RE("a*b");
        String string = "-foo-garply-wacky-";
        String string2 = rE.subst("aaaabfooaaabgarplyaaabwackyb", "-");
        this.assertEquals("Wrong result of substitution in \"a*b\"", string, string2);
        rE = new RE("http://[\\.\\w\\-\\?/~_@&=%]+");
        string2 = rE.subst("visit us: http://www.apache.org!", "1234<a href=\"$0\">$0</a>", 2);
        this.assertEquals("Wrong subst() result", "visit us: 1234<a href=\"http://www.apache.org\">http://www.apache.org</a>!", string2);
        rE = new RE("(.*?)=(.*)");
        string2 = rE.subst("variable=value", "$1_test_$212", 2);
        this.assertEquals("Wrong subst() result", "variable_test_value12", string2);
        rE = new RE("^a$");
        string2 = rE.subst("a", "b", 2);
        this.assertEquals("Wrong subst() result", "b", string2);
        rE = new RE("^a$", 2);
        string2 = rE.subst("\r\na\r\n", "b", 2);
        this.assertEquals("Wrong subst() result", "\r\nb\r\n", string2);
        rE = new RE("fo(o)");
        string2 = rE.subst("foo", "$1", 2);
        this.assertEquals("Wrong subst() result", "o", string2);
    }

    public void assertEquals(String string, String string2, String string3) {
        if (string2 != null && !string2.equals(string3) || string3 != null && !string3.equals(string2)) {
            this.fail(string + " (expected \"" + string2 + "\", actual \"" + string3 + "\")");
        }
    }

    public void assertEquals(String string, int n, int n2) {
        if (n != n2) {
            this.fail(string + " (expected \"" + n + "\", actual \"" + n2 + "\")");
        }
    }

    private boolean getExpectedResult(String string) {
        if ("NO".equals(string)) {
            return false;
        }
        if ("YES".equals(string)) {
            return true;
        }
        this.die("Test script error!");
        return false;
    }

    private String findNextTest(BufferedReader bufferedReader) throws IOException {
        String string = "";
        while (bufferedReader.ready()) {
            string = bufferedReader.readLine();
            if (string == null || (string = string.trim()).startsWith("#")) break;
            if (string.equals("")) continue;
            this.say("Script error.  Line = " + string);
            System.exit(-1);
        }
        return string;
    }

    private RETestCase getNextTestCase(BufferedReader bufferedReader) throws IOException {
        String string = this.findNextTest(bufferedReader);
        if (!bufferedReader.ready()) {
            return null;
        }
        String string2 = bufferedReader.readLine();
        String string3 = bufferedReader.readLine();
        boolean bl = "ERR".equals(string3);
        boolean bl2 = false;
        int n = 0;
        String[] stringArray = null;
        if (!bl && (bl2 = this.getExpectedResult(bufferedReader.readLine().trim()))) {
            n = Integer.parseInt(bufferedReader.readLine().trim());
            stringArray = new String[n];
            int n2 = 0;
            while (n2 < n) {
                stringArray[n2] = bufferedReader.readLine();
                ++n2;
            }
        }
        return new RETestCase(this, string, string2, string3, bl, bl2, stringArray);
    }
}

