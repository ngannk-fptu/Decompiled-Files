/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.network;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Generated;

public class NetworkCheckResult
implements Serializable {
    private boolean success;
    private String name;
    private List<String> failedDomains;
    private Exception exception;

    public boolean hasException() {
        return Objects.nonNull(this.exception);
    }

    public static NetworkCheckResult success(String name) {
        return new NetworkCheckResult(true, name, null, null);
    }

    public static NetworkCheckResult failed(String name, List<String> domains) {
        return new NetworkCheckResult(false, name, domains, null);
    }

    public static NetworkCheckResult failed(String name, String domain) {
        return new NetworkCheckResult(false, name, Collections.singletonList(domain), null);
    }

    public static NetworkCheckResult failed(String name, List<String> domains, Exception e) {
        return new NetworkCheckResult(false, name, domains, e);
    }

    @Generated
    public boolean isSuccess() {
        return this.success;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public List<String> getFailedDomains() {
        return this.failedDomains;
    }

    @Generated
    public Exception getException() {
        return this.exception;
    }

    @Generated
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setFailedDomains(List<String> failedDomains) {
        this.failedDomains = failedDomains;
    }

    @Generated
    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NetworkCheckResult)) {
            return false;
        }
        NetworkCheckResult other = (NetworkCheckResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        List<String> this$failedDomains = this.getFailedDomains();
        List<String> other$failedDomains = other.getFailedDomains();
        if (this$failedDomains == null ? other$failedDomains != null : !((Object)this$failedDomains).equals(other$failedDomains)) {
            return false;
        }
        Exception this$exception = this.getException();
        Exception other$exception = other.getException();
        return !(this$exception == null ? other$exception != null : !this$exception.equals(other$exception));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof NetworkCheckResult;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        List<String> $failedDomains = this.getFailedDomains();
        result = result * 59 + ($failedDomains == null ? 43 : ((Object)$failedDomains).hashCode());
        Exception $exception = this.getException();
        result = result * 59 + ($exception == null ? 43 : $exception.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "NetworkCheckResult(success=" + this.isSuccess() + ", name=" + this.getName() + ", failedDomains=" + this.getFailedDomains() + ", exception=" + this.getException() + ")";
    }

    @Generated
    public NetworkCheckResult(boolean success, String name, List<String> failedDomains, Exception exception) {
        this.success = success;
        this.name = name;
        this.failedDomains = failedDomains;
        this.exception = exception;
    }
}

