package com.authenthication.auth_session.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.authenthication.auth_session.Entity.User;
import java.util.Optional;


@EnableJpaRepositories
@Repository
public interface UserRepo extends JpaRepository<User,Integer> {


    Optional<User> findOneByEmailAndPassword(String email, String password);

    User findByEmail(String email);

}


