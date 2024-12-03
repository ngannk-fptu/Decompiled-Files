/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.MDC
 */
package org.apache.log4j;

import java.util.Stack;
import org.slf4j.MDC;

public class NDC {
    public static final String PREFIX = "NDC";

    public static void clear() {
        int depth = NDC.getDepth();
        for (int i = 0; i < depth; ++i) {
            String key = PREFIX + i;
            MDC.remove((String)key);
        }
    }

    public static Stack cloneStack() {
        return null;
    }

    public static void inherit(Stack stack) {
    }

    public static String get() {
        return null;
    }

    public static int getDepth() {
        String val;
        int i = 0;
        while ((val = MDC.get((String)(PREFIX + i))) != null) {
            ++i;
        }
        return i;
    }

    public static String pop() {
        int next = NDC.getDepth();
        if (next == 0) {
            return "";
        }
        int last = next - 1;
        String key = PREFIX + last;
        String val = MDC.get((String)key);
        MDC.remove((String)key);
        return val;
    }

    public static String peek() {
        int next = NDC.getDepth();
        if (next == 0) {
            return "";
        }
        int last = next - 1;
        String key = PREFIX + last;
        String val = MDC.get((String)key);
        return val;
    }

    public static void push(String message) {
        int next = NDC.getDepth();
        MDC.put((String)(PREFIX + next), (String)message);
    }

    public static void remove() {
        NDC.clear();
    }

    public static void setMaxDepth(int maxDepth) {
    }
}

