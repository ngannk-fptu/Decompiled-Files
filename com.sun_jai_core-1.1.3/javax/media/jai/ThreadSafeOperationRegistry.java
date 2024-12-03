/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.RWLock;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.PropertySource;
import javax.media.jai.RegistryElementDescriptor;

final class ThreadSafeOperationRegistry
extends OperationRegistry {
    private RWLock lock = new RWLock(true);

    public String toString() {
        try {
            this.lock.forReading();
            String t = super.toString();
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void writeToStream(OutputStream out) throws IOException {
        try {
            this.lock.forReading();
            super.writeToStream(out);
            this.lock.release();
        }
        catch (IOException ioe) {
            this.lock.release();
            throw ioe;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void initializeFromStream(InputStream in) throws IOException {
        try {
            this.lock.forWriting();
            super.initializeFromStream(in);
            this.lock.release();
        }
        catch (IOException ioe) {
            this.lock.release();
            throw ioe;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void updateFromStream(InputStream in) throws IOException {
        try {
            this.lock.forWriting();
            super.updateFromStream(in);
            this.lock.release();
        }
        catch (IOException ioe) {
            this.lock.release();
            throw ioe;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        try {
            this.lock.forWriting();
            super.readExternal(in);
            this.lock.release();
        }
        catch (IOException ioe) {
            this.lock.release();
            throw ioe;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        try {
            this.lock.forReading();
            super.writeExternal(out);
            this.lock.release();
        }
        catch (IOException ioe) {
            this.lock.release();
            throw ioe;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void removeRegistryMode(String modeName) {
        try {
            this.lock.forWriting();
            super.removeRegistryMode(modeName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public String[] getRegistryModes() {
        try {
            this.lock.forReading();
            String[] t = super.getRegistryModes();
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void registerDescriptor(RegistryElementDescriptor descriptor) {
        try {
            this.lock.forWriting();
            super.registerDescriptor(descriptor);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void unregisterDescriptor(RegistryElementDescriptor descriptor) {
        try {
            this.lock.forWriting();
            super.unregisterDescriptor(descriptor);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public RegistryElementDescriptor getDescriptor(Class descriptorClass, String descriptorName) {
        try {
            this.lock.forReading();
            RegistryElementDescriptor t = super.getDescriptor(descriptorClass, descriptorName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public List getDescriptors(Class descriptorClass) {
        try {
            this.lock.forReading();
            List t = super.getDescriptors(descriptorClass);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public String[] getDescriptorNames(Class descriptorClass) {
        try {
            this.lock.forReading();
            String[] t = super.getDescriptorNames(descriptorClass);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public RegistryElementDescriptor getDescriptor(String modeName, String descriptorName) {
        try {
            this.lock.forReading();
            RegistryElementDescriptor t = super.getDescriptor(modeName, descriptorName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public List getDescriptors(String modeName) {
        try {
            this.lock.forReading();
            List t = super.getDescriptors(modeName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public String[] getDescriptorNames(String modeName) {
        try {
            this.lock.forReading();
            String[] t = super.getDescriptorNames(modeName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void setProductPreference(String modeName, String descriptorName, String preferredProductName, String otherProductName) {
        try {
            this.lock.forWriting();
            super.setProductPreference(modeName, descriptorName, preferredProductName, otherProductName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void unsetProductPreference(String modeName, String descriptorName, String preferredProductName, String otherProductName) {
        try {
            this.lock.forWriting();
            super.unsetProductPreference(modeName, descriptorName, preferredProductName, otherProductName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void clearProductPreferences(String modeName, String descriptorName) {
        try {
            this.lock.forWriting();
            super.clearProductPreferences(modeName, descriptorName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public String[][] getProductPreferences(String modeName, String descriptorName) {
        try {
            this.lock.forReading();
            String[][] t = super.getProductPreferences(modeName, descriptorName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public Vector getOrderedProductList(String modeName, String descriptorName) {
        try {
            this.lock.forReading();
            Vector t = super.getOrderedProductList(modeName, descriptorName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void registerFactory(String modeName, String descriptorName, String productName, Object factory) {
        try {
            this.lock.forWriting();
            super.registerFactory(modeName, descriptorName, productName, factory);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void unregisterFactory(String modeName, String descriptorName, String productName, Object factory) {
        try {
            this.lock.forWriting();
            super.unregisterFactory(modeName, descriptorName, productName, factory);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void setFactoryPreference(String modeName, String descriptorName, String productName, Object preferredOp, Object otherOp) {
        try {
            this.lock.forWriting();
            super.setFactoryPreference(modeName, descriptorName, productName, preferredOp, otherOp);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void unsetFactoryPreference(String modeName, String descriptorName, String productName, Object preferredOp, Object otherOp) {
        try {
            this.lock.forWriting();
            super.unsetFactoryPreference(modeName, descriptorName, productName, preferredOp, otherOp);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void clearFactoryPreferences(String modeName, String descriptorName, String productName) {
        try {
            this.lock.forWriting();
            super.clearFactoryPreferences(modeName, descriptorName, productName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public Object[][] getFactoryPreferences(String modeName, String descriptorName, String productName) {
        try {
            this.lock.forReading();
            Object[][] t = super.getFactoryPreferences(modeName, descriptorName, productName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public List getOrderedFactoryList(String modeName, String descriptorName, String productName) {
        try {
            this.lock.forReading();
            List t = super.getOrderedFactoryList(modeName, descriptorName, productName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public Iterator getFactoryIterator(String modeName, String descriptorName) {
        try {
            this.lock.forReading();
            Iterator t = super.getFactoryIterator(modeName, descriptorName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public Object getFactory(String modeName, String descriptorName) {
        try {
            this.lock.forReading();
            Object t = super.getFactory(modeName, descriptorName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public Object invokeFactory(String modeName, String descriptorName, Object[] args) {
        try {
            this.lock.forReading();
            Object t = super.invokeFactory(modeName, descriptorName, args);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void addPropertyGenerator(String modeName, String descriptorName, PropertyGenerator generator) {
        try {
            this.lock.forWriting();
            super.addPropertyGenerator(modeName, descriptorName, generator);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void removePropertyGenerator(String modeName, String descriptorName, PropertyGenerator generator) {
        try {
            this.lock.forWriting();
            super.removePropertyGenerator(modeName, descriptorName, generator);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void copyPropertyFromSource(String modeName, String descriptorName, String propertyName, int sourceIndex) {
        try {
            this.lock.forWriting();
            super.copyPropertyFromSource(modeName, descriptorName, propertyName, sourceIndex);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void suppressProperty(String modeName, String descriptorName, String propertyName) {
        try {
            this.lock.forWriting();
            super.suppressProperty(modeName, descriptorName, propertyName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void suppressAllProperties(String modeName, String descriptorName) {
        try {
            this.lock.forWriting();
            super.suppressAllProperties(modeName, descriptorName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void clearPropertyState(String modeName) {
        try {
            this.lock.forWriting();
            super.clearPropertyState(modeName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public String[] getGeneratedPropertyNames(String modeName, String descriptorName) {
        try {
            this.lock.forReading();
            String[] t = super.getGeneratedPropertyNames(modeName, descriptorName);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public PropertySource getPropertySource(String modeName, String descriptorName, Object op, Vector sources) {
        try {
            this.lock.forReading();
            PropertySource t = super.getPropertySource(modeName, descriptorName, op, sources);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public PropertySource getPropertySource(OperationNode op) {
        try {
            this.lock.forReading();
            PropertySource t = super.getPropertySource(op);
            this.lock.release();
            return t;
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void registerServices(ClassLoader cl) throws IOException {
        try {
            this.lock.forWriting();
            super.registerServices(cl);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void unregisterOperationDescriptor(String operationName) {
        try {
            this.lock.forWriting();
            super.unregisterOperationDescriptor(operationName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }

    public void clearOperationPreferences(String operationName, String productName) {
        try {
            this.lock.forWriting();
            super.clearOperationPreferences(operationName, productName);
            this.lock.release();
        }
        catch (RuntimeException e) {
            this.lock.release();
            throw e;
        }
    }
}

