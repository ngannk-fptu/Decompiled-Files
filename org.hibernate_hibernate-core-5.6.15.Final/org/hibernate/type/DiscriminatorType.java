/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.IdentifierType;
import org.hibernate.type.LiteralType;

public interface DiscriminatorType<T>
extends IdentifierType<T>,
LiteralType<T> {
}

