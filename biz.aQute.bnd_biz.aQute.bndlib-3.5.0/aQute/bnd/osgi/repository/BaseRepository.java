/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.repository;

import aQute.bnd.osgi.resource.ResourceUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.AndExpression;
import org.osgi.service.repository.ExpressionCombiner;
import org.osgi.service.repository.IdentityExpression;
import org.osgi.service.repository.NotExpression;
import org.osgi.service.repository.OrExpression;
import org.osgi.service.repository.Repository;
import org.osgi.service.repository.RequirementBuilder;
import org.osgi.service.repository.RequirementExpression;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

public abstract class BaseRepository
implements Repository {
    private static final RequirementExpression[] EMPTY = new RequirementExpression[0];
    static IdentityExpression all;

    @Override
    public Promise<Collection<Resource>> findProviders(RequirementExpression expression) {
        HashSet<Resource> providers = new HashSet<Resource>();
        this.dispatch(expression, providers);
        return Promises.resolved(providers);
    }

    private void dispatch(RequirementExpression expression, Set<Resource> providers) {
        if (expression instanceof IdentityExpression) {
            Map<Requirement, Collection<Capability>> capabilities = this.findProviders(Collections.singleton(((IdentityExpression)expression).getRequirement()));
            for (Collection<Capability> caps : capabilities.values()) {
                for (Capability c : caps) {
                    providers.add(c.getResource());
                }
            }
        } else if (expression instanceof OrExpression) {
            for (RequirementExpression re : ((OrExpression)expression).getRequirementExpressions()) {
                this.dispatch(re, providers);
            }
        } else if (expression instanceof AndExpression) {
            List<RequirementExpression> requirementExpressions = ((AndExpression)expression).getRequirementExpressions();
            if (requirementExpressions.isEmpty()) {
                return;
            }
            if (requirementExpressions.size() == 1) {
                this.dispatch(requirementExpressions.get(0), providers);
                return;
            }
            HashSet<Resource> subset = new HashSet<Resource>();
            this.dispatch(requirementExpressions.get(0), subset);
            for (int i = 1; i < requirementExpressions.size(); ++i) {
                Iterator it = subset.iterator();
                while (it.hasNext()) {
                    Resource resource = (Resource)it.next();
                    RequirementExpression re = requirementExpressions.get(i);
                    if (this.matches(re, resource)) continue;
                    it.remove();
                    if (!subset.isEmpty()) continue;
                    return;
                }
            }
            providers.addAll(subset);
        } else if (expression instanceof NotExpression) {
            HashSet<Resource> allSet = new HashSet<Resource>();
            this.dispatch(all, allSet);
            RequirementExpression re = ((NotExpression)expression).getRequirementExpression();
            Iterator it = allSet.iterator();
            while (it.hasNext()) {
                Resource resource = (Resource)it.next();
                if (!this.matches(re, resource)) continue;
                it.remove();
                if (!allSet.isEmpty()) continue;
                return;
            }
            providers.addAll(allSet);
        } else {
            throw new UnsupportedOperationException("Unknown expression type " + expression.getClass());
        }
    }

    private boolean matches(RequirementExpression expression, Resource resource) {
        if (expression instanceof IdentityExpression) {
            Requirement r = ((IdentityExpression)expression).getRequirement();
            return ResourceUtils.matches(r, resource);
        }
        if (expression instanceof OrExpression) {
            List<RequirementExpression> res = ((OrExpression)expression).getRequirementExpressions();
            for (RequirementExpression re : res) {
                if (!this.matches(re, resource)) continue;
                return true;
            }
            return false;
        }
        if (expression instanceof AndExpression) {
            List<RequirementExpression> res = ((AndExpression)expression).getRequirementExpressions();
            for (RequirementExpression re : res) {
                if (this.matches(re, resource)) continue;
                return false;
            }
            return true;
        }
        if (expression instanceof NotExpression) {
            RequirementExpression re = ((NotExpression)expression).getRequirementExpression();
            return !this.matches(re, resource);
        }
        throw new UnsupportedOperationException("Unknown expression type " + expression.getClass());
    }

    @Override
    public ExpressionCombiner getExpressionCombiner() {
        return new ExpressionCombiner(){

            @Override
            public OrExpression or(RequirementExpression expr1, RequirementExpression expr2, RequirementExpression ... moreExprs) {
                final List<RequirementExpression> exprs = this.combine(expr1, expr2, moreExprs);
                return new OrExpression(){

                    @Override
                    public List<RequirementExpression> getRequirementExpressions() {
                        return exprs;
                    }
                };
            }

            List<RequirementExpression> combine(RequirementExpression expr1, RequirementExpression expr2, RequirementExpression ... moreExprs) {
                ArrayList<RequirementExpression> exprs = new ArrayList<RequirementExpression>();
                exprs = new ArrayList();
                exprs.add(expr1);
                exprs.add(expr2);
                for (int i = 0; i < moreExprs.length; ++i) {
                    exprs.add(moreExprs[i]);
                }
                return Collections.unmodifiableList(exprs);
            }

            @Override
            public OrExpression or(RequirementExpression expr1, RequirementExpression expr2) {
                return this.or(expr1, expr2, EMPTY);
            }

            @Override
            public NotExpression not(final RequirementExpression expr) {
                return new NotExpression(){

                    @Override
                    public RequirementExpression getRequirementExpression() {
                        return expr;
                    }
                };
            }

            @Override
            public IdentityExpression identity(final Requirement req) {
                return new IdentityExpression(){

                    @Override
                    public Requirement getRequirement() {
                        return req;
                    }
                };
            }

            @Override
            public AndExpression and(RequirementExpression expr1, RequirementExpression expr2, RequirementExpression ... moreExprs) {
                final List<RequirementExpression> exprs = this.combine(expr1, expr2, moreExprs);
                return new AndExpression(){

                    @Override
                    public List<RequirementExpression> getRequirementExpressions() {
                        return exprs;
                    }
                };
            }

            @Override
            public AndExpression and(RequirementExpression expr1, RequirementExpression expr2) {
                return this.and(expr1, expr2, EMPTY);
            }
        };
    }

    @Override
    public RequirementBuilder newRequirementBuilder(String namespace) {
        final aQute.bnd.osgi.resource.RequirementBuilder rb = new aQute.bnd.osgi.resource.RequirementBuilder(namespace);
        return new RequirementBuilder(){

            @Override
            public RequirementBuilder setResource(Resource resource) {
                rb.setResource(resource);
                return this;
            }

            @Override
            public RequirementBuilder setDirectives(Map<String, String> directives) {
                rb.addDirectives(directives);
                return this;
            }

            @Override
            public RequirementBuilder setAttributes(Map<String, Object> attributes) {
                try {
                    rb.addAttributes(attributes);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return this;
            }

            @Override
            public IdentityExpression buildExpression() {
                return new IdentityExpression(){

                    @Override
                    public Requirement getRequirement() {
                        if (rb.getResource() == null) {
                            return rb.buildSyntheticRequirement();
                        }
                        return rb.build();
                    }
                };
            }

            @Override
            public Requirement build() {
                return rb.build();
            }

            @Override
            public RequirementBuilder addDirective(String name, String value) {
                rb.addDirective(name, value);
                return this;
            }

            @Override
            public RequirementBuilder addAttribute(String name, Object value) {
                try {
                    rb.addAttribute(name, value);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return this;
            }
        };
    }

    static {
        aQute.bnd.osgi.resource.RequirementBuilder rb = new aQute.bnd.osgi.resource.RequirementBuilder("osgi.identity");
        rb.addFilter("(osgi.identity=*)");
        final Requirement requireAll = rb.synthetic();
        all = new IdentityExpression(){

            @Override
            public Requirement getRequirement() {
                return requireAll;
            }
        };
    }
}

