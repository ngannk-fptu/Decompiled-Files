/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import java.util.Calendar;
import java.util.Date;
import org.apache.jackrabbit.api.stats.QueryStatDto;

public class QueryStatDtoImpl
implements QueryStatDto {
    private static final long serialVersionUID = 1L;
    private long position;
    private final Date creationTime;
    private final long durationMs;
    private final String language;
    private final String statement;
    private int occurrenceCount = 1;

    public QueryStatDtoImpl(String language, String statement, long durationMs) {
        this.durationMs = durationMs;
        this.language = language;
        this.statement = statement;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis() - durationMs);
        this.creationTime = c.getTime();
    }

    @Override
    public long getDuration() {
        return this.durationMs;
    }

    @Override
    public String getLanguage() {
        return this.language;
    }

    @Override
    public String getStatement() {
        return this.statement;
    }

    @Override
    public String getCreationTime() {
        return this.creationTime.toString();
    }

    @Override
    public long getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(long position) {
        this.position = position;
    }

    public String toString() {
        return "QueryStat [creationTime=" + this.creationTime + ", duration=" + this.durationMs + ", position " + this.position + ", language=" + this.language + ", statement=" + this.statement + "]";
    }

    @Override
    public int getOccurrenceCount() {
        return this.occurrenceCount;
    }

    public void setOccurrenceCount(int occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.language == null ? 0 : this.language.hashCode());
        result = 31 * result + (this.statement == null ? 0 : this.statement.hashCode());
        return result;
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
        QueryStatDtoImpl other = (QueryStatDtoImpl)obj;
        if (this.language == null ? other.language != null : !this.language.equals(other.language)) {
            return false;
        }
        return !(this.statement == null ? other.statement != null : !this.statement.equals(other.statement));
    }
}

