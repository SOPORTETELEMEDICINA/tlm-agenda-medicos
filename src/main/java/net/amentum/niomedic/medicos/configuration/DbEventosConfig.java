package net.amentum.niomedic.medicos.configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "eventosEntityManagerFactory",
        transactionManagerRef = "eventosTransactionManager",
        basePackages = { "net.amentum.niomedic.medicos.DbEventos" }
)
public class DbEventosConfig {

    @Bean(name="eventosDataSource")
    @ConfigurationProperties(prefix="spring.second-datasource")
    public DataSource eventosDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "eventosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean eventosEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                                @Qualifier("eventosDataSource") DataSource eventosDataSource) {
        return builder
                .dataSource(eventosDataSource)
                .packages("net.amentum.niomedic.medicos.DbEventos")
                .persistenceUnit("nio-eventos")
                .properties(jpaProperties())
                .build();
    }

    @Bean(name = "eventosTransactionManager")
    public PlatformTransactionManager eventosTransactionManager(
            @Qualifier("eventosEntityManagerFactory") EntityManagerFactory eventosEntityManagerFactory) {
        return new JpaTransactionManager(eventosEntityManagerFactory);
    }

    protected Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        return props;
    }
}