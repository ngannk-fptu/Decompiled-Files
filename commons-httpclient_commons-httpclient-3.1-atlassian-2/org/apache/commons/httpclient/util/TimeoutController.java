/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.util;

public final class TimeoutController {
    private TimeoutController() {
    }

    public static void execute(Thread task, long timeout) throws TimeoutException {
        task.start();
        try {
            task.join(timeout);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        if (task.isAlive()) {
            task.interrupt();
            throw new TimeoutException();
        }
    }

    public static void execute(Runnable task, long timeout) throws TimeoutException {
        Thread t = new Thread(task, "Timeout guard");
        t.setDaemon(true);
        TimeoutController.execute(t, timeout);
    }

    public static class TimeoutException
    extends Exception {
    }
}

