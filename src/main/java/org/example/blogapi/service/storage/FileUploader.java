package org.example.blogapi.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    /**
     * @param file        multipart file
     * @param relativeDir directory relative to upload root, e.g. "articles_thumbnail"
     * @return public URL (e.g. "/uploads/articles_thumbnail/abc123.jpg")
     */
    String upload(MultipartFile file, String relativeDir);
}
