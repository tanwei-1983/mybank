package com.mybank.transaction.domain;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 分页请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    
    @Min(value = 1, message = "page must greater than 0")
    private Integer page = 1;
    
    @Min(value = 1, message = "size of page must greater than 0")
    @Max(value = 100, message = "size of page mus less than 100")
    private Integer size = 20;
}
