/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia;

import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.business.domain.Repository;
import org.seedstack.mongodb.morphia.fixtures.user.Address;
import org.seedstack.mongodb.morphia.fixtures.user.User;
import org.seedstack.seed.it.AbstractSeedIT;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class MorphiaRepositoryIT extends AbstractSeedIT {
    @Inject
    @Morphia
    private Repository<User, Long> userRepository;

    @Before
    public void setUp() throws Exception {
        userRepository.clear();
    }

    @Test
    public void addAndGet() throws Exception {
        User user1 = createUser(1L, "someFirstName", "someLastName");
        userRepository.add(user1);
        Optional<User> loaded = userRepository.get(1L);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName()).isEqualTo("someFirstName");
        assertThat(loaded.get().getLastname()).isEqualTo("someLastName");
    }

    @Test
    public void addRemoveAndGet() throws Exception {
        User user1 = createUser(1L, "someFirstName", "someLastName");
        userRepository.add(user1);
        assertThat(userRepository.get(1L)).isPresent();
        userRepository.remove(user1);
        assertThat(userRepository.get(1L)).isNotPresent();
    }

    @Test
    public void update() {
        userRepository.add(createUser(200L, "Robert", "SMITH"));
        userRepository.update(createUser(200L, "Jane", "SMITH"));
        assertThat(userRepository.get(200L).get().getName()).isEqualTo("Jane");
    }

    @Test(expected = AggregateNotFoundException.class)
    public void updateNonExistent() {
        userRepository.update(createUser(100L, "Robert", "SMITH"));
        fail("should not have updated");
    }

    @Test
    public void clear() {
        userRepository.add(createUser(400L, "Robert", "SMITH"));
        userRepository.add(createUser(401L, "Jayne", "SMITH"));
        assertThat(userRepository.get(400L)).isPresent();
        assertThat(userRepository.get(401L)).isPresent();
        userRepository.clear();
        assertThat(userRepository.get(400L)).isNotPresent();
        assertThat(userRepository.get(401L)).isNotPresent();
    }

    @Test
    public void contains() {
        userRepository.add(createUser(300L, "Robert", "SMITH"));
        assertThat(userRepository.contains(300L)).isTrue();
        assertThat(userRepository.contains(3010L)).isFalse();
    }

    @Test
    public void size() {
        userRepository.add(createUser(300L, "Robert", "SMITH"));
        userRepository.add(createUser(301L, "Roberta", "SMITH"));
        assertThat(userRepository.size()).isEqualTo(2);
    }

    private User createUser(long id, String firstname, String lastName) {
        return new User(id, firstname, lastName, new Address("France", "75001", "Paris", "Champ Elysee avenue", 1));
    }
}
