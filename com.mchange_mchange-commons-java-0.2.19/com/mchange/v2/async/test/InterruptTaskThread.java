/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.async.test;

import com.mchange.v2.async.RoundRobinAsynchronousRunner;
import com.mchange.v2.lang.ThreadUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InterruptTaskThread {
    static Set interruptedThreads = Collections.synchronizedSet(new HashSet());

    public static void main(String[] stringArray) {
        try {
            RoundRobinAsynchronousRunner roundRobinAsynchronousRunner = new RoundRobinAsynchronousRunner(5, false);
            new Interrupter().start();
            for (int i = 0; i < 1000; ++i) {
                try {
                    roundRobinAsynchronousRunner.postRunnable(new DumbTask());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                Thread.sleep(50L);
            }
            System.out.println("Interrupted Threads: " + interruptedThreads.size());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static class DumbTask
    implements Runnable {
        static int count = 0;

        DumbTask() {
        }

        static synchronized int number() {
            return count++;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(200L);
                System.out.println("DumbTask complete! " + DumbTask.number());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    static class Interrupter
    extends Thread {
        Interrupter() {
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread[] threadArray = new Thread[1000];
                    ThreadUtils.enumerateAll(threadArray);
                    int n = 0;
                    while (threadArray[n] != null) {
                        if (threadArray[n].getName().indexOf("RunnableQueue.TaskThread") >= 0) {
                            threadArray[n].interrupt();
                            System.out.println("INTERRUPTED!");
                            interruptedThreads.add(threadArray[n]);
                            break;
                        }
                        ++n;
                    }
                    Thread.sleep(1000L);
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
        }
    }
}

