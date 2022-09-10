package com.itbatia.app.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class BucketDTO {

    @NotEmpty(message = "Введите название бакета")
    @Size(max = 50, message = "Название бакета должно быть до 50 символов длиной")
    @NotBlank(message = "Название бакета не может состоять только из пробелов")
    private String bucketName;
}
