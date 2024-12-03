/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.sql;

import org.jboss.logging.Logger;

public final class NationalizedTypeMappings {
    private static final Logger log = Logger.getLogger(NationalizedTypeMappings.class);
    @Deprecated
    public static final NationalizedTypeMappings INSTANCE = new NationalizedTypeMappings();

    private NationalizedTypeMappings() {
    }

    public static int toNationalizedTypeCode(int jdbcCode) {
        switch (jdbcCode) {
            case 1: {
                return -15;
            }
            case 2005: {
                return 2011;
            }
            case -1: {
                return -16;
            }
            case 12: {
                return -9;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Unable to locate nationalized jdbc-code equivalent for given jdbc code : " + jdbcCode));
        }
        return jdbcCode;
    }

    @Deprecated
    public int getCorrespondingNationalizedCode(int jdbcCode) {
        return NationalizedTypeMappings.toNationalizedTypeCode(jdbcCode);
    }
}

