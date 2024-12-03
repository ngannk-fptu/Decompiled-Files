/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.io.Serializable;
import java.util.Objects;

final class PoolKey
implements Serializable {
    private static final long serialVersionUID = 2252771047542484533L;
    private final String dataSourceName;
    private final String userName;

    PoolKey(String dataSourceName, String userName) {
        this.dataSourceName = dataSourceName;
        this.userName = userName;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PoolKey other = (PoolKey)obj;
        if (!Objects.equals(this.dataSourceName, other.dataSourceName)) {
            return false;
        }
        return Objects.equals(this.userName, other.userName);
    }

    public int hashCode() {
        return Objects.hash(this.dataSourceName, this.userName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append("PoolKey(");
        sb.append(this.dataSourceName);
        sb.append(')');
        return sb.toString();
    }
}

