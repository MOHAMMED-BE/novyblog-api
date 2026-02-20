package org.example.blogapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final UploadProperties props;

    public StaticResourceConfig(UploadProperties props) {
        this.props = props;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Path.of(props.rootDir()).toAbsolutePath().normalize();
        registry.addResourceHandler(props.publicBase() + "/**")
                .addResourceLocations(root.toUri().toString());
    }
}
