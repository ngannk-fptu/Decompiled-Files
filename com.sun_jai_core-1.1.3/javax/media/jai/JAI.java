/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImagingListenerImpl;
import com.sun.media.jai.util.PropertyUtil;
import com.sun.media.jai.util.SunTileCache;
import com.sun.media.jai.util.SunTileScheduler;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.media.jai.CollectionOp;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.RecyclingTileFactory;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.TileCache;
import javax.media.jai.TileScheduler;
import javax.media.jai.util.ImagingListener;

public final class JAI {
    private static final int HINT_IMAGE_LAYOUT = 101;
    private static final int HINT_INTERPOLATION = 102;
    private static final int HINT_OPERATION_REGISTRY = 103;
    private static final int HINT_OPERATION_BOUND = 104;
    private static final int HINT_BORDER_EXTENDER = 105;
    private static final int HINT_TILE_CACHE = 106;
    private static final int HINT_TILE_SCHEDULER = 107;
    private static final int HINT_DEFAULT_COLOR_MODEL_ENABLED = 108;
    private static final int HINT_DEFAULT_COLOR_MODEL_METHOD = 109;
    private static final int HINT_TILE_CACHE_METRIC = 110;
    private static final int HINT_SERIALIZE_DEEP_COPY = 111;
    private static final int HINT_TILE_CODEC_FORMAT = 112;
    private static final int HINT_TILE_ENCODING_PARAM = 113;
    private static final int HINT_TILE_DECODING_PARAM = 114;
    private static final int HINT_RETRY_INTERVAL = 115;
    private static final int HINT_NUM_RETRIES = 116;
    private static final int HINT_NEGOTIATION_PREFERENCES = 117;
    private static final int HINT_DEFAULT_RENDERING_SIZE = 118;
    private static final int HINT_COLOR_MODEL_FACTORY = 119;
    private static final int HINT_REPLACE_INDEX_COLOR_MODEL = 120;
    private static final int HINT_TILE_FACTORY = 121;
    private static final int HINT_TILE_RECYCLER = 122;
    private static final int HINT_CACHED_TILE_RECYCLING_ENABLED = 123;
    private static final int HINT_TRANSFORM_ON_COLORMAP = 124;
    private static final int HINT_IMAGING_LISTENER = 125;
    public static RenderingHints.Key KEY_IMAGE_LAYOUT = new RenderingKey(101, class$javax$media$jai$ImageLayout == null ? (class$javax$media$jai$ImageLayout = JAI.class$("javax.media.jai.ImageLayout")) : class$javax$media$jai$ImageLayout);
    public static RenderingHints.Key KEY_INTERPOLATION = new RenderingKey(102, class$javax$media$jai$Interpolation == null ? (class$javax$media$jai$Interpolation = JAI.class$("javax.media.jai.Interpolation")) : class$javax$media$jai$Interpolation);
    public static RenderingHints.Key KEY_OPERATION_REGISTRY = new RenderingKey(103, class$javax$media$jai$OperationRegistry == null ? (class$javax$media$jai$OperationRegistry = JAI.class$("javax.media.jai.OperationRegistry")) : class$javax$media$jai$OperationRegistry);
    public static RenderingHints.Key KEY_OPERATION_BOUND = new RenderingKey(104, class$java$lang$Integer == null ? (class$java$lang$Integer = JAI.class$("java.lang.Integer")) : class$java$lang$Integer);
    public static RenderingHints.Key KEY_BORDER_EXTENDER = new RenderingKey(105, class$javax$media$jai$BorderExtender == null ? (class$javax$media$jai$BorderExtender = JAI.class$("javax.media.jai.BorderExtender")) : class$javax$media$jai$BorderExtender);
    public static RenderingHints.Key KEY_TILE_CACHE = new RenderingKey(106, class$javax$media$jai$TileCache == null ? (class$javax$media$jai$TileCache = JAI.class$("javax.media.jai.TileCache")) : class$javax$media$jai$TileCache);
    public static RenderingHints.Key KEY_TILE_CACHE_METRIC = new RenderingKey(110, class$java$lang$Object == null ? (class$java$lang$Object = JAI.class$("java.lang.Object")) : class$java$lang$Object);
    public static RenderingHints.Key KEY_TILE_SCHEDULER = new RenderingKey(107, class$javax$media$jai$TileScheduler == null ? (class$javax$media$jai$TileScheduler = JAI.class$("javax.media.jai.TileScheduler")) : class$javax$media$jai$TileScheduler);
    public static RenderingHints.Key KEY_DEFAULT_COLOR_MODEL_ENABLED = new RenderingKey(108, class$java$lang$Boolean == null ? (class$java$lang$Boolean = JAI.class$("java.lang.Boolean")) : class$java$lang$Boolean);
    public static RenderingHints.Key KEY_DEFAULT_COLOR_MODEL_METHOD = new RenderingKey(109, class$java$lang$reflect$Method == null ? (class$java$lang$reflect$Method = JAI.class$("java.lang.reflect.Method")) : class$java$lang$reflect$Method);
    public static final RenderingHints.Key KEY_DEFAULT_RENDERING_SIZE = new RenderingKey(118, class$java$awt$Dimension == null ? (class$java$awt$Dimension = JAI.class$("java.awt.Dimension")) : class$java$awt$Dimension);
    public static RenderingHints.Key KEY_COLOR_MODEL_FACTORY = new RenderingKey(119, class$javax$media$jai$ColorModelFactory == null ? (class$javax$media$jai$ColorModelFactory = JAI.class$("javax.media.jai.ColorModelFactory")) : class$javax$media$jai$ColorModelFactory);
    public static RenderingHints.Key KEY_REPLACE_INDEX_COLOR_MODEL = new RenderingKey(120, class$java$lang$Boolean == null ? (class$java$lang$Boolean = JAI.class$("java.lang.Boolean")) : class$java$lang$Boolean);
    public static RenderingHints.Key KEY_TILE_FACTORY = new RenderingKey(121, class$javax$media$jai$TileFactory == null ? (class$javax$media$jai$TileFactory = JAI.class$("javax.media.jai.TileFactory")) : class$javax$media$jai$TileFactory);
    public static RenderingHints.Key KEY_TILE_RECYCLER = new RenderingKey(122, class$javax$media$jai$TileRecycler == null ? (class$javax$media$jai$TileRecycler = JAI.class$("javax.media.jai.TileRecycler")) : class$javax$media$jai$TileRecycler);
    public static RenderingHints.Key KEY_CACHED_TILE_RECYCLING_ENABLED = new RenderingKey(123, class$java$lang$Boolean == null ? (class$java$lang$Boolean = JAI.class$("java.lang.Boolean")) : class$java$lang$Boolean);
    public static RenderingHints.Key KEY_SERIALIZE_DEEP_COPY = new RenderingKey(111, class$java$lang$Boolean == null ? (class$java$lang$Boolean = JAI.class$("java.lang.Boolean")) : class$java$lang$Boolean);
    public static RenderingHints.Key KEY_TILE_CODEC_FORMAT = new RenderingKey(112, class$java$lang$String == null ? (class$java$lang$String = JAI.class$("java.lang.String")) : class$java$lang$String);
    public static RenderingHints.Key KEY_TILE_ENCODING_PARAM = new RenderingKey(113, class$javax$media$jai$tilecodec$TileCodecParameterList == null ? (class$javax$media$jai$tilecodec$TileCodecParameterList = JAI.class$("javax.media.jai.tilecodec.TileCodecParameterList")) : class$javax$media$jai$tilecodec$TileCodecParameterList);
    public static RenderingHints.Key KEY_TILE_DECODING_PARAM = new RenderingKey(114, class$javax$media$jai$tilecodec$TileCodecParameterList == null ? (class$javax$media$jai$tilecodec$TileCodecParameterList = JAI.class$("javax.media.jai.tilecodec.TileCodecParameterList")) : class$javax$media$jai$tilecodec$TileCodecParameterList);
    public static RenderingHints.Key KEY_RETRY_INTERVAL = new RenderingKey(115, class$java$lang$Integer == null ? (class$java$lang$Integer = JAI.class$("java.lang.Integer")) : class$java$lang$Integer);
    public static RenderingHints.Key KEY_NUM_RETRIES = new RenderingKey(116, class$java$lang$Integer == null ? (class$java$lang$Integer = JAI.class$("java.lang.Integer")) : class$java$lang$Integer);
    public static RenderingHints.Key KEY_NEGOTIATION_PREFERENCES = new RenderingKey(117, class$javax$media$jai$remote$NegotiableCapabilitySet == null ? (class$javax$media$jai$remote$NegotiableCapabilitySet = JAI.class$("javax.media.jai.remote.NegotiableCapabilitySet")) : class$javax$media$jai$remote$NegotiableCapabilitySet);
    public static RenderingHints.Key KEY_TRANSFORM_ON_COLORMAP = new RenderingKey(124, class$java$lang$Boolean == null ? (class$java$lang$Boolean = JAI.class$("java.lang.Boolean")) : class$java$lang$Boolean);
    public static RenderingHints.Key KEY_IMAGING_LISTENER = new RenderingKey(125, class$javax$media$jai$util$ImagingListener == null ? (class$javax$media$jai$util$ImagingListener = JAI.class$("javax.media.jai.util.ImagingListener")) : class$javax$media$jai$util$ImagingListener);
    private static final int DEFAULT_TILE_SIZE = 512;
    private static Dimension defaultTileSize = new Dimension(512, 512);
    private static Dimension defaultRenderingSize = new Dimension(0, 512);
    private OperationRegistry operationRegistry;
    private TileScheduler tileScheduler;
    private TileCache tileCache;
    private RenderingHints renderingHints;
    private ImagingListener imagingListener = ImagingListenerImpl.getInstance();
    private static JAI defaultInstance = new JAI(OperationRegistry.initializeRegistry(), new SunTileScheduler(), new SunTileCache(), new RenderingHints(null));
    static /* synthetic */ Class class$javax$media$jai$ImageLayout;
    static /* synthetic */ Class class$javax$media$jai$Interpolation;
    static /* synthetic */ Class class$javax$media$jai$OperationRegistry;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$javax$media$jai$BorderExtender;
    static /* synthetic */ Class class$javax$media$jai$TileCache;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$javax$media$jai$TileScheduler;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$reflect$Method;
    static /* synthetic */ Class class$java$awt$Dimension;
    static /* synthetic */ Class class$javax$media$jai$ColorModelFactory;
    static /* synthetic */ Class class$javax$media$jai$TileFactory;
    static /* synthetic */ Class class$javax$media$jai$TileRecycler;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$javax$media$jai$tilecodec$TileCodecParameterList;
    static /* synthetic */ Class class$javax$media$jai$remote$NegotiableCapabilitySet;
    static /* synthetic */ Class class$javax$media$jai$util$ImagingListener;
    static /* synthetic */ Class class$javax$media$jai$JAI;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;
    static /* synthetic */ Class class$javax$media$jai$CollectionImage;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderableImage;

    private JAI(OperationRegistry operationRegistry, TileScheduler tileScheduler, TileCache tileCache, RenderingHints renderingHints) {
        this.operationRegistry = operationRegistry;
        this.tileScheduler = tileScheduler;
        this.tileCache = tileCache;
        this.renderingHints = renderingHints;
        this.renderingHints.put(KEY_OPERATION_REGISTRY, operationRegistry);
        this.renderingHints.put(KEY_TILE_CACHE, tileCache);
        this.renderingHints.put(KEY_TILE_SCHEDULER, tileScheduler);
        RecyclingTileFactory rtf = new RecyclingTileFactory();
        this.renderingHints.put(KEY_TILE_FACTORY, rtf);
        this.renderingHints.put(KEY_TILE_RECYCLER, rtf);
        this.renderingHints.put(KEY_CACHED_TILE_RECYCLING_ENABLED, Boolean.FALSE);
        this.renderingHints.put(KEY_IMAGING_LISTENER, this.imagingListener);
    }

    public static final String getBuildVersion() {
        try {
            String str;
            InputStream is = (class$javax$media$jai$JAI == null ? (class$javax$media$jai$JAI = JAI.class$("javax.media.jai.JAI")) : class$javax$media$jai$JAI).getResourceAsStream("buildVersion");
            if (is == null) {
                is = PropertyUtil.getFileFromClasspath("javax/media/jai/buildVersion");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringWriter sw = new StringWriter();
            BufferedWriter writer = new BufferedWriter(sw);
            boolean append = false;
            while ((str = reader.readLine()) != null) {
                if (append) {
                    writer.newLine();
                }
                writer.write(str);
                append = true;
            }
            writer.close();
            return sw.getBuffer().toString();
        }
        catch (Exception e) {
            return JaiI18N.getString("JAI13");
        }
    }

    public static final void disableDefaultTileCache() {
        TileCache tmp = defaultInstance.getTileCache();
        if (tmp != null) {
            tmp.flush();
        }
        JAI.defaultInstance.renderingHints.remove(KEY_TILE_CACHE);
    }

    public static final void enableDefaultTileCache() {
        JAI.defaultInstance.renderingHints.put(KEY_TILE_CACHE, defaultInstance.getTileCache());
    }

    public static final void setDefaultTileSize(Dimension tileDimensions) {
        if (tileDimensions != null && (tileDimensions.width <= 0 || tileDimensions.height <= 0)) {
            throw new IllegalArgumentException();
        }
        defaultTileSize = tileDimensions != null ? (Dimension)tileDimensions.clone() : null;
    }

    public static final Dimension getDefaultTileSize() {
        return defaultTileSize != null ? (Dimension)defaultTileSize.clone() : null;
    }

    public static final void setDefaultRenderingSize(Dimension defaultSize) {
        if (defaultSize != null && defaultSize.width <= 0 && defaultSize.height <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI8"));
        }
        defaultRenderingSize = defaultSize == null ? null : new Dimension(defaultSize);
    }

    public static final Dimension getDefaultRenderingSize() {
        return defaultRenderingSize == null ? null : new Dimension(defaultRenderingSize);
    }

    public static JAI getDefaultInstance() {
        return defaultInstance;
    }

    static RenderingHints mergeRenderingHints(RenderingHints defaultHints, RenderingHints hints) {
        RenderingHints mergedHints;
        if (hints == null || hints.isEmpty()) {
            mergedHints = defaultHints;
        } else if (defaultHints == null || defaultHints.isEmpty()) {
            mergedHints = hints;
        } else {
            mergedHints = new RenderingHints(defaultHints);
            mergedHints.add(hints);
        }
        return mergedHints;
    }

    public JAI() {
        this.operationRegistry = JAI.defaultInstance.operationRegistry;
        this.tileScheduler = JAI.defaultInstance.tileScheduler;
        this.tileCache = JAI.defaultInstance.tileCache;
        this.renderingHints = (RenderingHints)JAI.defaultInstance.renderingHints.clone();
    }

    public OperationRegistry getOperationRegistry() {
        return this.operationRegistry;
    }

    public void setOperationRegistry(OperationRegistry operationRegistry) {
        if (operationRegistry == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.operationRegistry = operationRegistry;
        this.renderingHints.put(KEY_OPERATION_REGISTRY, operationRegistry);
    }

    public TileScheduler getTileScheduler() {
        return this.tileScheduler;
    }

    public void setTileScheduler(TileScheduler tileScheduler) {
        if (tileScheduler == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.tileScheduler = tileScheduler;
        this.renderingHints.put(KEY_TILE_SCHEDULER, tileScheduler);
    }

    public TileCache getTileCache() {
        return this.tileCache;
    }

    public void setTileCache(TileCache tileCache) {
        if (tileCache == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.tileCache = tileCache;
        this.renderingHints.put(KEY_TILE_CACHE, tileCache);
    }

    public static TileCache createTileCache(int tileCapacity, long memCapacity) {
        if (memCapacity < 0L) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI10"));
        }
        return new SunTileCache(memCapacity);
    }

    public static TileCache createTileCache(long memCapacity) {
        if (memCapacity < 0L) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI10"));
        }
        return new SunTileCache(memCapacity);
    }

    public static TileCache createTileCache() {
        return new SunTileCache();
    }

    public static TileScheduler createTileScheduler() {
        return new SunTileScheduler();
    }

    public static RenderedOp create(String opName, ParameterBlock args, RenderingHints hints) {
        return defaultInstance.createNS(opName, args, hints);
    }

    public RenderedOp createNS(String opName, ParameterBlock args, RenderingHints hints) {
        if (opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI14"));
        }
        if (args == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI15"));
        }
        String modeName = "rendered";
        OperationDescriptor odesc = (OperationDescriptor)this.operationRegistry.getDescriptor(modeName, opName);
        if (odesc == null) {
            throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI0"));
        }
        if (!(class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = JAI.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage).isAssignableFrom(odesc.getDestClass(modeName))) {
            throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI2"));
        }
        StringBuffer msg = new StringBuffer();
        if (!odesc.validateArguments(modeName, args = (ParameterBlock)args.clone(), msg)) {
            throw new IllegalArgumentException(msg.toString());
        }
        RenderingHints mergedHints = JAI.mergeRenderingHints(this.renderingHints, hints);
        RenderedOp op = new RenderedOp(this.operationRegistry, opName, args, mergedHints);
        if (odesc.isImmediate()) {
            PlanarImage im = null;
            im = op.getRendering();
            if (im == null) {
                return null;
            }
        }
        return op;
    }

    public static Collection createCollection(String opName, ParameterBlock args, RenderingHints hints) {
        return defaultInstance.createCollectionNS(opName, args, hints);
    }

    public Collection createCollectionNS(String opName, ParameterBlock args, RenderingHints hints) {
        Class destClass;
        if (opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI14"));
        }
        if (args == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI15"));
        }
        String modeName = "collection";
        OperationDescriptor odesc = (OperationDescriptor)this.operationRegistry.getDescriptor(modeName, opName);
        if (odesc == null) {
            throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI0"));
        }
        if (!(class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = JAI.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage).isAssignableFrom(destClass = odesc.getDestClass(modeName)) && !(class$javax$media$jai$CollectionImage == null ? (class$javax$media$jai$CollectionImage = JAI.class$("javax.media.jai.CollectionImage")) : class$javax$media$jai$CollectionImage).isAssignableFrom(destClass)) {
            throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI5"));
        }
        RenderingHints mergedHints = JAI.mergeRenderingHints(this.renderingHints, hints);
        StringBuffer msg = new StringBuffer();
        if (odesc.validateArguments(modeName, args = (ParameterBlock)args.clone(), msg)) {
            if ((class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = JAI.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage).isAssignableFrom(destClass)) {
                Vector<RenderedOp> v = new Vector<RenderedOp>(1);
                v.add(new RenderedOp(this.operationRegistry, opName, args, mergedHints));
                return v;
            }
            CollectionOp cOp = new CollectionOp(this.operationRegistry, opName, args, mergedHints);
            if (odesc.isImmediate()) {
                Collection coll = null;
                coll = cOp.getCollection();
                if (coll == null) {
                    return null;
                }
            }
            return cOp;
        }
        int numSources = odesc.getNumSources();
        Vector<Object> sources = args.getSources();
        Iterator[] iters = new Iterator[numSources];
        Iterator iter = null;
        int size = Integer.MAX_VALUE;
        for (int i = 0; i < numSources; ++i) {
            Object s = sources.elementAt(i);
            if (!(s instanceof Collection)) continue;
            iters[i] = ((Collection)s).iterator();
            if (iter != null && ((Collection)s).size() >= size) continue;
            iter = iters[i];
            size = ((Collection)s).size();
        }
        if (iter == null) {
            throw new IllegalArgumentException(msg.toString());
        }
        Collection<Collection> col = null;
        for (int i = 0; i < numSources; ++i) {
            Object s = sources.elementAt(i);
            if (!(s instanceof Collection)) continue;
            try {
                col = (Collection)s.getClass().newInstance();
                break;
            }
            catch (Exception e) {
                this.sendExceptionToListener(JaiI18N.getString("JAI16") + s.getClass().getName(), e);
            }
        }
        if (col == null) {
            col = new Vector();
        }
        Class[] sourceClasses = odesc.getSourceClasses(modeName);
        while (iter.hasNext()) {
            ParameterBlock pb = new ParameterBlock();
            pb.setParameters(args.getParameters());
            for (int i = 0; i < numSources; ++i) {
                Object nextSource = null;
                nextSource = iters[i] == null ? sources.elementAt(i) : iters[i].next();
                if (!sourceClasses[i].isAssignableFrom(nextSource.getClass()) && !(nextSource instanceof Collection)) {
                    throw new IllegalArgumentException(msg.toString());
                }
                pb.addSource(nextSource);
            }
            Collection c = this.createCollectionNS(opName, pb, mergedHints);
            if (c instanceof Vector && c.size() == 1 && ((Vector)c).elementAt(0) instanceof RenderedOp) {
                col.add((Collection)((Vector)c).elementAt(0));
                continue;
            }
            col.add(c);
        }
        return col;
    }

    public static RenderedOp create(String opName, ParameterBlock args) {
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, Object param) {
        ParameterBlock args = new ParameterBlock();
        args.add(param);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, Object param1, Object param2) {
        ParameterBlock args = new ParameterBlock();
        args.add(param1);
        args.add(param2);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, Object param1, int param2) {
        ParameterBlock args = new ParameterBlock();
        args.add(param1);
        args.add(param2);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, Object param1, Object param2, Object param3) {
        ParameterBlock args = new ParameterBlock();
        args.add(param1);
        args.add(param2);
        args.add(param3);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, int param1, int param2, Object param3) {
        ParameterBlock args = new ParameterBlock();
        args.add(param1);
        args.add(param2);
        args.add(param3);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, Object param1, Object param2, Object param3, Object param4) {
        ParameterBlock args = new ParameterBlock();
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, Object param1, int param2, Object param3, int param4) {
        ParameterBlock args = new ParameterBlock();
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, Collection srcCol) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(srcCol);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, Object param) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, int param) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, Object param1, float param2) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, Object param3) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, Object param1, int param2, int param3) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, float param1, float param2, Object param3) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, Object param3, Object param4) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, int param3, int param4) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, int param1, int param2, int param3, int param4) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, float param1, float param2, float param3, Object param4) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, Object param3, Object param4, Object param5) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        args.add(param5);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, float param1, float param2, float param3, float param4, Object param5) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        args.add(param5);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, float param1, int param2, float param3, float param4, Object param5) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        args.add(param5);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        args.add(param5);
        args.add(param6);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src, int param1, int param2, int param3, int param4, int param5, Object param6) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        args.add(param5);
        args.add(param6);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src1, RenderedImage src2) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src1);
        args.addSource(src2);
        return JAI.create(opName, args, null);
    }

    public static RenderedOp create(String opName, RenderedImage src1, RenderedImage src2, Object param1, Object param2, Object param3, Object param4) {
        ParameterBlock args = new ParameterBlock();
        args.addSource(src1);
        args.addSource(src2);
        args.add(param1);
        args.add(param2);
        args.add(param3);
        args.add(param4);
        return JAI.create(opName, args, null);
    }

    public static Collection createCollection(String opName, ParameterBlock args) {
        return JAI.createCollection(opName, args, null);
    }

    public static RenderableOp createRenderable(String opName, ParameterBlock args, RenderingHints hints) {
        return defaultInstance.createRenderableNS(opName, args, hints);
    }

    public static RenderableOp createRenderable(String opName, ParameterBlock args) {
        return defaultInstance.createRenderableNS(opName, args, null);
    }

    public RenderableOp createRenderableNS(String opName, ParameterBlock args, RenderingHints hints) {
        if (opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI14"));
        }
        if (args == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI15"));
        }
        String modeName = "renderable";
        OperationDescriptor odesc = (OperationDescriptor)this.operationRegistry.getDescriptor(modeName, opName);
        if (odesc == null) {
            throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI0"));
        }
        if (!(class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = JAI.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage).isAssignableFrom(odesc.getDestClass(modeName))) {
            throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI4"));
        }
        StringBuffer msg = new StringBuffer();
        if (!odesc.validateArguments(modeName, args = (ParameterBlock)args.clone(), msg)) {
            throw new IllegalArgumentException(msg.toString());
        }
        RenderableOp op = new RenderableOp(this.operationRegistry, opName, args, JAI.mergeRenderingHints(this.renderingHints, hints));
        return op;
    }

    public RenderableOp createRenderableNS(String opName, ParameterBlock args) {
        return this.createRenderableNS(opName, args, null);
    }

    public static Collection createRenderableCollection(String opName, ParameterBlock args, RenderingHints hints) {
        return defaultInstance.createRenderableCollectionNS(opName, args, hints);
    }

    public static Collection createRenderableCollection(String opName, ParameterBlock args) {
        return defaultInstance.createRenderableCollectionNS(opName, args, null);
    }

    public Collection createRenderableCollectionNS(String opName, ParameterBlock args) {
        return this.createRenderableCollectionNS(opName, args, null);
    }

    public Collection createRenderableCollectionNS(String opName, ParameterBlock args, RenderingHints hints) {
        Class destClass;
        if (opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI14"));
        }
        if (args == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI15"));
        }
        String modeName = "renderableCollection";
        OperationDescriptor odesc = (OperationDescriptor)this.operationRegistry.getDescriptor(modeName, opName);
        if (odesc == null) {
            throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI0"));
        }
        if (!(class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = JAI.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage).isAssignableFrom(destClass = odesc.getDestClass(modeName)) && !(class$javax$media$jai$CollectionImage == null ? (class$javax$media$jai$CollectionImage = JAI.class$("javax.media.jai.CollectionImage")) : class$javax$media$jai$CollectionImage).isAssignableFrom(destClass)) {
            throw new IllegalArgumentException(opName + ": " + JaiI18N.getString("JAI6"));
        }
        StringBuffer msg = new StringBuffer();
        args = (ParameterBlock)args.clone();
        RenderingHints mergedHints = JAI.mergeRenderingHints(this.renderingHints, hints);
        if (odesc.validateArguments(modeName, args, msg)) {
            if ((class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = JAI.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage).isAssignableFrom(destClass)) {
                Vector<RenderableOp> v = new Vector<RenderableOp>(1);
                RenderableOp op = new RenderableOp(this.operationRegistry, opName, args, mergedHints);
                v.add(op);
                return v;
            }
            CollectionOp cOp = new CollectionOp(this.operationRegistry, opName, args, mergedHints, true);
            if (odesc.isImmediate()) {
                Collection coll = null;
                coll = cOp.getCollection();
                if (coll == null) {
                    return null;
                }
            }
            return cOp;
        }
        int numSources = odesc.getNumSources();
        Vector<Object> sources = args.getSources();
        Iterator[] iters = new Iterator[numSources];
        Iterator iter = null;
        int size = Integer.MAX_VALUE;
        for (int i = 0; i < numSources; ++i) {
            Object s = sources.elementAt(i);
            if (!(s instanceof Collection)) continue;
            iters[i] = ((Collection)s).iterator();
            if (iter != null && ((Collection)s).size() >= size) continue;
            iter = iters[i];
            size = ((Collection)s).size();
        }
        if (iter == null) {
            throw new IllegalArgumentException(msg.toString());
        }
        Collection<Collection> col = null;
        for (int i = 0; i < numSources; ++i) {
            Object s = sources.elementAt(i);
            if (!(s instanceof Collection)) continue;
            try {
                col = (Collection)s.getClass().newInstance();
                break;
            }
            catch (Exception e) {
                this.sendExceptionToListener(JaiI18N.getString("JAI16") + s.getClass().getName(), e);
            }
        }
        if (col == null) {
            col = new Vector();
        }
        Class[] sourceClasses = odesc.getSourceClasses(modeName);
        while (iter.hasNext()) {
            ParameterBlock pb = new ParameterBlock();
            pb.setParameters(args.getParameters());
            for (int i = 0; i < numSources; ++i) {
                Object nextSource = null;
                nextSource = iters[i] == null ? sources.elementAt(i) : iters[i].next();
                if (!sourceClasses[i].isAssignableFrom(nextSource.getClass()) && !(nextSource instanceof Collection)) {
                    throw new IllegalArgumentException(msg.toString());
                }
                pb.addSource(nextSource);
            }
            Collection c = this.createRenderableCollectionNS(opName, pb, mergedHints);
            if (c instanceof Vector && c.size() == 1 && ((Vector)c).elementAt(0) instanceof RenderableOp) {
                col.add((Collection)((Vector)c).elementAt(0));
                continue;
            }
            col.add(c);
        }
        return col;
    }

    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    public void setRenderingHints(RenderingHints hints) {
        if (hints == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.renderingHints = hints;
    }

    public void clearRenderingHints() {
        this.renderingHints = new RenderingHints(null);
    }

    public Object getRenderingHint(RenderingHints.Key key) {
        if (key == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI7"));
        }
        return this.renderingHints.get(key);
    }

    public void setRenderingHint(RenderingHints.Key key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI7"));
        }
        if (value == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAI9"));
        }
        try {
            this.renderingHints.put(key, value);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    public void removeRenderingHint(RenderingHints.Key key) {
        this.renderingHints.remove(key);
    }

    public void setImagingListener(ImagingListener listener) {
        if (listener == null) {
            listener = ImagingListenerImpl.getInstance();
        }
        this.renderingHints.put(KEY_IMAGING_LISTENER, listener);
        this.imagingListener = listener;
    }

    public ImagingListener getImagingListener() {
        return this.imagingListener;
    }

    private void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = this.getImagingListener();
        listener.errorOccurred(message, e, this, false);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static class RenderingKey
    extends RenderingHints.Key {
        private static Class JAIclass = class$javax$media$jai$JAI == null ? (class$javax$media$jai$JAI = JAI.class$("javax.media.jai.JAI")) : class$javax$media$jai$JAI;
        private Class objectClass;

        RenderingKey(int privateKey, Class objectClass) {
            super(privateKey);
            this.objectClass = objectClass;
        }

        public boolean isCompatibleValue(Object val) {
            return this.objectClass.isInstance(val);
        }
    }
}

