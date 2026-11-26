package no.hvl.dat108.booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registrerer interceptoren og seier kva stiar som er opne utan innlogging.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InnloggingInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/registrer",
                        "/css/**",
                        "/error");
    }
}
