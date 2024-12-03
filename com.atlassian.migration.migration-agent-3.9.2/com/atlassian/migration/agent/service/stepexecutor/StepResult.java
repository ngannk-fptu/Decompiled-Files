/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.stepexecutor;

import javax.annotation.Nullable;
import lombok.Generated;

public class StepResult {
    private final boolean isSuccess;
    private final boolean isStopped;
    private final String message;
    private final String result;
    private final Throwable e;

    private StepResult(boolean isSuccess, boolean isStopped, String message, String result, Throwable e) {
        this.isSuccess = isSuccess;
        this.isStopped = isStopped;
        this.message = message;
        this.result = result;
        this.e = e;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public String getMessage() {
        return this.message;
    }

    @Nullable
    public String getResult() {
        return this.result;
    }

    @Nullable
    public Throwable getException() {
        return this.e;
    }

    public static StepResult failed(String message) {
        return new StepResult(false, false, message, null, null);
    }

    public static StepResult failed(String message, Throwable e) {
        return new StepResult(false, false, message, null, e);
    }

    public static StepResult stopped() {
        return new StepResult(false, true, null, null, null);
    }

    public static StepResult succeeded(String message) {
        return new StepResult(true, false, message, null, null);
    }

    public static StepResult succeeded(String message, String result) {
        return new StepResult(true, false, message, result, null);
    }

    @Generated
    public String toString() {
        return "StepResult(isSuccess=" + this.isSuccess() + ", isStopped=" + this.isStopped() + ", message=" + this.getMessage() + ", result=" + this.getResult() + ", e=" + this.e + ")";
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StepResult)) {
            return false;
        }
        StepResult other = (StepResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        if (this.isStopped() != other.isStopped()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        String this$result = this.getResult();
        String other$result = other.getResult();
        if (this$result == null ? other$result != null : !this$result.equals(other$result)) {
            return false;
        }
        Throwable this$e = this.e;
        Throwable other$e = other.e;
        return !(this$e == null ? other$e != null : !this$e.equals(other$e));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof StepResult;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        result = result * 59 + (this.isStopped() ? 79 : 97);
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        String $result = this.getResult();
        result = result * 59 + ($result == null ? 43 : $result.hashCode());
        Throwable $e = this.e;
        result = result * 59 + ($e == null ? 43 : $e.hashCode());
        return result;
    }
}

