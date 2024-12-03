/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.EnhancedProjection;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.type.Type;

public class ProjectionList
implements EnhancedProjection {
    private List<Projection> elements = new ArrayList<Projection>();

    protected ProjectionList() {
    }

    @Deprecated
    public ProjectionList create() {
        return new ProjectionList();
    }

    public ProjectionList add(Projection projection) {
        this.elements.add(projection);
        return this;
    }

    public ProjectionList add(Projection projection, String alias) {
        return this.add(Projections.alias(projection, alias));
    }

    @Override
    public boolean isGrouped() {
        for (Projection projection : this.elements) {
            if (!projection.isGrouped()) continue;
            return true;
        }
        return false;
    }

    @Override
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        ArrayList types = new ArrayList(this.getLength());
        for (Projection projection : this.elements) {
            Type[] elemTypes = projection.getTypes(criteria, criteriaQuery);
            Collections.addAll(types, elemTypes);
        }
        return types.toArray(new Type[types.size()]);
    }

    @Override
    public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) throws HibernateException {
        StringBuilder buf = new StringBuilder();
        String separator = "";
        for (Projection projection : this.elements) {
            buf.append(separator).append(projection.toSqlString(criteria, loc, criteriaQuery));
            loc += ProjectionList.getColumnAliases(loc, criteria, criteriaQuery, projection).length;
            separator = ", ";
        }
        return buf.toString();
    }

    @Override
    public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        StringBuilder buf = new StringBuilder();
        String separator = "";
        for (Projection projection : this.elements) {
            if (!projection.isGrouped()) continue;
            buf.append(separator).append(projection.toGroupSqlString(criteria, criteriaQuery));
            separator = ", ";
        }
        return buf.toString();
    }

    @Override
    public String[] getColumnAliases(int loc) {
        int position = loc;
        ArrayList result = new ArrayList(this.getLength());
        for (Projection projection : this.elements) {
            String[] aliases = projection.getColumnAliases(position);
            Collections.addAll(result, aliases);
            position += aliases.length;
        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    public String[] getColumnAliases(int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
        int position = loc;
        ArrayList result = new ArrayList(this.getLength());
        for (Projection projection : this.elements) {
            String[] aliases = ProjectionList.getColumnAliases(position, criteria, criteriaQuery, projection);
            Collections.addAll(result, aliases);
            position += aliases.length;
        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    public String[] getColumnAliases(String alias, int loc) {
        int position = loc;
        for (Projection projection : this.elements) {
            String[] aliases = projection.getColumnAliases(alias, position);
            if (aliases != null) {
                return aliases;
            }
            position += projection.getColumnAliases(position).length;
        }
        return null;
    }

    @Override
    public String[] getColumnAliases(String alias, int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
        int position = loc;
        for (Projection projection : this.elements) {
            String[] aliases = ProjectionList.getColumnAliases(alias, position, criteria, criteriaQuery, projection);
            if (aliases != null) {
                return aliases;
            }
            position += ProjectionList.getColumnAliases(position, criteria, criteriaQuery, projection).length;
        }
        return null;
    }

    private static String[] getColumnAliases(int loc, Criteria criteria, CriteriaQuery criteriaQuery, Projection projection) {
        return projection instanceof EnhancedProjection ? ((EnhancedProjection)projection).getColumnAliases(loc, criteria, criteriaQuery) : projection.getColumnAliases(loc);
    }

    private static String[] getColumnAliases(String alias, int loc, Criteria criteria, CriteriaQuery criteriaQuery, Projection projection) {
        return projection instanceof EnhancedProjection ? ((EnhancedProjection)projection).getColumnAliases(alias, loc, criteria, criteriaQuery) : projection.getColumnAliases(alias, loc);
    }

    @Override
    public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery) {
        for (Projection projection : this.elements) {
            Type[] types = projection.getTypes(alias, criteria, criteriaQuery);
            if (types == null) continue;
            return types;
        }
        return null;
    }

    @Override
    public String[] getAliases() {
        ArrayList result = new ArrayList(this.getLength());
        for (Projection projection : this.elements) {
            String[] aliases = projection.getAliases();
            Collections.addAll(result, aliases);
        }
        return result.toArray(new String[result.size()]);
    }

    public Projection getProjection(int i) {
        return this.elements.get(i);
    }

    public int getLength() {
        return this.elements.size();
    }

    public String toString() {
        return this.elements.toString();
    }
}

