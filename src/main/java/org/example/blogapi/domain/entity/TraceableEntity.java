package org.example.blogapi.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class TraceableEntity {

    /**
     * MySQL best practice: use IDENTITY (AUTO_INCREMENT).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Automatically set on INSERT by Hibernate.
     * nullable=false ensures DB integrity.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically updated on UPDATE by Hibernate.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Optimistic locking to prevent lost updates (concurrent modifications).
     */
    @Version
    @Column(nullable = false)
    private Long version;
}