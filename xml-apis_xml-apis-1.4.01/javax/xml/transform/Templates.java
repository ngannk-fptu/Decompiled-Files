/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform;

import java.util.Properties;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

public interface Templates {
    public Transformer newTransformer() throws TransformerConfigurationException;

    public Properties getOutputProperties();
}

