package com.minipay;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CleanUp {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public  void all() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
        entityManager.getMetamodel().getEntities().forEach(entity -> {
            Class<?> javaType = entity.getJavaType();
            Table table = javaType.getAnnotation(Table.class);

            String tableName = (table != null && !table.name().isBlank())
                    ? table.name()
                    : entity.getName();

            // for mysql
            jdbcTemplate.execute("TRUNCATE TABLE " + tableName.toLowerCase());
        });
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
    }
}