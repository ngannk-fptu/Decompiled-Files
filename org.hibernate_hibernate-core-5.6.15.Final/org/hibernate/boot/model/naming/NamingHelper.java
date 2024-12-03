/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.naming.Identifier;

public class NamingHelper {
    public static final NamingHelper INSTANCE = new NamingHelper();
    private final String charset;

    public static NamingHelper withCharset(String charset) {
        return new NamingHelper(charset);
    }

    public NamingHelper() {
        this(null);
    }

    private NamingHelper(String charset) {
        this.charset = charset;
    }

    public String generateHashedFkName(String prefix, Identifier tableName, Identifier referencedTableName, List<Identifier> columnNames) {
        Identifier[] columnNamesArray = columnNames == null || columnNames.isEmpty() ? new Identifier[]{} : columnNames.toArray(new Identifier[columnNames.size()]);
        return this.generateHashedFkName(prefix, tableName, referencedTableName, columnNamesArray);
    }

    public String generateHashedFkName(String prefix, Identifier tableName, Identifier referencedTableName, Identifier ... columnNames) {
        StringBuilder sb = new StringBuilder().append("table`").append(tableName).append("`").append("references`").append(referencedTableName).append("`");
        Identifier[] alphabeticalColumns = (Identifier[])columnNames.clone();
        Arrays.sort(alphabeticalColumns, new Comparator<Identifier>(){

            @Override
            public int compare(Identifier o1, Identifier o2) {
                return o1.getCanonicalName().compareTo(o2.getCanonicalName());
            }
        });
        for (Identifier columnName : alphabeticalColumns) {
            sb.append("column`").append(columnName).append("`");
        }
        return prefix + this.hashedName(sb.toString());
    }

    public String generateHashedConstraintName(String prefix, Identifier tableName, Identifier ... columnNames) {
        StringBuilder sb = new StringBuilder("table`" + tableName + "`");
        Identifier[] alphabeticalColumns = (Identifier[])columnNames.clone();
        Arrays.sort(alphabeticalColumns, new Comparator<Identifier>(){

            @Override
            public int compare(Identifier o1, Identifier o2) {
                return o1.getCanonicalName().compareTo(o2.getCanonicalName());
            }
        });
        for (Identifier columnName : alphabeticalColumns) {
            sb.append("column`").append(columnName).append("`");
        }
        return prefix + this.hashedName(sb.toString());
    }

    public String generateHashedConstraintName(String prefix, Identifier tableName, List<Identifier> columnNames) {
        Identifier[] columnNamesArray = new Identifier[columnNames.size()];
        for (int i = 0; i < columnNames.size(); ++i) {
            columnNamesArray[i] = columnNames.get(i);
        }
        return this.generateHashedConstraintName(prefix, tableName, columnNamesArray);
    }

    public String hashedName(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(this.charset != null ? s.getBytes(this.charset) : s.getBytes());
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return bigInt.toString(35);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new HibernateException("Unable to generate a hashed name!", e);
        }
    }
}

