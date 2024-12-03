/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.AssertionFailure;
import org.hibernate.type.VersionType;

public enum ReplicationMode {
    EXCEPTION{

        @Override
        public boolean shouldOverwriteCurrentVersion(Object entity, Object currentVersion, Object newVersion, VersionType versionType) {
            throw new AssertionFailure("should not be called");
        }
    }
    ,
    IGNORE{

        @Override
        public boolean shouldOverwriteCurrentVersion(Object entity, Object currentVersion, Object newVersion, VersionType versionType) {
            return false;
        }
    }
    ,
    OVERWRITE{

        @Override
        public boolean shouldOverwriteCurrentVersion(Object entity, Object currentVersion, Object newVersion, VersionType versionType) {
            return true;
        }
    }
    ,
    LATEST_VERSION{

        @Override
        public boolean shouldOverwriteCurrentVersion(Object entity, Object currentVersion, Object newVersion, VersionType versionType) {
            return versionType == null || versionType.getComparator().compare(currentVersion, newVersion) <= 0;
        }
    };


    public abstract boolean shouldOverwriteCurrentVersion(Object var1, Object var2, Object var3, VersionType var4);
}

