/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception.spi;

import java.sql.SQLException;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;

public abstract class TemplatedViolatedConstraintNameExtracter
implements ViolatedConstraintNameExtracter {
    @Override
    public String extractConstraintName(SQLException sqle) {
        try {
            String constraintName = null;
            do {
                constraintName = this.doExtractConstraintName(sqle);
                if (sqle.getNextException() == null || sqle.getNextException() == sqle) break;
                sqle = sqle.getNextException();
            } while (constraintName == null);
            return constraintName;
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }

    protected abstract String doExtractConstraintName(SQLException var1) throws NumberFormatException;

    protected String extractUsingTemplate(String templateStart, String templateEnd, String message) {
        int templateStartPosition = message.indexOf(templateStart);
        if (templateStartPosition < 0) {
            return null;
        }
        int start = templateStartPosition + templateStart.length();
        int end = message.indexOf(templateEnd, start);
        if (end < 0) {
            end = message.length();
        }
        return message.substring(start, end);
    }
}

