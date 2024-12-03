/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigBoolean;
import com.typesafe.config.impl.ConfigDouble;
import com.typesafe.config.impl.ConfigLong;
import com.typesafe.config.impl.ConfigNull;
import com.typesafe.config.impl.ConfigString;
import com.typesafe.config.impl.SimpleConfigList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

final class DefaultTransformer {
    DefaultTransformer() {
    }

    static AbstractConfigValue transform(AbstractConfigValue value, ConfigValueType requested) {
        if (value.valueType() == ConfigValueType.STRING) {
            String s = (String)value.unwrapped();
            switch (requested) {
                case NUMBER: {
                    try {
                        Long v = Long.parseLong(s);
                        return new ConfigLong(value.origin(), v, s);
                    }
                    catch (NumberFormatException v) {
                        try {
                            Double v2 = Double.parseDouble(s);
                            return new ConfigDouble(value.origin(), v2, s);
                        }
                        catch (NumberFormatException v2) {
                            break;
                        }
                    }
                }
                case NULL: {
                    if (!s.equals("null")) break;
                    return new ConfigNull(value.origin());
                }
                case BOOLEAN: {
                    if (s.equals("true") || s.equals("yes") || s.equals("on")) {
                        return new ConfigBoolean(value.origin(), true);
                    }
                    if (!s.equals("false") && !s.equals("no") && !s.equals("off")) break;
                    return new ConfigBoolean(value.origin(), false);
                }
                case LIST: {
                    break;
                }
                case OBJECT: {
                    break;
                }
            }
        } else if (requested == ConfigValueType.STRING) {
            switch (value.valueType()) {
                case NUMBER: 
                case BOOLEAN: {
                    return new ConfigString.Quoted(value.origin(), value.transformToString());
                }
                case NULL: {
                    break;
                }
                case OBJECT: {
                    break;
                }
                case LIST: {
                    break;
                }
            }
        } else if (requested == ConfigValueType.LIST && value.valueType() == ConfigValueType.OBJECT) {
            AbstractConfigObject o = (AbstractConfigObject)value;
            HashMap<Integer, AbstractConfigValue> values = new HashMap<Integer, AbstractConfigValue>();
            for (String key : o.keySet()) {
                try {
                    int i = Integer.parseInt(key, 10);
                    if (i < 0) continue;
                    values.put(i, o.get(key));
                }
                catch (NumberFormatException e) {}
            }
            if (!values.isEmpty()) {
                ArrayList entryList = new ArrayList(values.entrySet());
                Collections.sort(entryList, new Comparator<Map.Entry<Integer, AbstractConfigValue>>(){

                    @Override
                    public int compare(Map.Entry<Integer, AbstractConfigValue> a, Map.Entry<Integer, AbstractConfigValue> b) {
                        return Integer.compare(a.getKey(), b.getKey());
                    }
                });
                ArrayList<AbstractConfigValue> list = new ArrayList<AbstractConfigValue>();
                for (Map.Entry entry : entryList) {
                    list.add((AbstractConfigValue)entry.getValue());
                }
                return new SimpleConfigList(value.origin(), list);
            }
        }
        return value;
    }
}

