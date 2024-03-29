package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditPasswordDTO {

    private String oldPassword;
    private String newPassword;
    private Integer empId;
    private LocalDateTime updateTime;

}
