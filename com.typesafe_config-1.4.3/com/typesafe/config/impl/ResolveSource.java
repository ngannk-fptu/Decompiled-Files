/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.Container;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveResult;
import com.typesafe.config.impl.SimpleConfigObject;
import com.typesafe.config.impl.SubstitutionExpression;

final class ResolveSource {
    final AbstractConfigObject root;
    final Node<Container> pathFromRoot;

    ResolveSource(AbstractConfigObject root, Node<Container> pathFromRoot) {
        this.root = root;
        this.pathFromRoot = pathFromRoot;
    }

    ResolveSource(AbstractConfigObject root) {
        this.root = root;
        this.pathFromRoot = null;
    }

    private AbstractConfigObject rootMustBeObj(Container value) {
        if (value instanceof AbstractConfigObject) {
            return (AbstractConfigObject)value;
        }
        return SimpleConfigObject.empty();
    }

    private static ResultWithPath findInObject(AbstractConfigObject obj, ResolveContext context, Path path) throws AbstractConfigValue.NotPossibleToResolve {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("*** finding '" + path + "' in " + obj);
        }
        Path restriction = context.restrictToChild();
        ResolveResult<? extends AbstractConfigValue> partiallyResolved = context.restrict(path).resolve(obj, new ResolveSource(obj));
        ResolveContext newContext = partiallyResolved.context.restrict(restriction);
        if (partiallyResolved.value instanceof AbstractConfigObject) {
            ValueWithPath pair = ResolveSource.findInObject((AbstractConfigObject)partiallyResolved.value, path);
            return new ResultWithPath(ResolveResult.make(newContext, pair.value), pair.pathFromRoot);
        }
        throw new ConfigException.BugOrBroken("resolved object to non-object " + obj + " to " + partiallyResolved);
    }

    private static ValueWithPath findInObject(AbstractConfigObject obj, Path path) {
        try {
            return ResolveSource.findInObject(obj, path, null);
        }
        catch (ConfigException.NotResolved e) {
            throw ConfigImpl.improveNotResolved(path, e);
        }
    }

    private static ValueWithPath findInObject(AbstractConfigObject obj, Path path, Node<Container> parents) {
        Node<Container> newParents;
        String key = path.first();
        Path next = path.remainder();
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("*** looking up '" + key + "' in " + obj);
        }
        AbstractConfigValue v = obj.attemptPeekWithPartialResolve(key);
        Node<Container> node = newParents = parents == null ? new Node<Container>(obj) : parents.prepend(obj);
        if (next == null) {
            return new ValueWithPath(v, newParents);
        }
        if (v instanceof AbstractConfigObject) {
            return ResolveSource.findInObject((AbstractConfigObject)v, next, newParents);
        }
        return new ValueWithPath(null, newParents);
    }

    ResultWithPath lookupSubst(ResolveContext context, SubstitutionExpression subst, int prefixLength) throws AbstractConfigValue.NotPossibleToResolve {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(context.depth(), "searching for " + subst);
        }
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(context.depth(), subst + " - looking up relative to file it occurred in");
        }
        ResultWithPath result = ResolveSource.findInObject(this.root, context, subst.path());
        if (result.result.value == null) {
            Path unprefixed = subst.path().subPath(prefixLength);
            if (prefixLength > 0) {
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(result.result.context.depth(), unprefixed + " - looking up relative to parent file");
                }
                result = ResolveSource.findInObject(this.root, result.result.context, unprefixed);
            }
            if (result.result.value == null && result.result.context.options().getUseSystemEnvironment()) {
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(result.result.context.depth(), unprefixed + " - looking up in system environment");
                }
                result = ResolveSource.findInObject(ConfigImpl.envVariablesAsConfigObject(), context, unprefixed);
            }
        }
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(result.result.context.depth(), "resolved to " + result);
        }
        return result;
    }

    ResolveSource pushParent(Container parent) {
        if (parent == null) {
            throw new ConfigException.BugOrBroken("can't push null parent");
        }
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("pushing parent " + parent + " ==root " + (parent == this.root) + " onto " + this);
        }
        if (this.pathFromRoot == null) {
            if (parent == this.root) {
                return new ResolveSource(this.root, new Node<Container>(parent));
            }
            if (ConfigImpl.traceSubstitutionsEnabled() && this.root.hasDescendant((AbstractConfigValue)((Object)parent))) {
                ConfigImpl.trace("***** BUG ***** tried to push parent " + parent + " without having a path to it in " + this);
            }
            return this;
        }
        Container parentParent = this.pathFromRoot.head();
        if (ConfigImpl.traceSubstitutionsEnabled() && parentParent != null && !parentParent.hasDescendant((AbstractConfigValue)((Object)parent))) {
            ConfigImpl.trace("***** BUG ***** trying to push non-child of " + parentParent + ", non-child was " + parent);
        }
        return new ResolveSource(this.root, this.pathFromRoot.prepend(parent));
    }

    ResolveSource resetParents() {
        if (this.pathFromRoot == null) {
            return this;
        }
        return new ResolveSource(this.root);
    }

    private static Node<Container> replace(Node<Container> list, Container old, AbstractConfigValue replacement) {
        Container parent;
        Container child = list.head();
        if (child != old) {
            throw new ConfigException.BugOrBroken("Can only replace() the top node we're resolving; had " + child + " on top and tried to replace " + old + " overall list was " + list);
        }
        Container container = parent = list.tail() == null ? null : list.tail().head();
        if (replacement == null || !(replacement instanceof Container)) {
            if (parent == null) {
                return null;
            }
            AbstractConfigValue newParent = parent.replaceChild((AbstractConfigValue)((Object)old), null);
            return ResolveSource.replace(list.tail(), parent, newParent);
        }
        if (parent == null) {
            return new Node<Container>((Container)((Object)replacement));
        }
        AbstractConfigValue newParent = parent.replaceChild((AbstractConfigValue)((Object)old), replacement);
        Node<Container> newTail = ResolveSource.replace(list.tail(), parent, newParent);
        if (newTail != null) {
            return newTail.prepend((Container)((Object)replacement));
        }
        return new Node<Container>((Container)((Object)replacement));
    }

    ResolveSource replaceCurrentParent(Container old, Container replacement) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("replaceCurrentParent old " + old + "@" + System.identityHashCode(old) + " replacement " + replacement + "@" + System.identityHashCode(old) + " in " + this);
        }
        if (old == replacement) {
            return this;
        }
        if (this.pathFromRoot != null) {
            Node<Container> newPath = ResolveSource.replace(this.pathFromRoot, old, (AbstractConfigValue)((Object)replacement));
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace("replaced " + old + " with " + replacement + " in " + this);
                ConfigImpl.trace("path was: " + this.pathFromRoot + " is now " + newPath);
            }
            if (newPath != null) {
                return new ResolveSource((AbstractConfigObject)newPath.last(), newPath);
            }
            return new ResolveSource(SimpleConfigObject.empty());
        }
        if (old == this.root) {
            return new ResolveSource(this.rootMustBeObj(replacement));
        }
        throw new ConfigException.BugOrBroken("attempt to replace root " + this.root + " with " + replacement);
    }

    ResolveSource replaceWithinCurrentParent(AbstractConfigValue old, AbstractConfigValue replacement) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("replaceWithinCurrentParent old " + old + "@" + System.identityHashCode(old) + " replacement " + replacement + "@" + System.identityHashCode(old) + " in " + this);
        }
        if (old == replacement) {
            return this;
        }
        if (this.pathFromRoot != null) {
            Container parent;
            AbstractConfigValue newParent = (parent = this.pathFromRoot.head()).replaceChild(old, replacement);
            return this.replaceCurrentParent(parent, newParent instanceof Container ? (Container)((Object)newParent) : null);
        }
        if (old == this.root && replacement instanceof Container) {
            return new ResolveSource(this.rootMustBeObj((Container)((Object)replacement)));
        }
        throw new ConfigException.BugOrBroken("replace in parent not possible " + old + " with " + replacement + " in " + this);
    }

    public String toString() {
        return "ResolveSource(root=" + this.root + ", pathFromRoot=" + this.pathFromRoot + ")";
    }

    static final class ResultWithPath {
        final ResolveResult<? extends AbstractConfigValue> result;
        final Node<Container> pathFromRoot;

        ResultWithPath(ResolveResult<? extends AbstractConfigValue> result, Node<Container> pathFromRoot) {
            this.result = result;
            this.pathFromRoot = pathFromRoot;
        }

        public String toString() {
            return "ResultWithPath(result=" + this.result + ", pathFromRoot=" + this.pathFromRoot + ")";
        }
    }

    static final class ValueWithPath {
        final AbstractConfigValue value;
        final Node<Container> pathFromRoot;

        ValueWithPath(AbstractConfigValue value, Node<Container> pathFromRoot) {
            this.value = value;
            this.pathFromRoot = pathFromRoot;
        }

        public String toString() {
            return "ValueWithPath(value=" + this.value + ", pathFromRoot=" + this.pathFromRoot + ")";
        }
    }

    static final class Node<T> {
        final T value;
        final Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }

        Node(T value) {
            this(value, null);
        }

        Node<T> prepend(T value) {
            return new Node<T>(value, this);
        }

        T head() {
            return this.value;
        }

        Node<T> tail() {
            return this.next;
        }

        T last() {
            Node<T> i = this;
            while (i.next != null) {
                i = i.next;
            }
            return i.value;
        }

        Node<T> reverse() {
            if (this.next == null) {
                return this;
            }
            Node<T> reversed = new Node<T>(this.value);
            Node<T> i = this.next;
            while (i != null) {
                reversed = reversed.prepend(i.value);
                i = i.next;
            }
            return reversed;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            Node<T> toAppendValue = this.reverse();
            while (toAppendValue != null) {
                sb.append(toAppendValue.value.toString());
                if (toAppendValue.next != null) {
                    sb.append(" <= ");
                }
                toAppendValue = toAppendValue.next;
            }
            sb.append("]");
            return sb.toString();
        }
    }
}

