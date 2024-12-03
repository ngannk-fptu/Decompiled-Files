/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateDateModel;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class SimpleDate
implements TemplateDateModel {
    private final Date date;
    private final int type;

    public SimpleDate(java.sql.Date date) {
        this(date, 2);
    }

    public SimpleDate(Time time) {
        this(time, 1);
    }

    public SimpleDate(Timestamp datetime) {
        this(datetime, 3);
    }

    public SimpleDate(Date date, int type) {
        if (date == null) {
            throw new IllegalArgumentException("date == null");
        }
        this.date = date;
        this.type = type;
    }

    @Override
    public Date getAsDate() {
        return this.date;
    }

    @Override
    public int getDateType() {
        return this.type;
    }

    public String toString() {
        return this.date.toString();
    }
}

