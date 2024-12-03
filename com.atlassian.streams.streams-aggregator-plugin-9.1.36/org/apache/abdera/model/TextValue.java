/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.abdera.model;

import java.io.InputStream;
import javax.activation.DataHandler;
import org.apache.abdera.model.Base;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TextValue {
    public DataHandler getDataHandler();

    public InputStream getInputStream();

    public String getText();

    public <T extends Base> T getParentElement();

    public void discard();
}

