/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.policy.actions;

import com.amazonaws.auth.policy.Action;

public enum KMSActions implements Action
{
    AllKMSActions("kms:*"),
    CancelKeyDeletion("kms:CancelKeyDeletion"),
    ConnectCustomKeyStore("kms:ConnectCustomKeyStore"),
    CreateAlias("kms:CreateAlias"),
    CreateCustomKeyStore("kms:CreateCustomKeyStore"),
    CreateGrant("kms:CreateGrant"),
    CreateKey("kms:CreateKey"),
    Decrypt("kms:Decrypt"),
    DeleteAlias("kms:DeleteAlias"),
    DeleteCustomKeyStore("kms:DeleteCustomKeyStore"),
    DeleteImportedKeyMaterial("kms:DeleteImportedKeyMaterial"),
    DescribeCustomKeyStores("kms:DescribeCustomKeyStores"),
    DescribeKey("kms:DescribeKey"),
    DisableKey("kms:DisableKey"),
    DisableKeyRotation("kms:DisableKeyRotation"),
    DisconnectCustomKeyStore("kms:DisconnectCustomKeyStore"),
    EnableKey("kms:EnableKey"),
    EnableKeyRotation("kms:EnableKeyRotation"),
    Encrypt("kms:Encrypt"),
    GenerateDataKey("kms:GenerateDataKey"),
    GenerateDataKeyPair("kms:GenerateDataKeyPair"),
    GenerateDataKeyPairWithoutPlaintext("kms:GenerateDataKeyPairWithoutPlaintext"),
    GenerateDataKeyWithoutPlaintext("kms:GenerateDataKeyWithoutPlaintext"),
    GenerateRandom("kms:GenerateRandom"),
    GetKeyPolicy("kms:GetKeyPolicy"),
    GetKeyRotationStatus("kms:GetKeyRotationStatus"),
    GetParametersForImport("kms:GetParametersForImport"),
    GetPublicKey("kms:GetPublicKey"),
    ImportKeyMaterial("kms:ImportKeyMaterial"),
    ListAliases("kms:ListAliases"),
    ListGrants("kms:ListGrants"),
    ListKeyPolicies("kms:ListKeyPolicies"),
    ListKeys("kms:ListKeys"),
    ListResourceTags("kms:ListResourceTags"),
    ListRetirableGrants("kms:ListRetirableGrants"),
    PutKeyPolicy("kms:PutKeyPolicy"),
    ReEncryptFrom("kms:ReEncryptFrom"),
    ReEncryptTo("kms:ReEncryptTo"),
    RetireGrant("kms:RetireGrant"),
    RevokeGrant("kms:RevokeGrant"),
    ScheduleKeyDeletion("kms:ScheduleKeyDeletion"),
    Sign("kms:Sign"),
    TagResource("kms:TagResource"),
    UntagResource("kms:UntagResource"),
    UpdateAlias("kms:UpdateAlias"),
    UpdateCustomKeyStore("kms:UpdateCustomKeyStore"),
    UpdateKeyDescription("kms:UpdateKeyDescription"),
    Verify("kms:Verify");

    private final String action;

    private KMSActions(String action) {
        this.action = action;
    }

    @Override
    public String getActionName() {
        return this.action;
    }
}

