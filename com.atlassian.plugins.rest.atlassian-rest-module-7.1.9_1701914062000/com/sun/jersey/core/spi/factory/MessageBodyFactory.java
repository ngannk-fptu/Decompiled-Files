/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.factory;

import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.core.util.KeyComparator;
import com.sun.jersey.core.util.KeyComparatorHashMap;
import com.sun.jersey.core.util.KeyComparatorLinkedHashMap;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

public class MessageBodyFactory
implements MessageBodyWorkers {
    static final KeyComparator<MediaType> MEDIA_TYPE_COMPARATOR = new KeyComparator<MediaType>(){

        @Override
        public boolean equals(MediaType x, MediaType y) {
            return x.getType().equalsIgnoreCase(y.getType()) && x.getSubtype().equalsIgnoreCase(y.getSubtype());
        }

        @Override
        public int hash(MediaType k) {
            return k.getType().toLowerCase().hashCode() + k.getSubtype().toLowerCase().hashCode();
        }

        @Override
        public int compare(MediaType o1, MediaType o2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
    private final ProviderServices providerServices;
    private final boolean deprecatedProviderPrecedence;
    private Map<MediaType, List<MessageBodyReader>> readerProviders;
    private Map<MediaType, List<MessageBodyWriter>> writerProviders;
    private List<MessageBodyWriterPair> writerListProviders;
    private Map<MediaType, List<MessageBodyReader>> customReaderProviders;
    private Map<MediaType, List<MessageBodyWriter>> customWriterProviders;
    private List<MessageBodyWriterPair> customWriterListProviders;

    public MessageBodyFactory(ProviderServices providerServices, boolean deprecatedProviderPrecedence) {
        this.providerServices = providerServices;
        this.deprecatedProviderPrecedence = deprecatedProviderPrecedence;
    }

    public void init() {
        this.initReaders();
        this.initWriters();
    }

    private void initReaders() {
        this.customReaderProviders = new KeyComparatorHashMap<MediaType, List<MessageBodyReader>>(MEDIA_TYPE_COMPARATOR);
        this.readerProviders = new KeyComparatorHashMap<MediaType, List<MessageBodyReader>>(MEDIA_TYPE_COMPARATOR);
        if (this.deprecatedProviderPrecedence) {
            this.initReaders(this.readerProviders, this.providerServices.getProvidersAndServices(MessageBodyReader.class));
        } else {
            this.initReaders(this.customReaderProviders, this.providerServices.getProviders(MessageBodyReader.class));
            this.initReaders(this.readerProviders, this.providerServices.getServices(MessageBodyReader.class));
        }
    }

    private void initReaders(Map<MediaType, List<MessageBodyReader>> providersMap, Set<MessageBodyReader> providersSet) {
        for (MessageBodyReader provider : providersSet) {
            List<MediaType> values = MediaTypes.createMediaTypes(provider.getClass().getAnnotation(Consumes.class));
            for (MediaType type : values) {
                this.getClassCapability(providersMap, provider, type);
            }
        }
        DistanceComparator dc = new DistanceComparator(MessageBodyReader.class);
        for (Map.Entry<MediaType, List<MessageBodyReader>> e : providersMap.entrySet()) {
            Collections.sort(e.getValue(), dc);
        }
    }

    private void initWriters() {
        this.customWriterProviders = new KeyComparatorHashMap<MediaType, List<MessageBodyWriter>>(MEDIA_TYPE_COMPARATOR);
        this.customWriterListProviders = new ArrayList<MessageBodyWriterPair>();
        this.writerProviders = new KeyComparatorHashMap<MediaType, List<MessageBodyWriter>>(MEDIA_TYPE_COMPARATOR);
        this.writerListProviders = new ArrayList<MessageBodyWriterPair>();
        if (this.deprecatedProviderPrecedence) {
            this.initWriters(this.writerProviders, this.writerListProviders, this.providerServices.getProvidersAndServices(MessageBodyWriter.class));
        } else {
            this.initWriters(this.customWriterProviders, this.customWriterListProviders, this.providerServices.getProviders(MessageBodyWriter.class));
            this.initWriters(this.writerProviders, this.writerListProviders, this.providerServices.getServices(MessageBodyWriter.class));
        }
    }

    private void initWriters(Map<MediaType, List<MessageBodyWriter>> providersMap, List<MessageBodyWriterPair> listProviders, Set<MessageBodyWriter> providersSet) {
        for (MessageBodyWriter provider : providersSet) {
            List<MediaType> values = MediaTypes.createMediaTypes(provider.getClass().getAnnotation(Produces.class));
            for (MediaType type : values) {
                this.getClassCapability(providersMap, provider, type);
            }
            listProviders.add(new MessageBodyWriterPair(provider, values));
        }
        final DistanceComparator dc = new DistanceComparator(MessageBodyWriter.class);
        for (Map.Entry<MediaType, List<MessageBodyWriter>> e : providersMap.entrySet()) {
            Collections.sort(e.getValue(), dc);
        }
        Collections.sort(listProviders, new Comparator<MessageBodyWriterPair>(){

            @Override
            public int compare(MessageBodyWriterPair p1, MessageBodyWriterPair p2) {
                return dc.compare(p1.mbw, p2.mbw);
            }
        });
    }

    private <T> void getClassCapability(Map<MediaType, List<T>> capabilities, T provider, MediaType mediaType) {
        if (!capabilities.containsKey(mediaType)) {
            capabilities.put(mediaType, new ArrayList());
        }
        List<T> providers = capabilities.get(mediaType);
        providers.add(provider);
    }

    @Override
    public Map<MediaType, List<MessageBodyReader>> getReaders(MediaType mediaType) {
        KeyComparatorLinkedHashMap<MediaType, List<MessageBodyReader>> subSet = new KeyComparatorLinkedHashMap<MediaType, List<MessageBodyReader>>(MEDIA_TYPE_COMPARATOR);
        if (!this.customReaderProviders.isEmpty()) {
            this.getCompatibleReadersWritersMap(mediaType, this.customReaderProviders, subSet);
        }
        this.getCompatibleReadersWritersMap(mediaType, this.readerProviders, subSet);
        return subSet;
    }

    @Override
    public Map<MediaType, List<MessageBodyWriter>> getWriters(MediaType mediaType) {
        KeyComparatorLinkedHashMap<MediaType, List<MessageBodyWriter>> subSet = new KeyComparatorLinkedHashMap<MediaType, List<MessageBodyWriter>>(MEDIA_TYPE_COMPARATOR);
        if (!this.customWriterProviders.isEmpty()) {
            this.getCompatibleReadersWritersMap(mediaType, this.customWriterProviders, subSet);
        }
        this.getCompatibleReadersWritersMap(mediaType, this.writerProviders, subSet);
        return subSet;
    }

    @Override
    public String readersToString(Map<MediaType, List<MessageBodyReader>> readers) {
        return this.toString(readers);
    }

    @Override
    public String writersToString(Map<MediaType, List<MessageBodyWriter>> writers) {
        return this.toString(writers);
    }

    private <T> String toString(Map<MediaType, List<T>> set) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        for (Map.Entry<MediaType, List<T>> e : set.entrySet()) {
            pw.append(e.getKey().toString()).println(" ->");
            for (T t : e.getValue()) {
                pw.append("  ").println(t.getClass().getName());
            }
        }
        pw.flush();
        return sw.toString();
    }

    @Override
    public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> c, Type t, Annotation[] as, MediaType mediaType) {
        MessageBodyReader<T> reader;
        if (!this.customReaderProviders.isEmpty() && (reader = this._getMessageBodyReader(c, t, as, mediaType, this.customReaderProviders)) != null) {
            return reader;
        }
        reader = this._getMessageBodyReader(c, t, as, mediaType, this.readerProviders);
        return reader;
    }

    private <T> MessageBodyReader<T> _getMessageBodyReader(Class<T> c, Type t, Annotation[] as, MediaType mediaType, Map<MediaType, List<MessageBodyReader>> providers) {
        MessageBodyReader<T> p = null;
        if (mediaType != null && (p = this._getMessageBodyReader(c, t, as, mediaType, mediaType, providers)) == null) {
            p = this._getMessageBodyReader(c, t, as, mediaType, MediaTypes.getTypeWildCart(mediaType), providers);
        }
        if (p == null) {
            p = this._getMessageBodyReader(c, t, as, mediaType, MediaTypes.GENERAL_MEDIA_TYPE, providers);
        }
        return p;
    }

    private <T> MessageBodyReader<T> _getMessageBodyReader(Class<T> c, Type t, Annotation[] as, MediaType mediaType, MediaType lookup) {
        MessageBodyReader<T> reader;
        if (!this.customReaderProviders.isEmpty() && (reader = this._getMessageBodyReader(c, t, as, mediaType, lookup, this.customReaderProviders)) != null) {
            return reader;
        }
        reader = this._getMessageBodyReader(c, t, as, mediaType, lookup, this.readerProviders);
        return reader;
    }

    private <T> MessageBodyReader<T> _getMessageBodyReader(Class<T> c, Type t, Annotation[] as, MediaType mediaType, MediaType lookup, Map<MediaType, List<MessageBodyReader>> providers) {
        List<MessageBodyReader> readers = providers.get(lookup);
        if (readers == null) {
            return null;
        }
        for (MessageBodyReader p : readers) {
            if (!p.isReadable(c, t, as, mediaType)) continue;
            return p;
        }
        return null;
    }

    @Override
    public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> c, Type t, Annotation[] as, MediaType mediaType) {
        MessageBodyWriter<T> p;
        if (!this.customWriterProviders.isEmpty() && (p = this._getMessageBodyWriter(c, t, as, mediaType, this.customWriterProviders)) != null) {
            return p;
        }
        p = this._getMessageBodyWriter(c, t, as, mediaType, this.writerProviders);
        return p;
    }

    private <T> MessageBodyWriter<T> _getMessageBodyWriter(Class<T> c, Type t, Annotation[] as, MediaType mediaType, Map<MediaType, List<MessageBodyWriter>> providers) {
        MessageBodyWriter<T> p = null;
        if (mediaType != null && (p = this._getMessageBodyWriter(c, t, as, mediaType, mediaType, providers)) == null) {
            p = this._getMessageBodyWriter(c, t, as, mediaType, MediaTypes.getTypeWildCart(mediaType), providers);
        }
        if (p == null) {
            p = this._getMessageBodyWriter(c, t, as, mediaType, MediaTypes.GENERAL_MEDIA_TYPE, providers);
        }
        return p;
    }

    private <T> MessageBodyWriter<T> _getMessageBodyWriter(Class<T> c, Type t, Annotation[] as, MediaType mediaType, MediaType lookup, Map<MediaType, List<MessageBodyWriter>> providers) {
        List<MessageBodyWriter> writers = providers.get(lookup);
        if (writers == null) {
            return null;
        }
        for (MessageBodyWriter p : writers) {
            if (!p.isWriteable(c, t, as, mediaType)) continue;
            return p;
        }
        return null;
    }

    private <T> void getCompatibleReadersWritersMap(MediaType mediaType, Map<MediaType, List<T>> set, Map<MediaType, List<T>> subSet) {
        if (mediaType.isWildcardType()) {
            this.getCompatibleReadersWritersList(mediaType, set, subSet);
        } else if (mediaType.isWildcardSubtype()) {
            this.getCompatibleReadersWritersList(mediaType, set, subSet);
            this.getCompatibleReadersWritersList(MediaTypes.GENERAL_MEDIA_TYPE, set, subSet);
        } else {
            this.getCompatibleReadersWritersList(mediaType, set, subSet);
            this.getCompatibleReadersWritersList(MediaTypes.getTypeWildCart(mediaType), set, subSet);
            this.getCompatibleReadersWritersList(MediaTypes.GENERAL_MEDIA_TYPE, set, subSet);
        }
    }

    private <T> void getCompatibleReadersWritersList(MediaType mediaType, Map<MediaType, List<T>> set, Map<MediaType, List<T>> subSet) {
        List<T> readers = set.get(mediaType);
        if (readers != null) {
            subSet.put(mediaType, Collections.unmodifiableList(readers));
        }
    }

    @Override
    public <T> List<MediaType> getMessageBodyWriterMediaTypes(Class<T> c, Type t, Annotation[] as) {
        ArrayList<MediaType> mtl = new ArrayList<MediaType>();
        for (MessageBodyWriterPair mbwp : this.customWriterListProviders) {
            if (!mbwp.mbw.isWriteable(c, t, as, MediaType.APPLICATION_OCTET_STREAM_TYPE)) continue;
            mtl.addAll(mbwp.types);
        }
        for (MessageBodyWriterPair mbwp : this.writerListProviders) {
            if (!mbwp.mbw.isWriteable(c, t, as, MediaType.APPLICATION_OCTET_STREAM_TYPE)) continue;
            mtl.addAll(mbwp.types);
        }
        Collections.sort(mtl, MediaTypes.MEDIA_TYPE_COMPARATOR);
        return mtl;
    }

    @Override
    public <T> MediaType getMessageBodyWriterMediaType(Class<T> c, Type t, Annotation[] as, List<MediaType> acceptableMediaTypes) {
        for (MediaType acceptable : acceptableMediaTypes) {
            for (MessageBodyWriterPair mbwp : this.customWriterListProviders) {
                for (MediaType mt : mbwp.types) {
                    if (!mt.isCompatible(acceptable) || !mbwp.mbw.isWriteable(c, t, as, acceptable)) continue;
                    return MediaTypes.mostSpecific(mt, acceptable);
                }
            }
            for (MessageBodyWriterPair mbwp : this.writerListProviders) {
                for (MediaType mt : mbwp.types) {
                    if (!mt.isCompatible(acceptable) || !mbwp.mbw.isWriteable(c, t, as, acceptable)) continue;
                    return MediaTypes.mostSpecific(mt, acceptable);
                }
            }
        }
        return null;
    }

    private static class DistanceComparator<T>
    implements Comparator<T> {
        private final Class<T> c;
        private final Map<Class, Integer> distanceMap = new HashMap<Class, Integer>();

        DistanceComparator(Class c) {
            this.c = c;
        }

        @Override
        public int compare(T o1, T o2) {
            int d1 = this.getDistance(o1);
            int d2 = this.getDistance(o2);
            return d2 - d1;
        }

        int getDistance(T t) {
            Integer d = this.distanceMap.get(t.getClass());
            if (d != null) {
                return d;
            }
            d = 0;
            for (Class a = (as = ReflectionHelper.getParameterizedClassArguments(p = ReflectionHelper.getClass(t.getClass(), this.c))) != null ? as[0] : null; a != null && a != Object.class; a = a.getSuperclass()) {
                Integer n = d;
                Integer n2 = d = Integer.valueOf(d + 1);
            }
            this.distanceMap.put(t.getClass(), d);
            return d;
        }
    }

    private static class MessageBodyWriterPair {
        final MessageBodyWriter mbw;
        final List<MediaType> types;

        MessageBodyWriterPair(MessageBodyWriter mbw, List<MediaType> types) {
            this.mbw = mbw;
            this.types = types;
        }
    }
}

