/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.Policy;
import org.springframework.vault.support.VaultHealth;
import org.springframework.vault.support.VaultInitializationRequest;
import org.springframework.vault.support.VaultInitializationResponse;
import org.springframework.vault.support.VaultMount;
import org.springframework.vault.support.VaultUnsealStatus;

public interface VaultSysOperations {
    public boolean isInitialized() throws VaultException;

    public VaultInitializationResponse initialize(VaultInitializationRequest var1) throws VaultException;

    public void seal() throws VaultException;

    public VaultUnsealStatus unseal(String var1) throws VaultException;

    public VaultUnsealStatus getUnsealStatus() throws VaultException;

    public void mount(String var1, VaultMount var2) throws VaultException;

    public Map<String, VaultMount> getMounts() throws VaultException;

    public void unmount(String var1) throws VaultException;

    public void authMount(String var1, VaultMount var2) throws VaultException;

    public Map<String, VaultMount> getAuthMounts() throws VaultException;

    public void authUnmount(String var1) throws VaultException;

    public List<String> getPolicyNames() throws VaultException;

    @Nullable
    public Policy getPolicy(String var1) throws VaultException;

    public void createOrUpdatePolicy(String var1, Policy var2) throws VaultException;

    public void deletePolicy(String var1) throws VaultException;

    public VaultHealth health() throws VaultException;
}

