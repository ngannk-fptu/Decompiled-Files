/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;

public interface HandleDelegate {
    public void writeEJBObject(EJBObject var1, ObjectOutputStream var2) throws IOException;

    public EJBObject readEJBObject(ObjectInputStream var1) throws IOException, ClassNotFoundException;

    public void writeEJBHome(EJBHome var1, ObjectOutputStream var2) throws IOException;

    public EJBHome readEJBHome(ObjectInputStream var1) throws IOException, ClassNotFoundException;
}

