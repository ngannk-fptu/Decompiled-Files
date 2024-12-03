/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.modifier;

import net.bytebuddy.description.modifier.ModifierContributor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum Visibility implements ModifierContributor.ForType,
ModifierContributor.ForMethod,
ModifierContributor.ForField
{
    PUBLIC(1),
    PACKAGE_PRIVATE(0),
    PROTECTED(4),
    PRIVATE(2);

    private final int mask;

    private Visibility(int mask) {
        this.mask = mask;
    }

    @Override
    public int getMask() {
        return this.mask;
    }

    @Override
    public int getRange() {
        return 7;
    }

    @Override
    public boolean isDefault() {
        return this == PACKAGE_PRIVATE;
    }

    public boolean isPublic() {
        return (this.mask & 1) != 0;
    }

    public boolean isProtected() {
        return (this.mask & 4) != 0;
    }

    public boolean isPackagePrivate() {
        return !this.isPublic() && !this.isPrivate() && !this.isProtected();
    }

    public boolean isPrivate() {
        return (this.mask & 2) != 0;
    }

    public Visibility expandTo(Visibility visibility) {
        switch (visibility) {
            case PUBLIC: {
                return PUBLIC;
            }
            case PROTECTED: {
                return this == PUBLIC ? PUBLIC : visibility;
            }
            case PACKAGE_PRIVATE: {
                return this == PRIVATE ? PACKAGE_PRIVATE : this;
            }
            case PRIVATE: {
                return this;
            }
        }
        throw new IllegalStateException("Unexpected visibility: " + visibility);
    }
}

