/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.mongodb.morphia.fixtures.product;

import org.mongodb.morphia.annotations.Embedded;
import org.seedstack.business.domain.BaseValueObject;

@Embedded
public class Picture extends BaseValueObject {
    private String name;
    private Long productId;

    public Picture(String name, Long productId) {
        super();
        this.name = name;
        this.productId = productId;
    }


    public Picture() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
