package com.nutech.simsppob.repository;

import com.nutech.simsppob.model.UserAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {

    UserAccount findByEmail (String email);

    @Modifying(clearAutomatically=true)
    @org.springframework.transaction.annotation.Transactional
    @Query(value = "update user_account set balance = balance - :amount where email = :email", nativeQuery = true)
    Integer updateBalance(@Param("amount") int amount,
                          @Param("email") String email);
}
