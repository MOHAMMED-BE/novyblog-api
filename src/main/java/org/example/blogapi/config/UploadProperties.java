package org.example.blogapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.upload")
public record UploadProperties(
        String rootDir,
        String publicBase
) {
}
