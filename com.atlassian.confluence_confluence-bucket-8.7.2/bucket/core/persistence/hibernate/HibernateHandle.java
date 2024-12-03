/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.Searchable
 *  org.hibernate.Hibernate
 */
package bucket.core.persistence.hibernate;

import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.Searchable;
import java.text.ParseException;
import org.hibernate.Hibernate;

@Deprecated
public class HibernateHandle
implements Handle {
    private final String className;
    private final long id;

    public HibernateHandle(String handleString) throws ParseException {
        int idx = handleString.indexOf("-");
        if (idx < 0) {
            throw new ParseException("Handle separator not found in " + handleString, 0);
        }
        if (idx == 0) {
            throw new ParseException("Handle starts with separator in " + handleString, 0);
        }
        if (idx == handleString.length() - 1) {
            throw new ParseException("Handle without an id in " + handleString, idx);
        }
        this.className = handleString.substring(0, idx).intern();
        try {
            this.id = Long.parseLong(handleString.substring(idx + 1));
        }
        catch (NumberFormatException e) {
            throw new ParseException("Handle with an invalid id in " + handleString, idx);
        }
    }

    public HibernateHandle(Searchable searchable) {
        this(Hibernate.getClass((Object)searchable).getName(), searchable.getId());
    }

    public HibernateHandle(String className, long id) {
        this.className = className;
        this.id = id;
    }

    public String getClassName() {
        return this.className;
    }

    public long getId() {
        return this.id;
    }

    public String toString() {
        return this.className + "-" + this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HibernateHandle)) {
            return false;
        }
        HibernateHandle handle = (HibernateHandle)o;
        if (this.id != handle.id) {
            return false;
        }
        return this.className.equals(handle.className);
    }

    public int hashCode() {
        int result = this.className.hashCode();
        result = 29 * result + (int)(this.id ^ this.id >>> 32);
        return result;
    }
}

