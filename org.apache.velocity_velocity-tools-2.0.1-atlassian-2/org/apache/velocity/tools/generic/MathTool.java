/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.PropertyUtils
 */
package org.apache.velocity.tools.generic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.FormatConfig;

@DefaultKey(value="math")
public class MathTool
extends FormatConfig {
    public Number add(Object num1, Object num2) {
        return this.add(new Object[]{num1, num2});
    }

    public Number sub(Object num1, Object num2) {
        return this.sub(new Object[]{num1, num2});
    }

    public Number mul(Object num1, Object num2) {
        return this.mul(new Object[]{num1, num2});
    }

    public Number div(Object num1, Object num2) {
        return this.div(new Object[]{num1, num2});
    }

    public Number max(Object num1, Object num2) {
        return this.max(new Object[]{num1, num2});
    }

    public Number min(Object num1, Object num2) {
        return this.min(new Object[]{num1, num2});
    }

    public Number add(Object ... nums) {
        double value = 0.0;
        Number[] ns = new Number[nums.length];
        for (Object num : nums) {
            Number n = this.toNumber(num);
            if (n == null) {
                return null;
            }
            value += n.doubleValue();
        }
        return this.matchType(value, ns);
    }

    public Number sub(Object ... nums) {
        double value = 0.0;
        Number[] ns = new Number[nums.length];
        for (int i = 0; i < nums.length; ++i) {
            Number n = this.toNumber(nums[i]);
            if (n == null) {
                return null;
            }
            if (i == 0) {
                value = n.doubleValue();
                continue;
            }
            value -= n.doubleValue();
        }
        return this.matchType(value, ns);
    }

    public Number mul(Object ... nums) {
        double value = 1.0;
        Number[] ns = new Number[nums.length];
        for (Object num : nums) {
            Number n = this.toNumber(num);
            if (n == null) {
                return null;
            }
            value *= n.doubleValue();
        }
        return this.matchType(value, ns);
    }

    public Number div(Object ... nums) {
        double value = 0.0;
        Number[] ns = new Number[nums.length];
        for (int i = 0; i < nums.length; ++i) {
            Number n = this.toNumber(nums[i]);
            if (n == null) {
                return null;
            }
            if (i == 0) {
                value = n.doubleValue();
                continue;
            }
            double denominator = n.doubleValue();
            if (denominator == 0.0) {
                return null;
            }
            value /= denominator;
        }
        return this.matchType(value, ns);
    }

    public Number pow(Object num1, Object num2) {
        Number n1 = this.toNumber(num1);
        Number n2 = this.toNumber(num2);
        if (n1 == null || n2 == null) {
            return null;
        }
        double value = Math.pow(n1.doubleValue(), n2.doubleValue());
        return this.matchType(n1, n2, value);
    }

    public Integer idiv(Object num1, Object num2) {
        Number n1 = this.toNumber(num1);
        Number n2 = this.toNumber(num2);
        if (n1 == null || n2 == null || n2.intValue() == 0) {
            return null;
        }
        int value = n1.intValue() / n2.intValue();
        return value;
    }

    public Integer mod(Object num1, Object num2) {
        Number n1 = this.toNumber(num1);
        Number n2 = this.toNumber(num2);
        if (n1 == null || n2 == null || n2.intValue() == 0) {
            return null;
        }
        int value = n1.intValue() % n2.intValue();
        return value;
    }

    public Number max(Object ... nums) {
        double value = Double.MIN_VALUE;
        Number[] ns = new Number[nums.length];
        for (Object num : nums) {
            Number n = this.toNumber(num);
            if (n == null) {
                return null;
            }
            value = Math.max(value, n.doubleValue());
        }
        return this.matchType(value, ns);
    }

    public Number min(Object ... nums) {
        double value = Double.MAX_VALUE;
        Number[] ns = new Number[nums.length];
        for (Object num : nums) {
            Number n = this.toNumber(num);
            if (n == null) {
                return null;
            }
            value = Math.min(value, n.doubleValue());
        }
        return this.matchType(value, ns);
    }

    public Number abs(Object num) {
        Number n = this.toNumber(num);
        if (n == null) {
            return null;
        }
        double value = Math.abs(n.doubleValue());
        return this.matchType(n, value);
    }

    public Integer ceil(Object num) {
        Number n = this.toNumber(num);
        if (n == null) {
            return null;
        }
        return (int)Math.ceil(n.doubleValue());
    }

    public Integer floor(Object num) {
        Number n = this.toNumber(num);
        if (n == null) {
            return null;
        }
        return (int)Math.floor(n.doubleValue());
    }

    public Integer round(Object num) {
        Number n = this.toNumber(num);
        if (n == null) {
            return null;
        }
        return (int)Math.rint(n.doubleValue());
    }

    public Double roundTo(Object decimals, Object num) {
        Number i = this.toNumber(decimals);
        Number d = this.toNumber(num);
        if (i == null || d == null) {
            return null;
        }
        int places = i.intValue();
        double value = d.doubleValue();
        int delta = 10;
        for (int j = 1; j < places; ++j) {
            delta *= 10;
        }
        return new Double((double)Math.round(value * (double)delta) / (double)delta);
    }

    public Double getRandom() {
        return new Double(Math.random());
    }

    public Number random(Object num1, Object num2) {
        Number n1 = this.toNumber(num1);
        Number n2 = this.toNumber(num2);
        if (n1 == null || n2 == null) {
            return null;
        }
        double diff = n2.doubleValue() - n1.doubleValue();
        double random = diff * Math.random() + n1.doubleValue();
        String in = n1.toString() + n2.toString();
        if (in.indexOf(46) < 0) {
            return this.matchType(n1, n2, Math.floor(random));
        }
        return new Double(random);
    }

    public Integer toInteger(Object num) {
        Number n = this.toNumber(num);
        if (n == null) {
            return null;
        }
        return n.intValue();
    }

    public Double toDouble(Object num) {
        Number n = this.toNumber(num);
        if (n == null) {
            return null;
        }
        return new Double(n.doubleValue());
    }

    public Number toNumber(Object num) {
        return ConversionUtils.toNumber(num, this.getFormat(), this.getLocale());
    }

    protected Number matchType(Number in, double out) {
        return this.matchType(out, in);
    }

    protected Number matchType(Number in1, Number in2, double out) {
        return this.matchType(out, in1, in2);
    }

    protected Number matchType(double out, Number ... in) {
        boolean isIntegral;
        boolean bl = isIntegral = Math.rint(out) == out;
        if (isIntegral) {
            for (Number n : in) {
                if (n == null) break;
                if (!this.hasFloatingPoint(n.toString())) continue;
                isIntegral = false;
                break;
            }
        }
        if (!isIntegral) {
            return new Double(out);
        }
        if (out > 2.147483647E9 || out < -2.147483648E9) {
            return (long)out;
        }
        return (int)out;
    }

    protected boolean hasFloatingPoint(String value) {
        return value.indexOf(46) >= 0;
    }

    @Deprecated
    protected Number parseNumber(String value) {
        if (!this.hasFloatingPoint(value)) {
            long i = Long.valueOf(value);
            if (i > Integer.MAX_VALUE || i < Integer.MIN_VALUE) {
                return i;
            }
            return (int)i;
        }
        return new Double(value);
    }

    public Number getTotal(Collection collection, String field) {
        if (collection == null || field == null) {
            return null;
        }
        double result = 0.0;
        Number first = null;
        try {
            Iterator i = collection.iterator();
            while (i.hasNext()) {
                Object property = PropertyUtils.getProperty(i.next(), (String)field);
                Number value = this.toNumber(property);
                if (value == null) continue;
                if (first == null) {
                    first = value;
                }
                result += value.doubleValue();
            }
            return this.matchType(first, result);
        }
        catch (Exception e) {
            return null;
        }
    }

    public Number getAverage(Collection collection, String field) {
        Number result = this.getTotal(collection, field);
        if (result == null) {
            return null;
        }
        double avg = result.doubleValue() / (double)collection.size();
        return this.matchType(result, avg);
    }

    public Number getTotal(Object[] array, String field) {
        return this.getTotal(Arrays.asList(array), field);
    }

    public Number getAverage(Object[] array, String field) {
        return this.getAverage(Arrays.asList(array), field);
    }

    public Number getTotal(Collection collection) {
        if (collection == null) {
            return null;
        }
        double result = 0.0;
        Number first = null;
        Iterator i = collection.iterator();
        while (i.hasNext()) {
            Number value = this.toNumber(i.next());
            if (value == null) {
                return null;
            }
            if (first == null) {
                first = value;
            }
            result += value.doubleValue();
        }
        return this.matchType(first, result);
    }

    public Number getAverage(Collection collection) {
        Number result = this.getTotal(collection);
        if (result == null) {
            return null;
        }
        double avg = result.doubleValue() / (double)collection.size();
        return this.matchType(result, avg);
    }

    public Number getTotal(Object ... array) {
        return this.getTotal(Arrays.asList(array));
    }

    public Number getAverage(Object ... array) {
        return this.getAverage(Arrays.asList(array));
    }

    public Number getTotal(double ... values) {
        if (values == null) {
            return null;
        }
        double result = 0.0;
        for (int i = 0; i < values.length; ++i) {
            result += values[i];
        }
        return new Double(result);
    }

    public Number getAverage(double ... values) {
        Number total = this.getTotal(values);
        if (total == null) {
            return null;
        }
        return new Double(total.doubleValue() / (double)values.length);
    }

    public Number getTotal(long ... values) {
        if (values == null) {
            return null;
        }
        long result = 0L;
        for (int i = 0; i < values.length; ++i) {
            result += values[i];
        }
        return result;
    }

    public Number getAverage(long ... values) {
        Number total = this.getTotal(values);
        if (total == null) {
            return null;
        }
        double avg = total.doubleValue() / (double)values.length;
        return this.matchType(total, avg);
    }
}

