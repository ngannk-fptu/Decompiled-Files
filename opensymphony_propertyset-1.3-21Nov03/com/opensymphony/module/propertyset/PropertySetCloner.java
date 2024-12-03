/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset;

import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Iterator;

public class PropertySetCloner {
    private PropertySet destination;
    private PropertySet source;

    public void setDestination(PropertySet destination) {
        this.destination = destination;
    }

    public PropertySet getDestination() {
        return this.destination;
    }

    public void setSource(PropertySet source) {
        this.source = source;
    }

    public PropertySet getSource() {
        return this.source;
    }

    public void cloneProperties() throws PropertyException {
        this.clearDestination();
        Iterator keys = this.source.getKeys().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            this.cloneProperty(key);
        }
    }

    private void clearDestination() throws PropertyException {
        Iterator keys = this.destination.getKeys().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            this.destination.remove(key);
        }
    }

    private void cloneProperty(String key) throws PropertyException {
        switch (this.source.getType(key)) {
            case 1: {
                this.destination.setBoolean(key, this.source.getBoolean(key));
                break;
            }
            case 2: {
                this.destination.setInt(key, this.source.getInt(key));
                break;
            }
            case 3: {
                this.destination.setLong(key, this.source.getLong(key));
                break;
            }
            case 4: {
                this.destination.setDouble(key, this.source.getDouble(key));
                break;
            }
            case 5: {
                this.destination.setString(key, this.source.getString(key));
                break;
            }
            case 6: {
                this.destination.setText(key, this.source.getText(key));
                break;
            }
            case 7: {
                this.destination.setDate(key, this.source.getDate(key));
                break;
            }
            case 8: {
                this.destination.setObject(key, this.source.getObject(key));
                break;
            }
            case 9: {
                this.destination.setXML(key, this.source.getXML(key));
                break;
            }
            case 10: {
                this.destination.setData(key, this.source.getData(key));
                break;
            }
            case 11: {
                this.destination.setProperties(key, this.source.getProperties(key));
            }
        }
    }
}

