/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.mapping.Mapper;
import org.seedstack.business.domain.AggregateExistsException;
import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseRepository;
import org.seedstack.business.specification.Specification;
import org.seedstack.mongodb.morphia.internal.DatastoreFactory;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class can serve as a base class for Morphia repositories. It provides methods for common CRUD operations as
 * well as access to the data store through the {@link #getDatastore()} ()} protected method.
 *
 * @param <A>  Aggregate root class.
 * @param <ID> Identifier class.
 */
public abstract class BaseMorphiaRepository<A extends AggregateRoot<ID>, ID> extends BaseRepository<A, ID> {
    private Datastore datastore;

    public BaseMorphiaRepository() {

    }

    public BaseMorphiaRepository(Class<A> aggregateRootClass, Class<ID> kClass) {
        super(aggregateRootClass, kClass);
    }

    @Inject
    private void initDatastore(DatastoreFactory datastoreFactory) {
        datastore = datastoreFactory.createDatastore(getAggregateRootClass());
    }

    /**
     * Provides access to the Morphia data store for implementing custom data access methods.
     *
     * @return the Morphia data store.
     */
    protected Datastore getDatastore() {
        return datastore;
    }

    @Override
    public void add(A aggregate) throws AggregateExistsException {
        datastore.save(aggregate);
    }

    @Override
    public Stream<A> get(Specification<A> specification, Option... options) {
        datastore.createQuery(getAggregateRootClass());
        // TODO
        return null;
    }

    @Override
    public Optional<A> get(ID id) {
        return Optional.ofNullable(datastore.get(getAggregateRootClass(), id));
    }

    @Override
    public boolean contains(Specification<A> specification) {
        // TODO
        return false;
    }

    @Override
    public boolean contains(ID id) {
        return datastore.find(getAggregateRootClass()).filter(Mapper.ID_KEY, id).getKey() != null;
    }

    @Override
    public long count(Specification<A> specification) {
        // TODO
        return 0;
    }

    @Override
    public long size() {
        return datastore.getCount(getAggregateRootClass());
    }

    @Override
    public long remove(Specification<A> specification) throws AggregateNotFoundException {
        // TODO
        return 0;
    }

    @Override
    public void remove(ID id) throws AggregateNotFoundException {
        checkExactlyOneAggregateRemoved(datastore.delete(getAggregateRootClass(), id).getN(), id);
    }

    @Override
    public void remove(A aggregate) throws AggregateNotFoundException {
        checkExactlyOneAggregateRemoved(datastore.delete(aggregate).getN(), aggregate.getId());
    }

    private void checkExactlyOneAggregateRemoved(int n, ID id) {
        if (n == 0) {
            throw new AggregateNotFoundException("Non-existent aggregate " + getAggregateRootClass().getSimpleName() + " identified with " + id + " cannot be removed");
        } else if (n > 1) {
            throw new IllegalStateException("More than one aggregate " + getAggregateRootClass().getSimpleName() + " identified with " + id + " have been removed");
        }
    }

    @Override
    public void update(A aggregate) throws AggregateNotFoundException {
        if (!contains(aggregate)) {
            throw new AggregateNotFoundException("Non-existent aggregate " + getAggregateRootClass().getSimpleName() + " identified with " + aggregate.getId() + " cannot be updated");
        }
        datastore.merge(aggregate);
    }

    @Override
    public void clear() {
        datastore.getCollection(getAggregateRootClass()).drop();
        datastore.getCollection(getAggregateRootClass()).dropIndexes();
    }
}