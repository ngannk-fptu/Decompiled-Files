/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.CaselessStringArrayTable;
import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.Serializable;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationNode;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RegistryMode;
import javax.media.jai.util.Range;

public abstract class OperationDescriptorImpl
implements OperationDescriptor,
Serializable {
    private boolean deprecated = false;
    protected final String[][] resources;
    protected final String[] supportedModes;
    private CaselessStringArrayTable modeIndices;
    protected final String[] sourceNames;
    private Class[][] sourceClasses;
    private CaselessStringArrayTable sourceIndices;
    private ParameterListDescriptor[] paramListDescriptors;
    String[] paramNames;
    private String name = null;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderableImage;
    static /* synthetic */ Class class$java$util$Collection;

    private String[] checkSources(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses) {
        if (resources == null || resources.length == 0) {
            throw new IllegalArgumentException("resources: " + JaiI18N.getString("Generic2"));
        }
        if (supportedModes == null || supportedModes.length == 0) {
            throw new IllegalArgumentException("supportedModes: " + JaiI18N.getString("Generic2"));
        }
        int numModes = supportedModes.length;
        if (sourceClasses != null) {
            int numSources;
            if (sourceClasses.length != numModes) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[]{"sourceClasses", new Integer(numModes)}));
            }
            int n = numSources = sourceClasses[0] == null ? 0 : sourceClasses[0].length;
            if (sourceNames == null) {
                sourceNames = this.getDefaultSourceNames(numSources);
            } else if (sourceNames.length != numSources) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl1", new Object[]{new Integer(sourceNames.length), new Integer(numSources)}));
            }
            for (int i = 0; i < sourceClasses.length; ++i) {
                int ns;
                int n2 = ns = sourceClasses[i] == null ? 0 : sourceClasses[i].length;
                if (numSources == ns) continue;
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl2", new Object[]{new Integer(ns), new Integer(numSources), supportedModes[i]}));
            }
        } else if (sourceNames != null && sourceNames.length != 0) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl1", new Object[]{new Integer(sourceNames.length), new Integer(0)}));
        }
        return sourceNames;
    }

    public OperationDescriptorImpl(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses, String[] paramNames, Class[][] paramClasses, Object[][] paramDefaults, Object[][] validParamValues) {
        sourceNames = this.checkSources(resources, supportedModes, sourceNames, sourceClasses);
        this.resources = resources;
        this.supportedModes = supportedModes;
        this.sourceNames = sourceNames;
        this.sourceClasses = sourceClasses;
        this.paramNames = paramNames;
        this.modeIndices = new CaselessStringArrayTable(supportedModes);
        this.sourceIndices = new CaselessStringArrayTable(sourceNames);
        int numParams = paramNames == null ? 0 : paramNames.length;
        int numModes = supportedModes.length;
        if (numParams == 0 ? paramClasses != null && paramClasses.length != numModes : paramClasses == null || paramClasses.length != numModes) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[]{"paramClasses", new Integer(numModes)}));
        }
        if (paramDefaults != null && paramDefaults.length != numModes) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[]{"paramDefaults", new Integer(numModes)}));
        }
        if (validParamValues != null && validParamValues.length != numModes) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[]{"validParamValues", new Integer(numModes)}));
        }
        this.paramListDescriptors = new ParameterListDescriptor[numModes];
        for (int i = 0; i < numModes; ++i) {
            this.paramListDescriptors[i] = new ParameterListDescriptorImpl(this, paramNames, paramClasses[i], paramDefaults == null ? null : paramDefaults[i], validParamValues == null ? null : validParamValues[i]);
        }
    }

    public OperationDescriptorImpl(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses, String[] paramNames, Class[] paramClasses, Object[] paramDefaults, Object[] validParamValues) {
        sourceNames = this.checkSources(resources, supportedModes, sourceNames, sourceClasses);
        this.resources = resources;
        this.supportedModes = supportedModes;
        this.sourceNames = sourceNames;
        this.sourceClasses = sourceClasses;
        this.paramNames = paramNames;
        this.modeIndices = new CaselessStringArrayTable(supportedModes);
        this.sourceIndices = new CaselessStringArrayTable(sourceNames);
        ParameterListDescriptorImpl pld = new ParameterListDescriptorImpl(this, paramNames, paramClasses, paramDefaults, validParamValues);
        this.paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
        for (int i = 0; i < supportedModes.length; ++i) {
            this.paramListDescriptors[i] = pld;
        }
    }

    public OperationDescriptorImpl(String[][] resources, String[] supportedModes, int numSources, String[] paramNames, Class[] paramClasses, Object[] paramDefaults, Object[] validParamValues) {
        Class[][] sourceClasses = OperationDescriptorImpl.makeDefaultSourceClassList(supportedModes, numSources);
        String[] sourceNames = this.checkSources(resources, supportedModes, null, sourceClasses);
        this.resources = resources;
        this.supportedModes = supportedModes;
        this.sourceNames = sourceNames;
        this.sourceClasses = sourceClasses;
        this.paramNames = paramNames;
        this.modeIndices = new CaselessStringArrayTable(supportedModes);
        this.sourceIndices = new CaselessStringArrayTable(sourceNames);
        ParameterListDescriptorImpl pld = new ParameterListDescriptorImpl(this, paramNames, paramClasses, paramDefaults, validParamValues);
        this.paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
        for (int i = 0; i < supportedModes.length; ++i) {
            this.paramListDescriptors[i] = pld;
        }
    }

    public OperationDescriptorImpl(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses, ParameterListDescriptor[] pld) {
        sourceNames = this.checkSources(resources, supportedModes, sourceNames, sourceClasses);
        this.resources = resources;
        this.supportedModes = supportedModes;
        this.sourceNames = sourceNames;
        this.sourceClasses = sourceClasses;
        this.modeIndices = new CaselessStringArrayTable(supportedModes);
        this.sourceIndices = new CaselessStringArrayTable(sourceNames);
        if (pld != null && pld.length != supportedModes.length) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[]{"ParameterListDescriptor's", new Integer(supportedModes.length)}));
        }
        if (pld == null) {
            ParameterListDescriptorImpl tpld = new ParameterListDescriptorImpl();
            this.paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
            for (int i = 0; i < supportedModes.length; ++i) {
                this.paramListDescriptors[i] = tpld;
            }
            this.paramNames = null;
        } else {
            this.paramListDescriptors = pld;
            this.paramNames = this.paramListDescriptors[0].getParamNames();
        }
    }

    public OperationDescriptorImpl(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses, ParameterListDescriptor pld) {
        sourceNames = this.checkSources(resources, supportedModes, sourceNames, sourceClasses);
        this.resources = resources;
        this.supportedModes = supportedModes;
        this.sourceNames = sourceNames;
        this.sourceClasses = sourceClasses;
        this.modeIndices = new CaselessStringArrayTable(supportedModes);
        this.sourceIndices = new CaselessStringArrayTable(sourceNames);
        if (pld == null) {
            pld = new ParameterListDescriptorImpl();
        }
        this.paramNames = pld.getParamNames();
        this.paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
        for (int i = 0; i < supportedModes.length; ++i) {
            this.paramListDescriptors[i] = pld;
        }
    }

    private String[] getDefaultSourceNames(int numSources) {
        String[] defaultSourceNames = new String[numSources];
        for (int i = 0; i < numSources; ++i) {
            defaultSourceNames[i] = "source" + i;
        }
        return defaultSourceNames;
    }

    public String getName() {
        if (this.name == null) {
            this.name = (String)this.getResourceBundle(Locale.getDefault()).getObject("GlobalName");
        }
        return this.name;
    }

    public String[] getSupportedModes() {
        return this.supportedModes;
    }

    public boolean isModeSupported(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.modeIndices.contains(modeName);
    }

    public boolean arePropertiesSupported() {
        return true;
    }

    public PropertyGenerator[] getPropertyGenerators(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.deprecated && (modeName.equalsIgnoreCase("rendered") || modeName.equalsIgnoreCase("renderable"))) {
            return this.getPropertyGenerators();
        }
        if (!this.arePropertiesSupported()) {
            throw new UnsupportedOperationException(JaiI18N.formatMsg("OperationDescriptorImpl3", new Object[]{modeName}));
        }
        return null;
    }

    public ParameterListDescriptor getParameterListDescriptor(String modeName) {
        return this.paramListDescriptors[this.modeIndices.indexOf(modeName)];
    }

    public String[][] getResources(Locale locale) {
        return this.resources;
    }

    public ResourceBundle getResourceBundle(Locale locale) {
        final Locale l = locale;
        return new ListResourceBundle(){

            public Object[][] getContents() {
                return OperationDescriptorImpl.this.getResources(l);
            }
        };
    }

    public int getNumSources() {
        return this.sourceNames.length;
    }

    public Class[] getSourceClasses(String modeName) {
        this.checkModeName(modeName);
        Class[] sc = this.sourceClasses[this.modeIndices.indexOf(modeName)];
        if (sc != null && sc.length <= 0) {
            return null;
        }
        return sc;
    }

    public String[] getSourceNames() {
        if (this.sourceNames == null || this.sourceNames.length <= 0) {
            return null;
        }
        return this.sourceNames;
    }

    public Class getDestClass(String modeName) {
        this.checkModeName(modeName);
        if (this.deprecated) {
            if (modeName.equalsIgnoreCase("rendered")) {
                return this.getDestClass();
            }
            if (modeName.equalsIgnoreCase("renderable")) {
                return this.getRenderableDestClass();
            }
        }
        return RegistryMode.getMode(modeName).getProductClass();
    }

    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.deprecated) {
            if (modeName.equalsIgnoreCase("rendered")) {
                return this.validateSources(args, msg);
            }
            if (modeName.equalsIgnoreCase("renderable")) {
                return this.validateRenderableSources(args, msg);
            }
        }
        return this.validateSources(this.getSourceClasses(modeName), args, msg);
    }

    protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer msg) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.deprecated && (modeName.equalsIgnoreCase("rendered") || modeName.equalsIgnoreCase("renderable"))) {
            return this.validateParameters(args, msg);
        }
        return this.validateParameters(this.getParameterListDescriptor(modeName), args, msg);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        return this.isModeSupported(modeName) && this.validateSources(modeName, args, msg) && this.validateParameters(modeName, args, msg);
    }

    public boolean isImmediate() {
        return false;
    }

    public Object getInvalidRegion(String modeName, ParameterBlock oldParamBlock, RenderingHints oldHints, ParameterBlock newParamBlock, RenderingHints newHints, OperationNode node) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return null;
    }

    protected static Class getDefaultSourceClass(String modeName) {
        if ("rendered".equalsIgnoreCase(modeName)) {
            return class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = OperationDescriptorImpl.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage;
        }
        if ("renderable".equalsIgnoreCase(modeName)) {
            return class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = OperationDescriptorImpl.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage;
        }
        if ("collection".equalsIgnoreCase(modeName)) {
            return class$java$util$Collection == null ? (class$java$util$Collection = OperationDescriptorImpl.class$("java.util.Collection")) : class$java$util$Collection;
        }
        if ("renderableCollection".equalsIgnoreCase(modeName)) {
            return class$java$util$Collection == null ? (class$java$util$Collection = OperationDescriptorImpl.class$("java.util.Collection")) : class$java$util$Collection;
        }
        return null;
    }

    protected static Class[][] makeDefaultSourceClassList(String[] supportedModes, int numSources) {
        if (supportedModes == null || supportedModes.length == 0) {
            return null;
        }
        int count = supportedModes.length;
        Class[][] classes = new Class[count][numSources];
        for (int i = 0; i < count; ++i) {
            Class sourceClass = OperationDescriptorImpl.getDefaultSourceClass(supportedModes[i]);
            for (int j = 0; j < numSources; ++j) {
                classes[i][j] = sourceClass;
            }
        }
        return classes;
    }

    private String[] makeSupportedModeList() {
        int count = 0;
        if (this.isRenderedSupported()) {
            ++count;
        }
        if (this.isRenderableSupported()) {
            ++count;
        }
        String[] modes = new String[count];
        count = 0;
        if (this.isRenderedSupported()) {
            modes[count++] = "rendered";
        }
        if (this.isRenderableSupported()) {
            modes[count++] = "renderable";
        }
        return modes;
    }

    private Class[][] makeSourceClassList(Class[] sourceClasses, Class[] renderableSourceClasses) {
        int count = 0;
        if (this.isRenderedSupported()) {
            ++count;
        }
        if (this.isRenderableSupported()) {
            ++count;
        }
        Class[][] classes = new Class[count][];
        count = 0;
        if (this.isRenderedSupported()) {
            classes[count++] = sourceClasses;
        }
        if (this.isRenderableSupported()) {
            classes[count++] = renderableSourceClasses;
        }
        return classes;
    }

    private Object[] makeValidParamValueList(Class[] paramClasses) {
        if (paramClasses == null) {
            return null;
        }
        int numParams = paramClasses.length;
        Object[] validValues = null;
        for (int i = 0; i < numParams; ++i) {
            Number min = this.getParamMinValue(i);
            Number max = this.getParamMaxValue(i);
            if (min == null && max == null) continue;
            if (validValues == null) {
                validValues = new Object[numParams];
            }
            validValues[i] = new Range(min.getClass(), (Comparable)((Object)min), (Comparable)((Object)max));
        }
        return validValues;
    }

    public OperationDescriptorImpl(String[][] resources, Class[] sourceClasses, Class[] renderableSourceClasses, Class[] paramClasses, String[] paramNames, Object[] paramDefaults) {
        this.deprecated = true;
        String[] supportedModes = this.makeSupportedModeList();
        Class[][] sourceClassList = this.makeSourceClassList(sourceClasses, renderableSourceClasses);
        String[] sourceNames = this.checkSources(resources, supportedModes, null, sourceClassList);
        Object[] validParamValues = this.makeValidParamValueList(paramClasses);
        this.resources = resources;
        this.supportedModes = supportedModes;
        this.sourceNames = sourceNames;
        this.sourceClasses = sourceClassList;
        this.paramNames = paramNames;
        this.modeIndices = new CaselessStringArrayTable(supportedModes);
        this.sourceIndices = new CaselessStringArrayTable(sourceNames);
        ParameterListDescriptorImpl pld = new ParameterListDescriptorImpl(this, paramNames, paramClasses, paramDefaults, validParamValues);
        this.paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
        for (int i = 0; i < supportedModes.length; ++i) {
            this.paramListDescriptors[i] = pld;
        }
    }

    public OperationDescriptorImpl(String[][] resources, int numSources, Class[] paramClasses, String[] paramNames, Object[] paramDefaults) {
        this.deprecated = true;
        String[] supportedModes = this.makeSupportedModeList();
        Class[][] sourceClassList = OperationDescriptorImpl.makeDefaultSourceClassList(supportedModes, numSources);
        String[] sourceNames = this.checkSources(resources, supportedModes, null, sourceClassList);
        Object[] validParamValues = this.makeValidParamValueList(paramClasses);
        this.resources = resources;
        this.supportedModes = supportedModes;
        this.sourceNames = sourceNames;
        this.sourceClasses = sourceClassList;
        this.paramNames = paramNames;
        this.modeIndices = new CaselessStringArrayTable(supportedModes);
        this.sourceIndices = new CaselessStringArrayTable(sourceNames);
        ParameterListDescriptorImpl pld = new ParameterListDescriptorImpl(this, paramNames, paramClasses, paramDefaults, validParamValues);
        this.paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
        for (int i = 0; i < supportedModes.length; ++i) {
            this.paramListDescriptors[i] = pld;
        }
    }

    public OperationDescriptorImpl(String[][] resources, Class[] sourceClasses) {
        this(resources, sourceClasses, null, null, null, null);
    }

    public OperationDescriptorImpl(String[][] resources, Class[] sourceClasses, Class[] renderableSourceClasses) {
        this(resources, sourceClasses, renderableSourceClasses, null, null, null);
    }

    public OperationDescriptorImpl(String[][] resources, Class[] paramClasses, String[] paramNames, Object[] paramDefaults) {
        this(resources, null, null, paramClasses, paramNames, paramDefaults);
    }

    public OperationDescriptorImpl(String[][] resources, int numSources) {
        this(resources, numSources, null, null, null);
    }

    public PropertyGenerator[] getPropertyGenerators() {
        return this.deprecated ? null : this.getPropertyGenerators("rendered");
    }

    public boolean isRenderedSupported() {
        return this.deprecated ? true : this.isModeSupported("rendered");
    }

    public Class[] getSourceClasses() {
        return this.getSourceClasses("rendered");
    }

    public Class getDestClass() {
        if (this.deprecated) {
            Class clazz = this.isRenderedSupported() ? (class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = OperationDescriptorImpl.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage) : null;
            return clazz;
        }
        return this.getDestClass("rendered");
    }

    public boolean validateArguments(ParameterBlock args, StringBuffer msg) {
        if (this.deprecated) {
            return this.validateSources(args, msg) && this.validateParameters(args, msg);
        }
        return this.validateArguments("rendered", args, msg);
    }

    public boolean isRenderableSupported() {
        return this.deprecated ? false : this.isModeSupported("renderable");
    }

    public Class[] getRenderableSourceClasses() {
        return this.getSourceClasses("renderable");
    }

    public Class getRenderableDestClass() {
        if (this.deprecated) {
            Class clazz = this.isRenderableSupported() ? (class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = OperationDescriptorImpl.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage) : null;
            return clazz;
        }
        return this.getDestClass("renderable");
    }

    public boolean validateRenderableArguments(ParameterBlock args, StringBuffer msg) {
        if (this.deprecated) {
            return this.validateRenderableSources(args, msg) && this.validateParameters(args, msg);
        }
        return this.validateArguments("renderable", args, msg);
    }

    private ParameterListDescriptor getDefaultPLD() {
        return this.getParameterListDescriptor(this.getSupportedModes()[0]);
    }

    public int getNumParameters() {
        return this.getDefaultPLD().getNumParameters();
    }

    public Class[] getParamClasses() {
        return this.getDefaultPLD().getParamClasses();
    }

    public String[] getParamNames() {
        return this.getDefaultPLD().getParamNames();
    }

    public Object[] getParamDefaults() {
        return this.getDefaultPLD().getParamDefaults();
    }

    public Object getParamDefaultValue(int index) {
        return this.getDefaultPLD().getParamDefaultValue(this.paramNames[index]);
    }

    public Number getParamMinValue(int index) {
        return null;
    }

    public Number getParamMaxValue(int index) {
        return null;
    }

    protected boolean validateSources(ParameterBlock args, StringBuffer msg) {
        if (this.deprecated) {
            return this.isRenderedSupported() && this.validateSources(this.getSourceClasses(), args, msg);
        }
        return this.validateSources("rendered", args, msg);
    }

    protected boolean validateRenderableSources(ParameterBlock args, StringBuffer msg) {
        if (this.deprecated) {
            return this.isRenderableSupported() && this.validateSources(this.getRenderableSourceClasses(), args, msg);
        }
        return this.validateSources("renderable", args, msg);
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        return this.validateParameters(this.getDefaultPLD(), args, msg);
    }

    private int getMinNumParameters(ParameterListDescriptor pld) {
        int numParams = pld.getNumParameters();
        Object[] paramDefaults = pld.getParamDefaults();
        for (int i = numParams - 1; i >= 0 && paramDefaults[i] != ParameterListDescriptor.NO_PARAMETER_DEFAULT; --i) {
            --numParams;
        }
        return numParams;
    }

    private boolean validateSources(Class[] sources, ParameterBlock args, StringBuffer msg) {
        if (args == null || msg == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int numSources = this.getNumSources();
        if (args.getNumSources() < numSources) {
            msg.append(JaiI18N.formatMsg("OperationDescriptorImpl6", new Object[]{this.getName(), new Integer(numSources)}));
            return false;
        }
        for (int i = 0; i < numSources; ++i) {
            Object s = args.getSource(i);
            if (s == null) {
                msg.append(JaiI18N.formatMsg("OperationDescriptorImpl7", new Object[]{this.getName()}));
                return false;
            }
            Class c = sources[i];
            if (c.isInstance(s)) continue;
            msg.append(JaiI18N.formatMsg("OperationDescriptorImpl8", new Object[]{this.getName(), new Integer(i), new String(c.toString()), new String(s.getClass().toString())}));
            return false;
        }
        return true;
    }

    private boolean validateParameters(ParameterListDescriptor pld, ParameterBlock args, StringBuffer msg) {
        int i;
        if (args == null || msg == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int numParams = pld.getNumParameters();
        int argNumParams = args.getNumParameters();
        Object[] paramDefaults = pld.getParamDefaults();
        if (argNumParams < numParams) {
            if (argNumParams < this.getMinNumParameters(pld)) {
                msg.append(JaiI18N.formatMsg("OperationDescriptorImpl9", new Object[]{this.getName(), new Integer(numParams)}));
                return false;
            }
            for (i = argNumParams; i < numParams; ++i) {
                args.add(paramDefaults[i]);
            }
        }
        for (i = 0; i < numParams; ++i) {
            Object p = args.getObjectParameter(i);
            if (p == null) {
                p = paramDefaults[i];
                if (p == OperationDescriptor.NO_PARAMETER_DEFAULT) {
                    msg.append(JaiI18N.formatMsg("OperationDescriptorImpl11", new Object[]{this.getName(), new Integer(i)}));
                    return false;
                }
                args.set(p, i);
            }
            try {
                if (pld.isParameterValueValid(this.paramNames[i], p)) continue;
                msg.append(JaiI18N.formatMsg("OperationDescriptorImpl10", new Object[]{this.getName(), pld.getParamNames()[i]}));
                return false;
            }
            catch (IllegalArgumentException e) {
                msg.append(this.getName() + " - " + e.getLocalizedMessage());
                return false;
            }
        }
        return true;
    }

    private void checkModeName(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("OperationDescriptorImpl12"));
        }
        if (!this.modeIndices.contains(modeName)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl13", new Object[]{this.getName(), modeName}));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

