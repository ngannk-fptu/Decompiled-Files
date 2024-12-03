/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.MaybeEmptyCallableMemberDescriptor;

final class EmptyCallableMemberDescriptor
extends MaybeEmptyCallableMemberDescriptor {
    static final EmptyCallableMemberDescriptor NO_SUCH_METHOD = new EmptyCallableMemberDescriptor();
    static final EmptyCallableMemberDescriptor AMBIGUOUS_METHOD = new EmptyCallableMemberDescriptor();

    private EmptyCallableMemberDescriptor() {
    }
}

