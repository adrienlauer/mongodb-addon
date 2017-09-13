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
import org.mongodb.morphia.query.CountOptions;
import org.mongodb.morphia.query.CriteriaContainer;
import org.mongodb.morphia.query.Query;
import org.seedstack.business.domain.AggregateExistsException;
import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseRepository;
import org.seedstack.business.specification.Specification;
import org.seedstack.business.spi.specification.SpecificationTranslator;
import org.seedstack.mongodb.morphia.internal.DatastoreFactory;
import org.seedstack.mongodb.morphia.internal.specification.MorphiaTranslationContext;

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
    private SpecificationTranslator<MorphiaTranslationContext, CriteriaContainer> specificationTranslator;

    public BaseMorphiaRepository() {

    }

    public BaseMorphiaRepository(Class<A> aggregateRootClass, Class<ID> kClass) {
        super(aggregateRootClass, kClass);
    }

    @Inject
    private void init(DatastoreFactory datastoreFactory, SpecificationTranslator<MorphiaTranslationContext, CriteriaContainer> specificationTranslator) {
        this.datastore = datastoreFactory.createDatastore(getAggregateRootClass());
        this.specificationTranslator = specificationTranslator;
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
        return buildQuery(specification).asList().stream();
    }

    @Override
    public Optional<A> get(ID id) {
        return Optional.ofNullable(datastore.get(getAggregateRootClass(), id));
    }

    @Override
    public boolean contains(Specification<A> specification) {
        return buildQuery(specification).count(new CountOptions().limit(1)) > 0;
    }

    @Override
    public boolean contains(ID id) {
        return datastore.find(getAggregateRootClass()).filter(Mapper.ID_KEY, id).count(new CountOptions().limit(1)) > 0;
    }

    @Override
    public long count(Specification<A> specification) {
        return buildQuery(specification).count();
    }

    @Override
    public long size() {
        return datastore.getCount(getAggregateRootClass());
    }

    @Override
    public long remove(Specification<A> specification) throws AggregateNotFoundException {
        return datastore.delete(buildQuery(specification)).getN();
    }

    @Override
    public void remove(ID id) throws AggregateNotFoundException {
        checkExactlyOneAggregateRemoved(datastore.delete(getAggregateRootClass(), id).getN(), id);
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

    private Query<A> buildQuery(Specification<A> specification) {
        Query<A> query = datastore.createQuery(getAggregateRootClass());
        specificationTranslator.translate(
                specification,
                new MorphiaTranslationContext<>(query)
        );
        return query;
    }
}