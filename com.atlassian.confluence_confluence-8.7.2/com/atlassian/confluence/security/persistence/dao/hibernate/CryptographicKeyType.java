/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.hibernate.BucketClobStringType
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import com.atlassian.confluence.security.persistence.dao.hibernate.KeyTransferBean;
import com.atlassian.hibernate.BucketClobStringType;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class CryptographicKeyType
extends BucketClobStringType {
    private static final int OFFSET_TYPE = 0;
    private static final int OFFSET_ALGORITHM = 1;
    private static final int OFFSET_KEYSPEC = 2;

    public Class returnedClass() {
        return Key.class;
    }

    public int[] sqlTypes() {
        int[] types = new int[]{12, 12, super.sqlTypes()[0]};
        return types;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String encodedKey = (String)super.nullSafeGet(rs, new String[]{names[2]}, session, owner);
        String typeString = rs.getString(names[0]);
        KeyTransferBean bean = new KeyTransferBean(typeString, rs.getString(names[1]), encodedKey);
        return bean.asKey();
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (!(value instanceof PrivateKey) && !(value instanceof PublicKey)) {
            throw new IllegalArgumentException("Object is not an instance of java.security.{PublicKey,PrivateKey}");
        }
        Key key = (Key)value;
        KeyTransferBean bean = new KeyTransferBean(key);
        st.setString(index + 0, bean.getKeyType());
        st.setString(index + 1, key.getAlgorithm());
        super.nullSafeSet(st, (Object)bean.getEncodedKey(), index + 2, session);
    }
}

