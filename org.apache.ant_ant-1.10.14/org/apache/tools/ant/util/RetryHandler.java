/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.Retryable;

public class RetryHandler {
    private int retriesAllowed = 0;
    private Task task;

    public RetryHandler(int retriesAllowed, Task task) {
        this.retriesAllowed = retriesAllowed;
        this.task = task;
    }

    public void execute(Retryable exe, String desc) throws IOException {
        int retries = 0;
        while (true) {
            try {
                exe.execute();
            }
            catch (IOException e) {
                if (++retries > this.retriesAllowed && this.retriesAllowed > -1) {
                    this.task.log("try #" + retries + ": IO error (" + desc + "), number of maximum retries reached (" + this.retriesAllowed + "), giving up", 1);
                    throw e;
                }
                this.task.log("try #" + retries + ": IO error (" + desc + "), retrying", 1);
                continue;
            }
            break;
        }
    }
}

