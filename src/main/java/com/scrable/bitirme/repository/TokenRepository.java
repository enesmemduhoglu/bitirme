package com.scrable.bitirme.repository;


import com.scrable.bitirme.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {

    @Query("""
select t from Token t inner join Users u on t.users.id = u.id
where t.users.id = :userId and t.loggedOut = false
""")
    List<Token> findAllAccessTokensByUser(Integer userId);

    Optional<Token> findByAccessToken(String token);

    Optional<Token > findByRefreshToken(String token);

    void deleteByUserId(Integer userId);
}
