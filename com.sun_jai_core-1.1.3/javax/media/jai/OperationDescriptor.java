/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.media.jai.OperationNode;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RegistryElementDescriptor;

public interface OperationDescriptor
extends RegistryElementDescriptor {
    public static final Object NO_PARAMETER_DEFAULT = ParameterListDescriptor.NO_PARAMETER_DEFAULT;

    public String[][] getResources(Locale var1);

    public ResourceBundle getResourceBundle(Locale var1);

    public int getNumSources();

    public Class[] getSourceClasses(String var1);

    public String[] getSourceNames();

    public Class getDestClass(String var1);

    public boolean validateArguments(String var1, ParameterBlock var2, StringBuffer var3);

    public boolean isImmediate();

    public Object getInvalidRegion(String var1, ParameterBlock var2, RenderingHints var3, ParameterBlock var4, RenderingHints var5, OperationNode var6);

    public PropertyGenerator[] getPropertyGenerators();

    public boolean isRenderedSupported();

    public Class[] getSourceClasses();

    public Class getDestClass();

    public boolean validateArguments(ParameterBlock var1, StringBuffer var2);

    public boolean isRenderableSupported();

    public Class[] getRenderableSourceClasses();

    public Class getRenderableDestClass();

    public boolean validateRenderableArguments(ParameterBlock var1, StringBuffer var2);

    public int getNumParameters();

    public Class[] getParamClasses();

    public String[] getParamNames();

    public Object[] getParamDefaults();

    public Object getParamDefaultValue(int var1);

    public Number getParamMinValue(int var1);

    public Number getParamMaxValue(int var1);
}

