/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.util.Vector;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationNode;
import javax.media.jai.PropertySource;

class PropertyGeneratorFromSource
extends PropertyGeneratorImpl {
    int sourceIndex;
    String propertyName;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$javax$media$jai$OperationNode;

    PropertyGeneratorFromSource(int sourceIndex, String propertyName) {
        super(new String[]{propertyName}, new Class[]{class$java$lang$Object == null ? (class$java$lang$Object = PropertyGeneratorFromSource.class$("java.lang.Object")) : class$java$lang$Object}, new Class[]{class$javax$media$jai$OperationNode == null ? (class$javax$media$jai$OperationNode = PropertyGeneratorFromSource.class$("javax.media.jai.OperationNode")) : class$javax$media$jai$OperationNode});
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.sourceIndex = sourceIndex;
        this.propertyName = propertyName;
    }

    public Object getProperty(String name, Object opNode) {
        Object src;
        OperationNode op;
        Vector<Object> sources;
        if (name == null || opNode == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sourceIndex >= 0 && opNode instanceof OperationNode && this.propertyName.equalsIgnoreCase(name) && (sources = (op = (OperationNode)opNode).getParameterBlock().getSources()) != null && this.sourceIndex < sources.size() && (src = sources.elementAt(this.sourceIndex)) instanceof PropertySource) {
            return ((PropertySource)src).getProperty(name);
        }
        return Image.UndefinedProperty;
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

