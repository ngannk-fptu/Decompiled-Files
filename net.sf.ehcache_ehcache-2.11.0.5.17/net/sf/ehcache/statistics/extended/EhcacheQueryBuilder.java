/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.HashSet;
import java.util.Set;
import net.sf.ehcache.Cache;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Matchers;
import org.terracotta.context.query.Query;
import org.terracotta.context.query.QueryBuilder;

class EhcacheQueryBuilder {
    EhcacheQueryBuilder() {
    }

    static EhcacheQuery cache() {
        return new EhcacheQuery(QueryBuilder.queryBuilder().build()).children(Cache.class);
    }

    static EhcacheQuery children() {
        return new EhcacheQuery(QueryBuilder.queryBuilder().build()).children();
    }

    static EhcacheQuery descendants() {
        return new EhcacheQuery(QueryBuilder.queryBuilder().build()).descendants();
    }

    static final class EhcacheQuery
    implements Query {
        private final Query query;

        private EhcacheQuery(Query query) {
            this.query = query;
        }

        EhcacheQuery children() {
            return new EhcacheQuery(QueryBuilder.queryBuilder().chain(this.query).children().build());
        }

        EhcacheQuery children(Class<?> klazz) {
            return new EhcacheQuery(QueryBuilder.queryBuilder().chain(this.query).children().filter(Matchers.context(Matchers.identifier(Matchers.subclassOf(klazz)))).build());
        }

        EhcacheQuery descendants() {
            return new EhcacheQuery(QueryBuilder.queryBuilder().chain(this.query).descendants().build());
        }

        EhcacheQuery add(final EhcacheQuery chain) {
            return new EhcacheQuery(QueryBuilder.queryBuilder().chain(this.query).chain(new Query(){

                @Override
                public Set<TreeNode> execute(Set<TreeNode> input) {
                    HashSet<TreeNode> result = new HashSet<TreeNode>();
                    result.addAll(input);
                    result.addAll(chain.execute(input));
                    return result;
                }
            }).build());
        }

        EhcacheQuery exclude(Class<?> klazz) {
            return new EhcacheQuery(QueryBuilder.queryBuilder().chain(this.query).filter(Matchers.context(Matchers.identifier(Matchers.not(Matchers.subclassOf(klazz))))).build());
        }

        @Override
        public Set<TreeNode> execute(Set<TreeNode> input) {
            return this.query.execute(input);
        }
    }
}

