/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.message.ThreadDumpMessage$ThreadInfoFactory
 *  org.apache.logging.log4j.message.ThreadInformation
 */
package org.apache.logging.log4j.core.message;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.message.ExtendedThreadInformation;
import org.apache.logging.log4j.message.ThreadDumpMessage;
import org.apache.logging.log4j.message.ThreadInformation;

public class ExtendedThreadInfoFactory
implements ThreadDumpMessage.ThreadInfoFactory {
    public ExtendedThreadInfoFactory() {
        Method[] methods = ThreadInfo.class.getMethods();
        boolean basic = true;
        for (Method method : methods) {
            if (!method.getName().equals("getLockInfo")) continue;
            basic = false;
            break;
        }
        if (basic) {
            throw new IllegalStateException();
        }
    }

    public Map<ThreadInformation, StackTraceElement[]> createThreadInfo() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] array = bean.dumpAllThreads(true, true);
        HashMap<ThreadInformation, StackTraceElement[]> threads = new HashMap<ThreadInformation, StackTraceElement[]>(array.length);
        for (ThreadInfo info : array) {
            threads.put(new ExtendedThreadInformation(info), info.getStackTrace());
        }
        return threads;
    }
}

