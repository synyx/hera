/*
 * Copyright 2008-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.synyx.hera.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Basic implementation of {@link PluginRegistry}. Simply holds all given
 * plugins in a list dropping {@literal null} values silently on adding.
 * 
 * @param <T> the concrete plugin interface
 * @param <S> the delimiter type
 * @author Oliver Gierke - gierke@synyx.de
 */
public class SimplePluginRegistry<T extends Plugin<S>, S> implements
        MutablePluginRegistry<T, S> {

    protected List<T> plugins;


    /**
     * Creates a new {@code SimplePluginRegistry}. Will create an empty registry
     * if {@literal null} is provided.
     */
    protected SimplePluginRegistry(List<? extends T> plugins) {

        this.plugins = filterNulls(plugins);
    }


    /**
     * Creates a new {@link SimplePluginRegistry}.
     * 
     * @param <T>
     * @param <S>
     * @return
     */
    public static <S, T extends Plugin<S>> SimplePluginRegistry<T, S> create() {

        return new SimplePluginRegistry<T, S>(null);
    }


    /**
     * Creates a new {@link SimplePluginRegistry} with the given {@link Plugin}
     * s.
     * 
     * @param <T>
     * @param <S>
     * @return
     */
    public static <S, T extends Plugin<S>> SimplePluginRegistry<T, S> create(
            List<? extends T> plugins) {

        return new SimplePluginRegistry<T, S>(plugins);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.MutablePluginRegistry#setPlugins(java.util.List)
     */
    public void setPlugins(List<? extends T> plugins) {

        this.plugins = filterNulls(plugins);
    }


    /**
     * Filters {@literal null} values from the given list.
     * 
     * @param plugins
     * @return an empty {@link List} if the given list is {@literal null} or
     *         empty, or the original list without its {@literal null} elements.
     */
    private List<T> filterNulls(List<? extends T> plugins) {

        if (plugins == null || plugins.isEmpty()) {
            return new ArrayList<T>();
        }

        List<T> result = new ArrayList<T>();

        for (T plugin : plugins) {
            if (plugin != null) {
                result.add(plugin);
            }
        }

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hera.core.MutablePluginRegistry#addPlugin(org.synyx.hera.core
     * .Plugin)
     */
    public SimplePluginRegistry<T, S> addPlugin(T plugin) {

        if (plugin != null) {
            this.plugins.add(plugin);
        }

        return this;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hera.core.MutablePluginRegistry#removePlugin(org.synyx.hera
     * .core.Plugin)
     */
    public boolean removePlugin(T plugin) {

        return this.plugins.remove(plugin);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.PluginRegistry#getPluginFor(S)
     */
    public T getPluginFor(S delimiter) {

        List<T> result = getPluginsFor(delimiter);

        if (0 < result.size()) {
            return result.get(0);
        }

        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.PluginRegistry#getPluginsFor(S)
     */
    public List<T> getPluginsFor(S delimiter) {

        List<T> result = new ArrayList<T>();

        for (T plugin : plugins) {
            if (plugin.supports(delimiter)) {
                result.add(plugin);
            }
        }

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.PluginRegistry#getPluginFor(S, E)
     */
    public <E extends Exception> T getPluginFor(S delimiter, E ex) throws E {

        T plugin = getPluginFor(delimiter);

        if (null == plugin) {
            throw ex;
        }

        return plugin;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.PluginRegistry#getPluginsFor(S, E)
     */
    public <E extends Exception> List<T> getPluginsFor(S delimiter, E ex)
            throws E {

        List<T> result = getPluginsFor(delimiter);

        if (0 == result.size()) {
            throw ex;
        }

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.PluginRegistry#getPluginFor(S, T)
     */
    public T getPluginFor(S delimiter, T plugin) {

        T candidate = getPluginFor(delimiter);

        return null == candidate ? plugin : candidate;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.PluginRegistry#getPluginsFor(S, java.util.List)
     */
    public List<T> getPluginsFor(S delimiter, List<? extends T> plugins) {

        List<T> candidates = getPluginsFor(delimiter);

        return candidates.size() == 0 ? new ArrayList<T>(plugins) : candidates;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.PluginRegistry#countPlugins()
     */
    public int countPlugins() {

        return plugins.size();
    }


    /**
     * Returns all registered plugins. Only use this method if you really need
     * to access all plugins. For distinguished access to certain plugins favour
     * accessor methods like {link #getPluginFor} over this one. This method
     * should only be used for testing purposes to check registry configuration.
     * 
     * @return all plugins of the registry
     */
    public List<T> getPlugins() {

        return Collections.unmodifiableList(plugins);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hera.core.PluginRegistry#contains(org.synyx.hera.core.Plugin)
     */
    public boolean contains(T plugin) {

        return this.plugins.contains(plugin);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.PluginRegistry#hasPluginFor(java.lang.Object)
     */
    public boolean hasPluginFor(S delimter) {

        return null != getPluginFor(delimter);
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<T> iterator() {

        return plugins.iterator();
    }
}
