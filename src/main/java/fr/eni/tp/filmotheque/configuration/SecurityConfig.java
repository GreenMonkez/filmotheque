package fr.eni.tp.filmotheque.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	UserDetailsManager users(DataSource dataSource) {

		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
		jdbcUserDetailsManager.setUsersByUsernameQuery(
				"select email, password, 'true' as enabled from membre where email = ?");
		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
				"select email, role from roles r join membre m on m.admin=r.is_admin  where email = ?");

		return jdbcUserDetailsManager;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorize) -> authorize
					.requestMatchers("/").permitAll()
					.requestMatchers("/css/*").permitAll()
					.requestMatchers("/images/*").permitAll()
					.requestMatchers(HttpMethod.GET,"/films").permitAll()
					.requestMatchers(HttpMethod.GET,"/films/detail").permitAll()
					.requestMatchers(HttpMethod.GET,"/films/creer").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST,"/films/creer").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/avis/creer").hasRole("MEMBRE")
					.requestMatchers(HttpMethod.POST,"/avis/creer").hasRole("MEMBRE")
					.requestMatchers("/login").permitAll()
					.requestMatchers("/signin").permitAll()
					.requestMatchers("/session").permitAll()
					.requestMatchers("/logout").permitAll()
					.anyRequest().denyAll()//interdit l'accès aux urls non configurée
					)
				.httpBasic(Customizer.withDefaults())
				.formLogin((formLogin) -> formLogin
	 				.loginPage("/login")
	 				.defaultSuccessUrl("/session")
	 				)
				.logout((logout) -> logout
						.invalidateHttpSession(true)
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
						.logoutSuccessUrl("/")
					);
		
		return http.build();
	}
}
