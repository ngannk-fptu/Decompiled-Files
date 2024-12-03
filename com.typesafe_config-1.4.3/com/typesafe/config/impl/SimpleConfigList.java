/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.Container;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveResult;
import com.typesafe.config.impl.ResolveSource;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SerializedConfigValue;
import com.typesafe.config.impl.SimpleConfigOrigin;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

final class SimpleConfigList
extends AbstractConfigValue
implements ConfigList,
Container,
Serializable {
    private static final long serialVersionUID = 2L;
    private final List<AbstractConfigValue> value;
    private final boolean resolved;

    SimpleConfigList(ConfigOrigin origin, List<AbstractConfigValue> value) {
        this(origin, value, ResolveStatus.fromValues(value));
    }

    SimpleConfigList(ConfigOrigin origin, List<AbstractConfigValue> value, ResolveStatus status) {
        super(origin);
        this.value = value;
        boolean bl = this.resolved = status == ResolveStatus.RESOLVED;
        if (status != ResolveStatus.fromValues(value)) {
            throw new ConfigException.BugOrBroken("SimpleConfigList created with wrong resolve status: " + this);
        }
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.LIST;
    }

    @Override
    public List<Object> unwrapped() {
        ArrayList<Object> list = new ArrayList<Object>();
        for (AbstractConfigValue v : this.value) {
            list.add(v.unwrapped());
        }
        return list;
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.fromBoolean(this.resolved);
    }

    @Override
    public SimpleConfigList replaceChild(AbstractConfigValue child, AbstractConfigValue replacement) {
        List<AbstractConfigValue> newList = SimpleConfigList.replaceChildInList(this.value, child, replacement);
        if (newList == null) {
            return null;
        }
        return new SimpleConfigList(this.origin(), newList);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue descendant) {
        return SimpleConfigList.hasDescendantInList(this.value, descendant);
    }

    private SimpleConfigList modify(AbstractConfigValue.NoExceptionsModifier modifier, ResolveStatus newResolveStatus) {
        try {
            return this.modifyMayThrow(modifier, newResolveStatus);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", e);
        }
    }

    private SimpleConfigList modifyMayThrow(AbstractConfigValue.Modifier modifier, ResolveStatus newResolveStatus) throws Exception {
        ArrayList<AbstractConfigValue> changed = null;
        int i = 0;
        for (AbstractConfigValue v : this.value) {
            AbstractConfigValue modified = modifier.modifyChildMayThrow(null, v);
            if (changed == null && modified != v) {
                changed = new ArrayList<AbstractConfigValue>();
                for (int j = 0; j < i; ++j) {
                    changed.add(this.value.get(j));
                }
            }
            if (changed != null && modified != null) {
                changed.add(modified);
            }
            ++i;
        }
        if (changed != null) {
            if (newResolveStatus != null) {
                return new SimpleConfigList(this.origin(), changed, newResolveStatus);
            }
            return new SimpleConfigList(this.origin(), changed);
        }
        return this;
    }

    ResolveResult<? extends SimpleConfigList> resolveSubstitutions(ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
        if (this.resolved) {
            return ResolveResult.make(context, this);
        }
        if (context.isRestrictedToChild()) {
            return ResolveResult.make(context, this);
        }
        try {
            ResolveModifier modifier = new ResolveModifier(context, source.pushParent(this));
            SimpleConfigList value = this.modifyMayThrow(modifier, context.options().getAllowUnresolved() ? null : ResolveStatus.RESOLVED);
            return ResolveResult.make(modifier.context, value);
        }
        catch (AbstractConfigValue.NotPossibleToResolve e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", e);
        }
    }

    @Override
    SimpleConfigList relativized(final Path prefix) {
        return this.modify(new AbstractConfigValue.NoExceptionsModifier(){

            @Override
            public AbstractConfigValue modifyChild(String key, AbstractConfigValue v) {
                return v.relativized(prefix);
            }
        }, this.resolveStatus());
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof SimpleConfigList;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SimpleConfigList) {
            return this.canEqual(other) && (this.value == ((SimpleConfigList)other).value || this.value.equals(((SimpleConfigList)other).value));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
        if (this.value.isEmpty()) {
            sb.append("[]");
        } else {
            sb.append("[");
            if (options.getFormatted()) {
                sb.append('\n');
            }
            for (AbstractConfigValue v : this.value) {
                if (options.getOriginComments()) {
                    String[] lines;
                    for (String l : lines = v.origin().description().split("\n")) {
                        SimpleConfigList.indent(sb, indent + 1, options);
                        sb.append('#');
                        if (!l.isEmpty()) {
                            sb.append(' ');
                        }
                        sb.append(l);
                        sb.append("\n");
                    }
                }
                if (options.getComments()) {
                    for (String comment : v.origin().comments()) {
                        SimpleConfigList.indent(sb, indent + 1, options);
                        sb.append("# ");
                        sb.append(comment);
                        sb.append("\n");
                    }
                }
                SimpleConfigList.indent(sb, indent + 1, options);
                v.render(sb, indent + 1, atRoot, options);
                sb.append(",");
                if (!options.getFormatted()) continue;
                sb.append('\n');
            }
            sb.setLength(sb.length() - 1);
            if (options.getFormatted()) {
                sb.setLength(sb.length() - 1);
                sb.append('\n');
                SimpleConfigList.indent(sb, indent, options);
            }
            sb.append("]");
        }
    }

    @Override
    public boolean contains(Object o) {
        return this.value.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.value.containsAll(c);
    }

    @Override
    public AbstractConfigValue get(int index) {
        return this.value.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.value.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public Iterator<ConfigValue> iterator() {
        final Iterator<AbstractConfigValue> i = this.value.iterator();
        return new Iterator<ConfigValue>(){

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public ConfigValue next() {
                return (ConfigValue)i.next();
            }

            @Override
            public void remove() {
                throw SimpleConfigList.weAreImmutable("iterator().remove");
            }
        };
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.value.lastIndexOf(o);
    }

    private static ListIterator<ConfigValue> wrapListIterator(final ListIterator<AbstractConfigValue> i) {
        return new ListIterator<ConfigValue>(){

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public ConfigValue next() {
                return (ConfigValue)i.next();
            }

            @Override
            public void remove() {
                throw SimpleConfigList.weAreImmutable("listIterator().remove");
            }

            @Override
            public void add(ConfigValue arg0) {
                throw SimpleConfigList.weAreImmutable("listIterator().add");
            }

            @Override
            public boolean hasPrevious() {
                return i.hasPrevious();
            }

            @Override
            public int nextIndex() {
                return i.nextIndex();
            }

            @Override
            public ConfigValue previous() {
                return (ConfigValue)i.previous();
            }

            @Override
            public int previousIndex() {
                return i.previousIndex();
            }

            @Override
            public void set(ConfigValue arg0) {
                throw SimpleConfigList.weAreImmutable("listIterator().set");
            }
        };
    }

    @Override
    public ListIterator<ConfigValue> listIterator() {
        return SimpleConfigList.wrapListIterator(this.value.listIterator());
    }

    @Override
    public ListIterator<ConfigValue> listIterator(int index) {
        return SimpleConfigList.wrapListIterator(this.value.listIterator(index));
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public List<ConfigValue> subList(int fromIndex, int toIndex) {
        ArrayList<ConfigValue> list = new ArrayList<ConfigValue>();
        for (AbstractConfigValue v : this.value.subList(fromIndex, toIndex)) {
            list.add(v);
        }
        return list;
    }

    @Override
    public Object[] toArray() {
        return this.value.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.value.toArray(a);
    }

    private static UnsupportedOperationException weAreImmutable(String method) {
        return new UnsupportedOperationException("ConfigList is immutable, you can't call List.'" + method + "'");
    }

    @Override
    public boolean add(ConfigValue e) {
        throw SimpleConfigList.weAreImmutable("add");
    }

    @Override
    public void add(int index, ConfigValue element) {
        throw SimpleConfigList.weAreImmutable("add");
    }

    @Override
    public boolean addAll(Collection<? extends ConfigValue> c) {
        throw SimpleConfigList.weAreImmutable("addAll");
    }

    @Override
    public boolean addAll(int index, Collection<? extends ConfigValue> c) {
        throw SimpleConfigList.weAreImmutable("addAll");
    }

    @Override
    public void clear() {
        throw SimpleConfigList.weAreImmutable("clear");
    }

    @Override
    public boolean remove(Object o) {
        throw SimpleConfigList.weAreImmutable("remove");
    }

    @Override
    public ConfigValue remove(int index) {
        throw SimpleConfigList.weAreImmutable("remove");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw SimpleConfigList.weAreImmutable("removeAll");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw SimpleConfigList.weAreImmutable("retainAll");
    }

    @Override
    public ConfigValue set(int index, ConfigValue element) {
        throw SimpleConfigList.weAreImmutable("set");
    }

    @Override
    protected SimpleConfigList newCopy(ConfigOrigin newOrigin) {
        return new SimpleConfigList(newOrigin, this.value);
    }

    final SimpleConfigList concatenate(SimpleConfigList other) {
        ConfigOrigin combinedOrigin = SimpleConfigOrigin.mergeOrigins(this.origin(), other.origin());
        ArrayList<AbstractConfigValue> combined = new ArrayList<AbstractConfigValue>(this.value.size() + other.value.size());
        combined.addAll(this.value);
        combined.addAll(other.value);
        return new SimpleConfigList(combinedOrigin, combined);
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializedConfigValue(this);
    }

    @Override
    public SimpleConfigList withOrigin(ConfigOrigin origin) {
        return (SimpleConfigList)super.withOrigin(origin);
    }

    private static class ResolveModifier
    implements AbstractConfigValue.Modifier {
        ResolveContext context;
        final ResolveSource source;

        ResolveModifier(ResolveContext context, ResolveSource source) {
            this.context = context;
            this.source = source;
        }

        @Override
        public AbstractConfigValue modifyChildMayThrow(String key, AbstractConfigValue v) throws AbstractConfigValue.NotPossibleToResolve {
            ResolveResult<? extends AbstractConfigValue> result = this.context.resolve(v, this.source);
            this.context = result.context;
            return result.value;
        }
    }
}

