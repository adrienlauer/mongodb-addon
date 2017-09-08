/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal.specification;

import org.mongodb.morphia.query.CriteriaContainer;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.specification.IdentitySpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;

public class MorphiaIdentityConverter<A extends AggregateRoot<ID>, ID> implements SpecificationConverter<IdentitySpecification<A, ID>, MorphiaQueryContext<A>, CriteriaContainer> {
    @Override
    public CriteriaContainer convert(IdentitySpecification<A, ID> specification, MorphiaQueryContext<A> context, SpecificationTranslator<MorphiaQueryContext<A>, CriteriaContainer> translator) {
        context.setFieldEnd("_id");
        // We avoid using equal() because Morphia optimizes it without operator ("someAttr": "someVal")
        // Thus generating an invalid query when trying to negate it ("$not": "someVal")
        return context.pickFieldEnd().not().notEqual(specification.getExpectedIdentifier());
    }
}
