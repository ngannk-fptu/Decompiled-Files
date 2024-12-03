/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.factory;

import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.core.spi.factory.MessageBodyFactory;
import com.sun.jersey.core.util.KeyComparatorHashMap;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

public class ContextResolverFactory {
    private final Map<Type, Map<MediaType, ContextResolver>> resolver = new HashMap<Type, Map<MediaType, ContextResolver>>(4);
    private final Map<Type, ConcurrentHashMap<MediaType, ContextResolver>> cache = new HashMap<Type, ConcurrentHashMap<MediaType, ContextResolver>>(4);
    private static final NullContextResolverAdapter NULL_CONTEXT_RESOLVER = new NullContextResolverAdapter();

    public void init(ProviderServices providersServices, InjectableProviderFactory ipf) {
        HashMap rs = new HashMap();
        Set<ContextResolver> providers = providersServices.getProviders(ContextResolver.class);
        for (ContextResolver contextResolver : providers) {
            List<MediaType> ms = MediaTypes.createMediaTypes(contextResolver.getClass().getAnnotation(Produces.class));
            Type type = this.getParameterizedType(contextResolver.getClass());
            HashMap<MediaType, ArrayList<ContextResolver>> mr = (HashMap<MediaType, ArrayList<ContextResolver>>)rs.get(type);
            if (mr == null) {
                mr = new HashMap<MediaType, ArrayList<ContextResolver>>();
                rs.put(type, mr);
            }
            for (MediaType m : ms) {
                ArrayList<ContextResolver> crl = (ArrayList<ContextResolver>)mr.get(m);
                if (crl == null) {
                    crl = new ArrayList<ContextResolver>();
                    mr.put(m, crl);
                }
                crl.add(contextResolver);
            }
        }
        for (Map.Entry entry : rs.entrySet()) {
            KeyComparatorHashMap<MediaType, ContextResolver> mr = new KeyComparatorHashMap<MediaType, ContextResolver>(4, MessageBodyFactory.MEDIA_TYPE_COMPARATOR);
            this.resolver.put((Type)entry.getKey(), (Map<MediaType, ContextResolver>)mr);
            this.cache.put((Type)entry.getKey(), new ConcurrentHashMap(4));
            for (Map.Entry f : ((Map)entry.getValue()).entrySet()) {
                mr.put((MediaType)f.getKey(), this.reduce((List)f.getValue()));
            }
        }
        ipf.add(new InjectableProvider<Context, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable getInjectable(ComponentContext ic, Context ac, Type c) {
                if (!(c instanceof ParameterizedType)) {
                    return null;
                }
                ParameterizedType pType = (ParameterizedType)c;
                if (pType.getRawType() != ContextResolver.class) {
                    return null;
                }
                Type type = pType.getActualTypeArguments()[0];
                final ContextResolver cr = this.getResolver(ic, type);
                if (cr == null) {
                    return new Injectable(){

                        public Object getValue() {
                            return null;
                        }
                    };
                }
                return new Injectable(){

                    public Object getValue() {
                        return cr;
                    }
                };
            }

            ContextResolver getResolver(ComponentContext ic, Type type) {
                Map x = (Map)ContextResolverFactory.this.resolver.get(type);
                if (x == null) {
                    return null;
                }
                List<MediaType> ms = this.getMediaTypes(ic);
                if (ms.size() == 1) {
                    return ContextResolverFactory.this.resolve(type, ms.get(0));
                }
                TreeSet<MediaType> ml = new TreeSet<MediaType>(MediaTypes.MEDIA_TYPE_COMPARATOR);
                for (MediaType m : ms) {
                    if (m.isWildcardType()) {
                        ml.add(MediaTypes.GENERAL_MEDIA_TYPE);
                        continue;
                    }
                    if (m.isWildcardSubtype()) {
                        ml.add(new MediaType(m.getType(), "*"));
                        ml.add(MediaTypes.GENERAL_MEDIA_TYPE);
                        continue;
                    }
                    ml.add(new MediaType(m.getType(), m.getSubtype()));
                    ml.add(new MediaType(m.getType(), "*"));
                    ml.add(MediaTypes.GENERAL_MEDIA_TYPE);
                }
                ArrayList<ContextResolver> crl = new ArrayList<ContextResolver>(ml.size());
                for (MediaType m : ms) {
                    ContextResolver cr = (ContextResolver)x.get(m);
                    if (cr == null) continue;
                    crl.add(cr);
                }
                if (crl.isEmpty()) {
                    return null;
                }
                return new ContextResolverAdapter(crl);
            }

            List<MediaType> getMediaTypes(ComponentContext ic) {
                Produces p = null;
                for (Annotation a : ic.getAnnotations()) {
                    if (!(a instanceof Produces)) continue;
                    p = (Produces)a;
                    break;
                }
                return MediaTypes.createMediaTypes(p);
            }
        });
    }

    private Type getParameterizedType(Class c) {
        ReflectionHelper.DeclaringClassInterfacePair p = ReflectionHelper.getClass(c, ContextResolver.class);
        Type[] as = ReflectionHelper.getParameterizedTypeArguments(p);
        return as != null ? as[0] : Object.class;
    }

    private ContextResolver reduce(List<ContextResolver> r) {
        if (r.size() == 1) {
            return r.iterator().next();
        }
        return new ContextResolverAdapter(r);
    }

    public <T> ContextResolver<T> resolve(Type t, MediaType m) {
        ContextResolver cr;
        ConcurrentHashMap<MediaType, ContextResolver> crMapCache = this.cache.get(t);
        if (crMapCache == null) {
            return null;
        }
        if (m == null) {
            m = MediaTypes.GENERAL_MEDIA_TYPE;
        }
        if ((cr = crMapCache.get(m)) == null) {
            ContextResolver _cr;
            Map<MediaType, ContextResolver> crMap = this.resolver.get(t);
            if (m.isWildcardType()) {
                cr = crMap.get(MediaTypes.GENERAL_MEDIA_TYPE);
                if (cr == null) {
                    cr = NULL_CONTEXT_RESOLVER;
                }
            } else if (m.isWildcardSubtype()) {
                ContextResolver subTypeWildCard = crMap.get(m);
                ContextResolver wildCard = crMap.get(MediaTypes.GENERAL_MEDIA_TYPE);
                cr = new ContextResolverAdapter(subTypeWildCard, wildCard).reduce();
            } else {
                ContextResolver type = crMap.get(m);
                ContextResolver subTypeWildCard = crMap.get(new MediaType(m.getType(), "*"));
                ContextResolver wildCard = crMap.get(MediaType.WILDCARD_TYPE);
                cr = new ContextResolverAdapter(type, subTypeWildCard, wildCard).reduce();
            }
            if ((_cr = crMapCache.putIfAbsent(m, cr)) != null) {
                cr = _cr;
            }
        }
        return cr != NULL_CONTEXT_RESOLVER ? cr : null;
    }

    private static final class ContextResolverAdapter
    implements ContextResolver {
        private final ContextResolver[] cra;

        ContextResolverAdapter(ContextResolver ... cra) {
            this(ContextResolverAdapter.removeNull(cra));
        }

        ContextResolverAdapter(List<ContextResolver> crl) {
            this.cra = crl.toArray(new ContextResolver[crl.size()]);
        }

        public Object getContext(Class objectType) {
            for (ContextResolver cr : this.cra) {
                Object c = cr.getContext(objectType);
                if (c == null) continue;
                return c;
            }
            return null;
        }

        ContextResolver reduce() {
            if (this.cra.length == 0) {
                return NULL_CONTEXT_RESOLVER;
            }
            if (this.cra.length == 1) {
                return this.cra[0];
            }
            return this;
        }

        private static List<ContextResolver> removeNull(ContextResolver ... cra) {
            ArrayList<ContextResolver> crl = new ArrayList<ContextResolver>(cra.length);
            for (ContextResolver cr : cra) {
                if (cr == null) continue;
                crl.add(cr);
            }
            return crl;
        }
    }

    private static final class NullContextResolverAdapter
    implements ContextResolver {
        private NullContextResolverAdapter() {
        }

        public Object getContext(Class type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}

