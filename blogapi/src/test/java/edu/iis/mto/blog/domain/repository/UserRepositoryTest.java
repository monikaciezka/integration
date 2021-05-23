package edu.iis.mto.blog.domain.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;
import org.springframework.cache.support.NullValue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Jan");
        user.setEmail("john@domain.com");
        user.setAccountStatus(AccountStatus.NEW);
    }


    @Test
    void shouldFindNoUsersIfRepositoryIsEmpty() {

        List<User> users = repository.findAll();

        assertThat(users, hasSize(0));
    }


    @Test
    void shouldFindOneUsersIfRepositoryContainsOneUserEntity() {
        User persistedUser = entityManager.persist(user);
        List<User> users = repository.findAll();

        assertThat(users, hasSize(1));
        assertThat(users.get(0)
                        .getEmail(),
                equalTo(persistedUser.getEmail()));
    }


    @Test
    void shouldStoreANewUser() {

        User persistedUser = repository.save(user);

        assertThat(persistedUser.getId(), notNullValue());
    }

    @Test
    void shouldFindUserByPartOfName(){
        repository.save(user);
        List<User> testSearchUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(user.getFirstName().substring(0, 1), "tempLastname", "tempEmail");
        assertThat(testSearchUsers.size(), is(1));
        assertThat(true, is(testSearchUsers.get(0).equals(user)));
    }

    @Test
    void shouldNotFindAnyUser(){
        repository.save(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("random", "random", "random");
        assertThat(true, is(foundUsers.isEmpty()));
    }

    @Test
    void shouldFindUserByPartOfLastName(){
        user.setLastName("Kowalski");
        repository.save(user);
        List<User> testSearchUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("username", user.getLastName().substring(0, 2), "tempEmail");
        assertThat(testSearchUsers.size(), is(1));
        assertThat(true, is(testSearchUsers.get(0).equals(user)));
    }

    @Test
    void shouldFindUserByPartOfEmail(){
        repository.save(user);
        User user2 = new User();
        user2.setFirstName("John");
        user2.setEmail("john@interia.com");
        user2.setAccountStatus(AccountStatus.NEW);
        repository.save(user2);
        List<User> testSearchUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("tempName", "tempLastname", "john");
        assertThat(testSearchUsers.size(), is(2));
    }

    @Test
    void shouldFindAllUsers(){
        repository.save(user);
        User user2 = new User();
        user2.setFirstName("Piotr");
        user2.setEmail("piotr@interia.com");
        user2.setAccountStatus(AccountStatus.NEW);
        repository.save(user2);
        List<User> testSearchUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("", "", "");
        assertThat(testSearchUsers.size(), is(2));
    }
}
