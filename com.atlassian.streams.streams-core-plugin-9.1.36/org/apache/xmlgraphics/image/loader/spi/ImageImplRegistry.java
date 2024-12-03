/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.spi.ImageConverter;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;
import org.apache.xmlgraphics.image.loader.spi.ImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.spi.ImagePreloader;
import org.apache.xmlgraphics.image.loader.util.Penalty;
import org.apache.xmlgraphics.util.Service;

public class ImageImplRegistry {
    protected static final Log log = LogFactory.getLog(ImageImplRegistry.class);
    public static final int INFINITE_PENALTY = Integer.MAX_VALUE;
    private List preloaders = new ArrayList();
    private int lastPreloaderIdentifier;
    private int lastPreloaderSort;
    private Map loaders = new HashMap();
    private List converters = new ArrayList();
    private int converterModifications;
    private Map additionalPenalties = new HashMap();
    private static ImageImplRegistry defaultInstance = new ImageImplRegistry();

    public ImageImplRegistry(boolean discover) {
        if (discover) {
            this.discoverClasspathImplementations();
        }
    }

    public ImageImplRegistry() {
        this(true);
    }

    public static ImageImplRegistry getDefaultInstance() {
        return defaultInstance;
    }

    public void discoverClasspathImplementations() {
        Iterator<Object> iter = Service.providers(ImagePreloader.class);
        while (iter.hasNext()) {
            this.registerPreloader((ImagePreloader)iter.next());
        }
        iter = Service.providers(ImageLoaderFactory.class);
        while (iter.hasNext()) {
            this.registerLoaderFactory((ImageLoaderFactory)iter.next());
        }
        iter = Service.providers(ImageConverter.class);
        while (iter.hasNext()) {
            this.registerConverter((ImageConverter)iter.next());
        }
    }

    public void registerPreloader(ImagePreloader preloader) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Registered " + preloader.getClass().getName() + " with priority " + preloader.getPriority()));
        }
        this.preloaders.add(this.newPreloaderHolder(preloader));
    }

    private synchronized PreloaderHolder newPreloaderHolder(ImagePreloader preloader) {
        PreloaderHolder holder = new PreloaderHolder();
        holder.preloader = preloader;
        holder.identifier = ++this.lastPreloaderIdentifier;
        return holder;
    }

    private synchronized void sortPreloaders() {
        if (this.lastPreloaderIdentifier != this.lastPreloaderSort) {
            Collections.sort(this.preloaders, new Comparator(){

                public int compare(Object o1, Object o2) {
                    PreloaderHolder h1 = (PreloaderHolder)o1;
                    long p1 = h1.preloader.getPriority();
                    PreloaderHolder h2 = (PreloaderHolder)o2;
                    int p2 = h2.preloader.getPriority();
                    int diff = Penalty.truncate((p1 += (long)ImageImplRegistry.this.getAdditionalPenalty(h1.preloader.getClass().getName()).getValue()) - (long)(p2 += ImageImplRegistry.this.getAdditionalPenalty(h2.preloader.getClass().getName()).getValue()));
                    if (diff != 0) {
                        return diff;
                    }
                    diff = h1.identifier - h2.identifier;
                    return diff;
                }
            });
            this.lastPreloaderSort = this.lastPreloaderIdentifier;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerLoaderFactory(ImageLoaderFactory loaderFactory) {
        String[] mimes;
        if (!loaderFactory.isAvailable()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("ImageLoaderFactory reports not available: " + loaderFactory.getClass().getName()));
            }
            return;
        }
        for (String mime : mimes = loaderFactory.getSupportedMIMETypes()) {
            Map map = this.loaders;
            synchronized (map) {
                ImageFlavor[] flavors;
                HashMap<ImageFlavor, ArrayList<ImageLoaderFactory>> flavorMap = (HashMap<ImageFlavor, ArrayList<ImageLoaderFactory>>)this.loaders.get(mime);
                if (flavorMap == null) {
                    flavorMap = new HashMap<ImageFlavor, ArrayList<ImageLoaderFactory>>();
                    this.loaders.put(mime, flavorMap);
                }
                for (ImageFlavor flavor : flavors = loaderFactory.getSupportedFlavors(mime)) {
                    ArrayList<ImageLoaderFactory> factoryList = (ArrayList<ImageLoaderFactory>)flavorMap.get(flavor);
                    if (factoryList == null) {
                        factoryList = new ArrayList<ImageLoaderFactory>();
                        flavorMap.put(flavor, factoryList);
                    }
                    factoryList.add(loaderFactory);
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("Registered " + loaderFactory.getClass().getName() + ": MIME = " + mime + ", Flavor = " + flavor));
                }
            }
        }
    }

    public Collection getImageConverters() {
        return Collections.unmodifiableList(this.converters);
    }

    public int getImageConverterModifications() {
        return this.converterModifications;
    }

    public void registerConverter(ImageConverter converter) {
        this.converters.add(converter);
        ++this.converterModifications;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Registered: " + converter.getClass().getName()));
        }
    }

    public Iterator getPreloaderIterator() {
        this.sortPreloaders();
        Iterator iter = this.preloaders.iterator();
        MyIterator i = new MyIterator();
        i.iter = iter;
        return i;
    }

    public ImageLoaderFactory getImageLoaderFactory(ImageInfo imageInfo, ImageFlavor flavor) {
        List factoryList;
        String mime = imageInfo.getMimeType();
        Map flavorMap = (Map)this.loaders.get(mime);
        if (flavorMap != null && (factoryList = (List)flavorMap.get(flavor)) != null && factoryList.size() > 0) {
            Iterator iter = factoryList.iterator();
            int bestPenalty = Integer.MAX_VALUE;
            ImageLoaderFactory bestFactory = null;
            while (iter.hasNext()) {
                ImageLoader loader;
                int penalty;
                ImageLoaderFactory factory = (ImageLoaderFactory)iter.next();
                if (!factory.isSupported(imageInfo) || (penalty = (loader = factory.newImageLoader(flavor)).getUsagePenalty()) >= bestPenalty) continue;
                bestPenalty = penalty;
                bestFactory = factory;
            }
            return bestFactory;
        }
        return null;
    }

    public ImageLoaderFactory[] getImageLoaderFactories(ImageInfo imageInfo, ImageFlavor flavor) {
        String mime = imageInfo.getMimeType();
        TreeSet<ImageLoaderFactory> matches = new TreeSet<ImageLoaderFactory>(new ImageLoaderFactoryComparator(flavor));
        imageInfo.getCustomObjects().put("additionalPenalties", this.additionalPenalties);
        Map flavorMap = (Map)this.loaders.get(mime);
        if (flavorMap != null) {
            for (Map.Entry i : flavorMap.entrySet()) {
                List factoryList;
                Map.Entry e = i;
                ImageFlavor checkFlavor = (ImageFlavor)e.getKey();
                if (!checkFlavor.isCompatible(flavor) || (factoryList = (List)e.getValue()) == null || factoryList.size() <= 0) continue;
                for (Object aFactoryList : factoryList) {
                    ImageLoaderFactory factory = (ImageLoaderFactory)aFactoryList;
                    if (!factory.isSupported(imageInfo)) continue;
                    matches.add(factory);
                }
            }
        }
        if (matches.size() == 0) {
            return null;
        }
        return matches.toArray(new ImageLoaderFactory[matches.size()]);
    }

    public ImageLoaderFactory[] getImageLoaderFactories(String mime) {
        Map flavorMap = (Map)this.loaders.get(mime);
        if (flavorMap != null) {
            HashSet factories = new HashSet();
            for (Object o : flavorMap.values()) {
                List factoryList = (List)o;
                factories.addAll(factoryList);
            }
            int factoryCount = factories.size();
            if (factoryCount > 0) {
                return factories.toArray(new ImageLoaderFactory[factoryCount]);
            }
        }
        return new ImageLoaderFactory[0];
    }

    public void setAdditionalPenalty(String className, Penalty penalty) {
        if (penalty != null) {
            this.additionalPenalties.put(className, penalty);
        } else {
            this.additionalPenalties.remove(className);
        }
        this.lastPreloaderSort = -1;
    }

    public Penalty getAdditionalPenalty(String className) {
        Penalty p = (Penalty)this.additionalPenalties.get(className);
        return p != null ? p : Penalty.ZERO_PENALTY;
    }

    private class ImageLoaderFactoryComparator
    implements Comparator {
        private ImageFlavor targetFlavor;

        public ImageLoaderFactoryComparator(ImageFlavor targetFlavor) {
            this.targetFlavor = targetFlavor;
        }

        public int compare(Object o1, Object o2) {
            ImageLoaderFactory f1 = (ImageLoaderFactory)o1;
            ImageLoader l1 = f1.newImageLoader(this.targetFlavor);
            long p1 = l1.getUsagePenalty();
            ImageLoaderFactory f2 = (ImageLoaderFactory)o2;
            ImageLoader l2 = f2.newImageLoader(this.targetFlavor);
            long p2 = ImageImplRegistry.this.getAdditionalPenalty(l2.getClass().getName()).getValue();
            return Penalty.truncate((p1 += (long)ImageImplRegistry.this.getAdditionalPenalty(l1.getClass().getName()).getValue()) - p2);
        }
    }

    static class MyIterator
    implements Iterator {
        Iterator iter;

        MyIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        public Object next() {
            Object obj = this.iter.next();
            if (obj != null) {
                return ((PreloaderHolder)obj).preloader;
            }
            return null;
        }

        @Override
        public void remove() {
            this.iter.remove();
        }
    }

    private static class PreloaderHolder {
        private ImagePreloader preloader;
        private int identifier;

        private PreloaderHolder() {
        }

        public String toString() {
            return this.preloader + " " + this.identifier;
        }
    }
}

