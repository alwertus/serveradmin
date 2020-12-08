package ru.alwertus.serveradmin.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  // везде прописываю доступы при помощи аннотации @PreAuthorize
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()   // механизм защиты от угроз, отключаем
                .authorizeRequests()
                .antMatchers("/").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/**").hasAnyRole(Role.ADMIN.name(), Role.USER.name())   // проверяем доступ по текущим ролям
//                .antMatchers(HttpMethod.GET, "/api/**").hasAuthority(Permission.DEVELOPERS_READ.getPermission())  // проверяе доступ по текущим разрешениям
//                .antMatchers(HttpMethod.POST, "/api/**").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())
//                .antMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())
//                .antMatchers("/login").permitAll()
//                .antMatchers("/logout").permitAll()
                .anyRequest()
                .authenticated()

                .and()
//                .httpBasic()  // базовая аутентификация
                .formLogin()    // аутентификация - форма логина
                .loginPage("/auth/login").permitAll()   // определяем кастомную страницу логина, и разрешаем её всем
                .defaultSuccessUrl("/auth/success")     // если логин прошёл удачно - перенаправляем
                .and()

                .logout()//.permitAll()
                .logoutRequestMatcher(                  // настраиваем логаут
                        new AntPathRequestMatcher(      // requestMatcher должен быть обработан AntPathRequestMatcher
                                "/auth/logout", // по указаной ссылке
                                "POST"       // с указанным методом
                                )
                        )
                .invalidateHttpSession(true)            // инвалидируем сессию
                .clearAuthentication(true)              // очищаем аутентификацию
                .deleteCookies("JSESSIONID")            // чистим куки
                .logoutSuccessUrl("/auth/login")        // перенаправляем в случае удачного логаута
        ;
    }
/*
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        System.out.println("USER");
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("admin"))
                        .authorities(Role.ADMIN.getAuthorities())
//                    .roles(Role.ADMIN.name())
                    .build(),
                User.builder()
                        .username("user")
                        .password(passwordEncoder().encode("user"))
                        .authorities(Role.USER.getAuthorities())
//                        .roles(Role.USER.name())
                        .build()

        );
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        System.out.println("PASSWORD");
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    protected DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }
}
