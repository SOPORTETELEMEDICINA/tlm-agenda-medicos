package net.amentum.niomedic.medicos.configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @author victor de la Cruz
 */
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**").authorizeRequests()
                .antMatchers("/v2/api-docs**","/info", "medicos-auth/obtenerPorEspecialidad**",
                        "catalogo-especialidades-auth/findAll", "eventos-auth/search", "/eventos-auth**")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/medicos-auth/obtenerPorEspecialidad**").permitAll()
                .antMatchers(HttpMethod.GET, "/catalogo-especialidades-auth/findAll**").permitAll()
                .antMatchers(HttpMethod.GET, "/eventos-auth/search**").permitAll()
                .antMatchers(HttpMethod.POST, "/eventos-auth**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()
                .httpBasic().disable();
    }
}

