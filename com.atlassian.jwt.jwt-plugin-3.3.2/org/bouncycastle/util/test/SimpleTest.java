/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.test;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.test.SimpleTestResult;
import org.bouncycastle.util.test.Test;
import org.bouncycastle.util.test.TestFailedException;
import org.bouncycastle.util.test.TestResult;

public abstract class SimpleTest
implements Test {
    public abstract String getName();

    private TestResult success() {
        return SimpleTestResult.successful(this, "Okay");
    }

    protected void fail(String string) {
        throw new TestFailedException(SimpleTestResult.failed(this, string));
    }

    protected void isTrue(boolean bl) {
        if (!bl) {
            throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
        }
    }

    protected void isTrue(String string, boolean bl) {
        if (!bl) {
            throw new TestFailedException(SimpleTestResult.failed(this, string));
        }
    }

    protected void isEquals(Object object, Object object2) {
        if (!object.equals(object2)) {
            throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
        }
    }

    protected void isEquals(int n, int n2) {
        if (n != n2) {
            throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
        }
    }

    protected void isEquals(long l, long l2) {
        if (l != l2) {
            throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
        }
    }

    protected void isEquals(String string, boolean bl, boolean bl2) {
        if (bl != bl2) {
            throw new TestFailedException(SimpleTestResult.failed(this, string));
        }
    }

    protected void isEquals(String string, long l, long l2) {
        if (l != l2) {
            throw new TestFailedException(SimpleTestResult.failed(this, string));
        }
    }

    protected void isEquals(String string, Object object, Object object2) {
        if (object == null && object2 == null) {
            return;
        }
        if (object == null) {
            throw new TestFailedException(SimpleTestResult.failed(this, string));
        }
        if (object2 == null) {
            throw new TestFailedException(SimpleTestResult.failed(this, string));
        }
        if (!object.equals(object2)) {
            throw new TestFailedException(SimpleTestResult.failed(this, string));
        }
    }

    protected boolean areEqual(byte[][] byArray, byte[][] byArray2) {
        if (byArray == null && byArray2 == null) {
            return true;
        }
        if (byArray == null || byArray2 == null) {
            return false;
        }
        if (byArray.length != byArray2.length) {
            return false;
        }
        for (int i = 0; i < byArray.length; ++i) {
            if (this.areEqual(byArray[i], byArray2[i])) continue;
            return false;
        }
        return true;
    }

    protected void fail(String string, Throwable throwable) {
        throw new TestFailedException(SimpleTestResult.failed(this, string, throwable));
    }

    protected void fail(String string, Object object, Object object2) {
        throw new TestFailedException(SimpleTestResult.failed(this, string, object, object2));
    }

    protected boolean areEqual(byte[] byArray, byte[] byArray2) {
        return Arrays.areEqual(byArray, byArray2);
    }

    protected boolean areEqual(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) {
        return Arrays.areEqual(byArray, n, n2, byArray2, n3, n4);
    }

    public TestResult perform() {
        try {
            this.performTest();
            return this.success();
        }
        catch (TestFailedException testFailedException) {
            return testFailedException.getResult();
        }
        catch (Exception exception) {
            return SimpleTestResult.failed(this, "Exception: " + exception, exception);
        }
    }

    public abstract void performTest() throws Exception;

    public static void runTest(Test test) {
        SimpleTest.runTest(test, System.out);
    }

    public static void runTest(Test test, PrintStream printStream) {
        TestResult testResult = test.perform();
        if (testResult.getException() != null) {
            testResult.getException().printStackTrace(printStream);
        }
        printStream.println(testResult);
    }

    public static void runTests(Test[] testArray) {
        SimpleTest.runTests(testArray, System.out);
    }

    public static void runTests(Test[] testArray, PrintStream printStream) {
        Vector<TestResult> vector = new Vector<TestResult>();
        for (int i = 0; i != testArray.length; ++i) {
            TestResult testResult = testArray[i].perform();
            if (!testResult.isSuccessful()) {
                vector.addElement(testResult);
            }
            if (testResult.getException() != null) {
                testResult.getException().printStackTrace(printStream);
            }
            printStream.println(testResult);
        }
        printStream.println("-----");
        if (vector.isEmpty()) {
            printStream.println("All tests successful.");
        } else {
            printStream.println("Completed with " + vector.size() + " FAILURES:");
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                System.out.println("=>  " + (TestResult)enumeration.nextElement());
            }
        }
    }
}

