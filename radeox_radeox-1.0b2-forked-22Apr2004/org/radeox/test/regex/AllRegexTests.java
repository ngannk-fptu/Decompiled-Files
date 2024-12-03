/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test.regex;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.radeox.test.regex.OroMatcherTest;

public class AllRegexTests
extends TestCase {
    public AllRegexTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite s = new TestSuite();
        s.addTest(OroMatcherTest.suite());
        return s;
    }
}

