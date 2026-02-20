package org.example.blogapi.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CategoryDto {

    private Long id;

    @NotBlank
    @Length(min = 2, max = 120)
    private String name;

    private String slug;

}
