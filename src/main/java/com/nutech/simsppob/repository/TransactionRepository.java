package com.nutech.simsppob.repository;

import com.nutech.simsppob.model.Transaction;
import com.nutech.simsppob.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query(value = "select count(*) from transaction t where t.user_account_id = :#{#userAccount.id}", nativeQuery = true)
    Integer countByUserAccount(@Param("userAccount") UserAccount userAccountByJwt);
    @Query(value = "select * from transaction t where t.user_account_id = :#{#userAccount.id} limit :limit offset :offset", nativeQuery = true)
    List<Transaction> findByUserAccount(@Param("userAccount") UserAccount userAccountByJwt,
                                        @Param("limit") Integer limit,
                                        @Param("offset") Integer offset);

}
