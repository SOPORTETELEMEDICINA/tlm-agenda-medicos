package net.amentum.niomedic.medicos.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef =  "agendaMedicoEntityManagerFactory",
        transactionManagerRef = "agendaMedicoTransactionManager",
        basePackages  = { "net.amentum.niomedic.medicos.DbAgendaMedicos" }
)
public class DBAgendaMedicosConfig {

    @Bean(name="agendaMedicoDataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource agendaMedicoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "agendaMedicoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean agendaMedicoEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                              @Qualifier("agendaMedicoDataSource") DataSource agendaMedicoDataSource) {
        return builder
                .dataSource(agendaMedicoDataSource)
                .packages("net.amentum.niomedic.medicos.DbAgendaMedicos")
                .persistenceUnit("db-agenda-medicos")
                .properties(jpaProperties())
                .build();
    }

    @Bean(name = "agendaMedicoTransactionManager")
    public PlatformTransactionManager agendaMedicoTransactionManager(
            @Qualifier("agendaMedicoEntityManagerFactory") EntityManagerFactory agendaMedicoEntityManagerFactory) {
        return new JpaTransactionManager(agendaMedicoEntityManagerFactory);
    }

    protected Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        return props;
    }
}
