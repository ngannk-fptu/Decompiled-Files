/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.encryption;

public class AccessPermission {
    private static final int DEFAULT_PERMISSIONS = -4;
    private static final int PRINT_BIT = 3;
    private static final int MODIFICATION_BIT = 4;
    private static final int EXTRACT_BIT = 5;
    private static final int MODIFY_ANNOTATIONS_BIT = 6;
    private static final int FILL_IN_FORM_BIT = 9;
    private static final int EXTRACT_FOR_ACCESSIBILITY_BIT = 10;
    private static final int ASSEMBLE_DOCUMENT_BIT = 11;
    private static final int FAITHFUL_PRINT_BIT = 12;
    private int bytes;
    private boolean readOnly = false;

    public AccessPermission() {
        this.bytes = -4;
    }

    public AccessPermission(byte[] b) {
        this.bytes = 0;
        this.bytes |= b[0] & 0xFF;
        this.bytes <<= 8;
        this.bytes |= b[1] & 0xFF;
        this.bytes <<= 8;
        this.bytes |= b[2] & 0xFF;
        this.bytes <<= 8;
        this.bytes |= b[3] & 0xFF;
    }

    public AccessPermission(int permissions) {
        this.bytes = permissions;
    }

    private boolean isPermissionBitOn(int bit) {
        return (this.bytes & 1 << bit - 1) != 0;
    }

    private boolean setPermissionBit(int bit, boolean value) {
        int permissions = this.bytes;
        permissions = value ? (permissions |= 1 << bit - 1) : (permissions &= ~(1 << bit - 1));
        this.bytes = permissions;
        return (this.bytes & 1 << bit - 1) != 0;
    }

    public boolean isOwnerPermission() {
        return this.canAssembleDocument() && this.canExtractContent() && this.canExtractForAccessibility() && this.canFillInForm() && this.canModify() && this.canModifyAnnotations() && this.canPrint() && this.canPrintFaithful();
    }

    public static AccessPermission getOwnerAccessPermission() {
        AccessPermission ret = new AccessPermission();
        ret.setCanAssembleDocument(true);
        ret.setCanExtractContent(true);
        ret.setCanExtractForAccessibility(true);
        ret.setCanFillInForm(true);
        ret.setCanModify(true);
        ret.setCanModifyAnnotations(true);
        ret.setCanPrint(true);
        ret.setCanPrintFaithful(true);
        return ret;
    }

    public int getPermissionBytesForPublicKey() {
        this.setPermissionBit(1, true);
        this.setPermissionBit(7, false);
        this.setPermissionBit(8, false);
        for (int i = 13; i <= 32; ++i) {
            this.setPermissionBit(i, false);
        }
        return this.bytes;
    }

    public int getPermissionBytes() {
        return this.bytes;
    }

    public boolean canPrint() {
        return this.isPermissionBitOn(3);
    }

    public void setCanPrint(boolean allowPrinting) {
        if (!this.readOnly) {
            this.setPermissionBit(3, allowPrinting);
        }
    }

    public boolean canModify() {
        return this.isPermissionBitOn(4);
    }

    public void setCanModify(boolean allowModifications) {
        if (!this.readOnly) {
            this.setPermissionBit(4, allowModifications);
        }
    }

    public boolean canExtractContent() {
        return this.isPermissionBitOn(5);
    }

    public void setCanExtractContent(boolean allowExtraction) {
        if (!this.readOnly) {
            this.setPermissionBit(5, allowExtraction);
        }
    }

    public boolean canModifyAnnotations() {
        return this.isPermissionBitOn(6);
    }

    public void setCanModifyAnnotations(boolean allowAnnotationModification) {
        if (!this.readOnly) {
            this.setPermissionBit(6, allowAnnotationModification);
        }
    }

    public boolean canFillInForm() {
        return this.isPermissionBitOn(9);
    }

    public void setCanFillInForm(boolean allowFillingInForm) {
        if (!this.readOnly) {
            this.setPermissionBit(9, allowFillingInForm);
        }
    }

    public boolean canExtractForAccessibility() {
        return this.isPermissionBitOn(10);
    }

    public void setCanExtractForAccessibility(boolean allowExtraction) {
        if (!this.readOnly) {
            this.setPermissionBit(10, allowExtraction);
        }
    }

    public boolean canAssembleDocument() {
        return this.isPermissionBitOn(11);
    }

    public void setCanAssembleDocument(boolean allowAssembly) {
        if (!this.readOnly) {
            this.setPermissionBit(11, allowAssembly);
        }
    }

    @Deprecated
    public boolean canPrintDegraded() {
        return this.isPermissionBitOn(12);
    }

    @Deprecated
    public void setCanPrintDegraded(boolean canPrintFaithful) {
        if (!this.readOnly) {
            this.setPermissionBit(12, canPrintFaithful);
        }
    }

    public boolean canPrintFaithful() {
        return this.isPermissionBitOn(12);
    }

    public void setCanPrintFaithful(boolean canPrintFaithful) {
        if (!this.readOnly) {
            this.setPermissionBit(12, canPrintFaithful);
        }
    }

    public void setReadOnly() {
        this.readOnly = true;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    protected boolean hasAnyRevision3PermissionSet() {
        if (this.canFillInForm()) {
            return true;
        }
        if (this.canExtractForAccessibility()) {
            return true;
        }
        if (this.canAssembleDocument()) {
            return true;
        }
        return this.canPrintFaithful();
    }
}

