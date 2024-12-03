/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.testing;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.WaitFor;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.taskdefs.optional.testing.BlockFor;
import org.apache.tools.ant.taskdefs.optional.testing.BuildTimeoutException;
import org.apache.tools.ant.util.WorkerAnt;

public class Funtest
extends Task {
    public static final String WARN_OVERRIDING = "Overriding previous definition of ";
    public static final String APPLICATION_FORCIBLY_SHUT_DOWN = "Application forcibly shut down";
    public static final String SHUTDOWN_INTERRUPTED = "Shutdown interrupted";
    public static final String SKIPPING_TESTS = "Condition failed -skipping tests";
    public static final String APPLICATION_EXCEPTION = "Application Exception";
    public static final String TEARDOWN_EXCEPTION = "Teardown Exception";
    private NestedCondition condition;
    private Parallel timedTests;
    private Sequential setup;
    private Sequential application;
    private BlockFor block;
    private Sequential tests;
    private Sequential reporting;
    private Sequential teardown;
    private long timeout;
    private long timeoutUnitMultiplier = 1L;
    private long shutdownTime = 10000L;
    private long shutdownUnitMultiplier = 1L;
    private String failureProperty;
    private String failureMessage = "Tests failed";
    private boolean failOnTeardownErrors = true;
    private BuildException testException;
    private BuildException teardownException;
    private BuildException applicationException;
    private BuildException taskException;

    private void logOverride(String name, Object definition) {
        if (definition != null) {
            this.log("Overriding previous definition of <" + name + '>', 2);
        }
    }

    public ConditionBase createCondition() {
        this.logOverride("condition", this.condition);
        this.condition = new NestedCondition();
        return this.condition;
    }

    public void addApplication(Sequential sequence) {
        this.logOverride("application", this.application);
        this.application = sequence;
    }

    public void addSetup(Sequential sequence) {
        this.logOverride("setup", this.setup);
        this.setup = sequence;
    }

    public void addBlock(BlockFor sequence) {
        this.logOverride("block", this.block);
        this.block = sequence;
    }

    public void addTests(Sequential sequence) {
        this.logOverride("tests", this.tests);
        this.tests = sequence;
    }

    public void addReporting(Sequential sequence) {
        this.logOverride("reporting", this.reporting);
        this.reporting = sequence;
    }

    public void addTeardown(Sequential sequence) {
        this.logOverride("teardown", this.teardown);
        this.teardown = sequence;
    }

    public void setFailOnTeardownErrors(boolean failOnTeardownErrors) {
        this.failOnTeardownErrors = failOnTeardownErrors;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public void setFailureProperty(String failureProperty) {
        this.failureProperty = failureProperty;
    }

    public void setShutdownTime(long shutdownTime) {
        this.shutdownTime = shutdownTime;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setTimeoutUnit(WaitFor.Unit unit) {
        this.timeoutUnitMultiplier = unit.getMultiplier();
    }

    public void setShutdownUnit(WaitFor.Unit unit) {
        this.shutdownUnitMultiplier = unit.getMultiplier();
    }

    public BuildException getApplicationException() {
        return this.applicationException;
    }

    public BuildException getTeardownException() {
        return this.teardownException;
    }

    public BuildException getTestException() {
        return this.testException;
    }

    public BuildException getTaskException() {
        return this.taskException;
    }

    private void bind(Task task) {
        task.bindToOwner(this);
        task.init();
    }

    private Parallel newParallel(long parallelTimeout) {
        Parallel par = new Parallel();
        this.bind(par);
        par.setFailOnAny(true);
        par.setTimeout(parallelTimeout);
        return par;
    }

    private Parallel newParallel(long parallelTimeout, Task child) {
        Parallel par = this.newParallel(parallelTimeout);
        par.addTask(child);
        return par;
    }

    private void validateTask(Task task, String role) {
        if (task != null && task.getProject() == null) {
            throw new BuildException("%s task is not bound to the project %s", role, task);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        this.validateTask(this.setup, "setup");
        this.validateTask(this.application, "application");
        this.validateTask(this.tests, "tests");
        this.validateTask(this.reporting, "reporting");
        this.validateTask(this.teardown, "teardown");
        if (this.condition != null && !this.condition.eval()) {
            this.log(SKIPPING_TESTS);
            return;
        }
        long timeoutMillis = this.timeout * this.timeoutUnitMultiplier;
        Parallel applicationRun = this.newParallel(timeoutMillis);
        WorkerAnt worker = new WorkerAnt(applicationRun, null);
        if (this.application != null) {
            applicationRun.addTask(this.application);
        }
        long testRunTimeout = 0L;
        Sequential testRun = new Sequential();
        this.bind(testRun);
        if (this.block != null) {
            TaskAdapter ta = new TaskAdapter(this.block);
            ta.bindToOwner(this);
            this.validateTask(ta, "block");
            testRun.addTask(ta);
            testRunTimeout = this.block.calculateMaxWaitMillis();
        }
        if (this.tests != null) {
            testRun.addTask(this.tests);
            testRunTimeout += timeoutMillis;
        }
        if (this.reporting != null) {
            testRun.addTask(this.reporting);
            testRunTimeout += timeoutMillis;
        }
        this.timedTests = this.newParallel(testRunTimeout, testRun);
        try {
            if (this.setup != null) {
                Parallel setupRun = this.newParallel(timeoutMillis, this.setup);
                setupRun.execute();
            }
            worker.start();
            this.timedTests.execute();
        }
        catch (BuildException e) {
            this.testException = e;
        }
        finally {
            if (this.teardown != null) {
                try {
                    Parallel teardownRun = this.newParallel(timeoutMillis, this.teardown);
                    teardownRun.execute();
                }
                catch (BuildException e) {
                    this.teardownException = e;
                }
            }
        }
        try {
            long shutdownTimeMillis = this.shutdownTime * this.shutdownUnitMultiplier;
            worker.waitUntilFinished(shutdownTimeMillis);
            if (worker.isAlive()) {
                this.log(APPLICATION_FORCIBLY_SHUT_DOWN, 1);
                worker.interrupt();
                worker.waitUntilFinished(shutdownTimeMillis);
            }
        }
        catch (InterruptedException e) {
            this.log(SHUTDOWN_INTERRUPTED, e, 3);
        }
        this.applicationException = worker.getBuildException();
        this.processExceptions();
    }

    protected void processExceptions() {
        this.taskException = this.testException;
        if (this.applicationException != null) {
            if (this.taskException == null || this.taskException instanceof BuildTimeoutException) {
                this.taskException = this.applicationException;
            } else {
                this.ignoringThrowable(APPLICATION_EXCEPTION, this.applicationException);
            }
        }
        if (this.teardownException != null) {
            if (this.taskException == null && this.failOnTeardownErrors) {
                this.taskException = this.teardownException;
            } else {
                this.ignoringThrowable(TEARDOWN_EXCEPTION, this.teardownException);
            }
        }
        if (this.failureProperty != null && this.getProject().getProperty(this.failureProperty) != null) {
            this.log(this.failureMessage);
            if (this.taskException == null) {
                this.taskException = new BuildException(this.failureMessage);
            }
        }
        if (this.taskException != null) {
            throw this.taskException;
        }
    }

    protected void ignoringThrowable(String type, Throwable thrown) {
        this.log(type + ": " + thrown.toString(), thrown, 1);
    }

    private static class NestedCondition
    extends ConditionBase
    implements Condition {
        private NestedCondition() {
        }

        @Override
        public boolean eval() {
            if (this.countConditions() != 1) {
                throw new BuildException("A single nested condition is required.");
            }
            return this.getConditions().nextElement().eval();
        }
    }
}

