package com.minipay.user;


import com.minipay.account.Account;
import com.minipay.account.AccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static class UserFactory {
        public static UserCreateRequest createUserCreateRequest() {
            return new UserCreateRequest("default", "default");
        }
    }

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE account");
        jdbcTemplate.execute("TRUNCATE TABLE `user`");
        jdbcTemplate.execute("TRUNCATE TABLE savings_account");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }


//    @AfterEach
//    void tearDown(){
//        accountRepository.deleteAll();
//        userRepository.deleteAll();
//    }

    @Test
    @Transactional
    void test_register_success() {
        userService.register(UserFactory.createUserCreateRequest());

        User user = userRepository.findByUsername("default").orElseThrow();
        Account account = accountRepository.findByUserId(user.getId()).orElseThrow();

        assertThat(user.getUsername()).isEqualTo("default");
        assertThat(user.getPassword()).isEqualTo("default");
        assertThat(user.getDailyLimit()).isEqualTo(3_000_000L);
        assertThat(user.getUsed()).isEqualTo(0L);

        assertThat(account.getBalance()).isEqualTo(0L);
        assertThat(account.isMainAccount()).isTrue();
    }

    @Test
    void test_already_registered() {

    }

    @Test
    void test_register_concurrency() throws InterruptedException {
        int threadCount = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        try {
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);

            Callable<Void> task = () -> {
                try {
                    startLatch.await();
                    userService.register(UserFactory.createUserCreateRequest());
                } catch (DataIntegrityViolationException e) {
                    throw e;
                } finally {
                    doneLatch.countDown();
                }
                return null;
            };

            List<Future<Void>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(pool.submit(task));
            }

            startLatch.countDown();
            doneLatch.await();

            int successCount = 0;
            int failCount = 0;
            for (Future<Void> f : futures) {
                try {
                    f.get(); // SQL 유니크 제약 예외 발생 여부만 확인 (반환값 없음)
                    successCount++;
                } catch (ExecutionException e) {
                    failCount++;

                }
            }

            // 성공 / 실패 횟수 검증
            assertThat(successCount).isEqualTo(1);
            assertThat(failCount).isEqualTo(threadCount - 1);

            // 데이터 검증
            User user = userRepository.findByUsername("default").orElseThrow();
            Account account = accountRepository.findByUserId(user.getId()).orElseThrow();

            assertThat(user.getUsername()).isEqualTo("default");
            assertThat(user.getPassword()).isEqualTo("default");
            assertThat(user.getDailyLimit()).isEqualTo(3_000_000L);
            assertThat(user.getUsed()).isEqualTo(0L);

            assertThat(account.getBalance()).isEqualTo(0L);
            assertThat(account.isMainAccount()).isTrue();


        }finally {
            pool.shutdown();
        }
    }
}