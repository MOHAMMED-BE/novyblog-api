package org.example.blogapi.service.storage.impl;

import org.example.blogapi.config.UploadProperties;
import org.example.blogapi.service.exceptions.FileStorageException;
import org.example.blogapi.service.storage.FileUploader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploaderImpl implements FileUploader {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_BYTES = 5L * 1024 * 1024;

    private final UploadProperties props;

    public FileUploaderImpl(UploadProperties props) {
        this.props = props;
    }

    @Override
    public String upload(MultipartFile file, String relativeDir) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File is required");
        }

        if (file.getSize() > MAX_BYTES) {
            throw new FileStorageException("File too large (max 5MB)");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String ext = getExtension(original);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new FileStorageException("Unsupported file type. Allowed: jpg, jpeg, png, webp");
        }

        String filename = UUID.randomUUID() + "." + ext;

        Path root = Path.of(props.rootDir()).toAbsolutePath().normalize();
        Path dir = root.resolve(relativeDir).normalize();

        if (!dir.startsWith(root)) {
            throw new FileStorageException("Invalid upload directory");
        }

        try {
            Files.createDirectories(dir);

            Path target = dir.resolve(filename).normalize();
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new FileStorageException("Could not store file", e);
        }

        return props.publicBase() + "/" + relativeDir + "/" + filename;
    }

    private static String getExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return "";
        return name.substring(dot + 1).toLowerCase();
    }
}
