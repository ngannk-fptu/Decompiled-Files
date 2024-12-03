/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.HibernateException;
import org.hibernate.dialect.CUBRIDDialect;
import org.hibernate.dialect.Cache71Dialect;
import org.hibernate.dialect.DB2400Dialect;
import org.hibernate.dialect.DB2400V7R3Dialect;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.DerbyTenFiveDialect;
import org.hibernate.dialect.DerbyTenSevenDialect;
import org.hibernate.dialect.DerbyTenSixDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.FirebirdDialect;
import org.hibernate.dialect.FrontBaseDialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HANACloudColumnStoreDialect;
import org.hibernate.dialect.HANAColumnStoreDialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.Informix10Dialect;
import org.hibernate.dialect.Ingres10Dialect;
import org.hibernate.dialect.Ingres9Dialect;
import org.hibernate.dialect.IngresDialect;
import org.hibernate.dialect.InterbaseDialect;
import org.hibernate.dialect.MariaDB102Dialect;
import org.hibernate.dialect.MariaDB103Dialect;
import org.hibernate.dialect.MariaDB106Dialect;
import org.hibernate.dialect.MariaDB10Dialect;
import org.hibernate.dialect.MariaDB53Dialect;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.MckoiDialect;
import org.hibernate.dialect.MimerSQLDialect;
import org.hibernate.dialect.MySQL55Dialect;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.dialect.PointbaseDialect;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.dialect.PostgreSQL92Dialect;
import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.dialect.PostgresPlusDialect;
import org.hibernate.dialect.ProgressDialect;
import org.hibernate.dialect.SAPDBDialect;
import org.hibernate.dialect.SQLServer2005Dialect;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.dialect.SQLServer2016Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.dialect.SybaseAnywhereDialect;
import org.hibernate.dialect.Teradata14Dialect;
import org.hibernate.dialect.TimesTenDialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

public enum Database {
    CACHE{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return Cache71Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    }
    ,
    CUBRID{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return CUBRIDDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("CUBRID".equalsIgnoreCase(databaseName)) {
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    DB2{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return DB2400V7R3Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("DB2 UDB for AS/400".equals(databaseName)) {
                int majorVersion = info.getDatabaseMajorVersion();
                int minorVersion = info.getDatabaseMinorVersion();
                if (majorVersion > 7 || majorVersion == 7 && minorVersion >= 3) {
                    return Database.latestDialectInstance((Database)this);
                }
                return new DB2400Dialect();
            }
            if (databaseName.startsWith("DB2/")) {
                return new DB2Dialect();
            }
            return null;
        }
    }
    ,
    DERBY{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return DerbyTenSevenDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("Apache Derby".equals(databaseName)) {
                int majorVersion = info.getDatabaseMajorVersion();
                int minorVersion = info.getDatabaseMinorVersion();
                if (majorVersion > 10 || majorVersion == 10 && minorVersion >= 7) {
                    return Database.latestDialectInstance((Database)this);
                }
                if (majorVersion == 10 && minorVersion == 6) {
                    return new DerbyTenSixDialect();
                }
                if (majorVersion == 10 && minorVersion == 5) {
                    return new DerbyTenFiveDialect();
                }
                return new DerbyDialect();
            }
            return null;
        }
    }
    ,
    ENTERPRISEDB{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return PostgresPlusDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("EnterpriseDB".equals(databaseName)) {
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    FIREBIRD{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return FirebirdDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if (databaseName.startsWith("Firebird")) {
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    FRONTBASE{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return FrontBaseDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    }
    ,
    H2{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return H2Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("H2".equals(databaseName)) {
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    HANA{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return HANAColumnStoreDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            int databaseMajorVersion = info.getDatabaseMajorVersion();
            if ("HDB".equals(databaseName)) {
                if (databaseMajorVersion >= 4) {
                    return new HANACloudColumnStoreDialect();
                }
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    HSQL{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return HSQLDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("HSQL Database Engine".equals(databaseName)) {
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    INFORMIX{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return Informix10Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("Informix Dynamic Server".equals(databaseName)) {
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    INGRES{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return Ingres10Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("ingres".equalsIgnoreCase(databaseName)) {
                int majorVersion = info.getDatabaseMajorVersion();
                int minorVersion = info.getDatabaseMinorVersion();
                if (majorVersion < 9) {
                    return new IngresDialect();
                }
                if (majorVersion == 9) {
                    if (minorVersion > 2) {
                        return new Ingres9Dialect();
                    }
                    return new IngresDialect();
                }
                if (majorVersion == 10) {
                    return new Ingres10Dialect();
                }
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    INTERBASE{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return InterbaseDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    }
    ,
    MARIADB{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return MariaDB103Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            if (info.getDriverName() != null && info.getDriverName().startsWith("MariaDB")) {
                int majorVersion = info.getDatabaseMajorVersion();
                int minorVersion = info.getDatabaseMinorVersion();
                if (majorVersion == 10) {
                    if (minorVersion >= 6) {
                        return new MariaDB106Dialect();
                    }
                    if (minorVersion >= 3) {
                        return new MariaDB103Dialect();
                    }
                    if (minorVersion == 2) {
                        return new MariaDB102Dialect();
                    }
                    if (minorVersion >= 0) {
                        return new MariaDB10Dialect();
                    }
                    return new MariaDB53Dialect();
                }
                if (majorVersion > 5 || majorVersion == 5 && minorVersion >= 3) {
                    return new MariaDB53Dialect();
                }
                return new MariaDBDialect();
            }
            return null;
        }
    }
    ,
    MAXDB{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return SAPDBDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    }
    ,
    MCKOI{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return MckoiDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    }
    ,
    MIMERSQL{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return MimerSQLDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    }
    ,
    MYSQL{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return MySQL8Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("MySQL".equals(databaseName)) {
                int majorVersion = info.getDatabaseMajorVersion();
                int minorVersion = info.getDatabaseMinorVersion();
                if (majorVersion < 5) {
                    return new MySQLDialect();
                }
                if (majorVersion == 5) {
                    if (minorVersion < 5) {
                        return new MySQL5Dialect();
                    }
                    if (minorVersion < 7) {
                        return new MySQL55Dialect();
                    }
                    return new MySQL57Dialect();
                }
                if (majorVersion < 8) {
                    return new MySQL57Dialect();
                }
                if (majorVersion == 8) {
                    return new MySQL8Dialect();
                }
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    ORACLE{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return Oracle12cDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("Oracle".equals(databaseName)) {
                int majorVersion = info.getDatabaseMajorVersion();
                switch (majorVersion) {
                    case 12: {
                        return new Oracle12cDialect();
                    }
                    case 10: 
                    case 11: {
                        return new Oracle10gDialect();
                    }
                    case 9: {
                        return new Oracle9iDialect();
                    }
                    case 8: {
                        return new Oracle8iDialect();
                    }
                }
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    POINTBASE{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return PointbaseDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    }
    ,
    POSTGRESQL{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return PostgreSQL10Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("PostgreSQL".equals(databaseName)) {
                int majorVersion = info.getDatabaseMajorVersion();
                int minorVersion = info.getDatabaseMinorVersion();
                if (majorVersion < 8) {
                    return new PostgreSQL81Dialect();
                }
                if (majorVersion == 8) {
                    return minorVersion >= 2 ? new PostgreSQL82Dialect() : new PostgreSQL81Dialect();
                }
                if (majorVersion == 9) {
                    if (minorVersion < 2) {
                        return new PostgreSQL9Dialect();
                    }
                    if (minorVersion < 4) {
                        return new PostgreSQL92Dialect();
                    }
                    if (minorVersion < 5) {
                        return new PostgreSQL94Dialect();
                    }
                    return new PostgreSQL95Dialect();
                }
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    PROGRESS{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return ProgressDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    }
    ,
    SQLSERVER{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return SQLServer2016Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if (databaseName.startsWith("Microsoft SQL Server")) {
                int majorVersion = info.getDatabaseMajorVersion();
                switch (majorVersion) {
                    case 8: {
                        return new SQLServerDialect();
                    }
                    case 9: {
                        return new SQLServer2005Dialect();
                    }
                    case 10: {
                        return new SQLServer2008Dialect();
                    }
                    case 11: 
                    case 12: {
                        return new SQLServer2012Dialect();
                    }
                    case 13: 
                    case 14: 
                    case 15: {
                        return new SQLServer2016Dialect();
                    }
                }
                if (majorVersion < 8) {
                    return new SQLServerDialect();
                }
                return Database.latestDialectInstance((Database)this);
            }
            return null;
        }
    }
    ,
    SYBASE{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return SybaseASE15Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            String databaseName = info.getDatabaseName();
            if ("Sybase SQL Server".equals(databaseName) || "Adaptive Server Enterprise".equals(databaseName)) {
                return Database.latestDialectInstance((Database)this);
            }
            if (databaseName.startsWith("Adaptive Server Anywhere") || "SQL Anywhere".equals(databaseName)) {
                return new SybaseAnywhereDialect();
            }
            return null;
        }
    }
    ,
    TERADATA{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return Teradata14Dialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    }
    ,
    TIMESTEN{

        @Override
        public Class<? extends Dialect> latestDialect() {
            return TimesTenDialect.class;
        }

        @Override
        public Dialect resolveDialect(DialectResolutionInfo info) {
            return null;
        }
    };


    public abstract Class<? extends Dialect> latestDialect();

    public abstract Dialect resolveDialect(DialectResolutionInfo var1);

    private static Dialect latestDialectInstance(Database database) {
        try {
            return database.latestDialect().newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new HibernateException(e);
        }
    }
}

