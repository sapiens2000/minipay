package com.minipay.account;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SavingsAccount s where s.id = :id")
    Optional<SavingsAccount> getSavingsAccountWithLock(@Param("id") Long id);


    List<SavingsAccount> findByUserIdOrderByIdAsc(Long id);
}
