/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia;

import org.mongodb.morphia.Datastore;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseRepository;

/**
 * This class can serve as a base class for Morphia repositories. It provides methods for common CRUD operations as
 * well as access to the data store through the {@link #getDatastore()} ()} protected method.
 *
 * @param <A> Aggregate root class
 * @param <K> Key class
 * @author redouane.loulou@ext.mpsa.com
 */
public abstract class BaseMorphiaRepository<A extends AggregateRoot<K>, K> extends BaseRepository<A, K> {
    private final Datastore datastore;

    /**
     * Provides access to the Morphia data store for implementing custom data access methods.
     *
     * @return the Morphia data store.
     */
    protected Datastore getDatastore() {
        return datastore;
    }

    public BaseMorphiaRepository(Class<A> aggregateRootClass, Class<K> kClass, Datastore datastore) {
        super(aggregateRootClass, kClass);
        this.datastore = datastore;
    }

    @Override
    public A load(K id) {
        return datastore.get(getAggregateRootClass(), id);
    }

    @Override
    public void clear() {
        datastore.getCollection(getAggregateRootClass()).drop();
    }

    @Override
    public void delete(K id) {
        datastore.delete(getAggregateRootClass(), id);
    }

    @Override
    public void delete(A aggregate) {
        datastore.delete(aggregate);
    }

    @Override
    public void persist(A aggregate) {
        datastore.save(aggregate);
    }

    @Override
    public A save(A aggregate) {
        datastore.merge(aggregate);
        return aggregate;
    }

    @Override
    public boolean exists(K id) {
        return load(id) != null;
    }

    @Override
    public long count() {
        return datastore.getCount(getAggregateRootClass());
    }
}