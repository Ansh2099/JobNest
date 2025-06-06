package com.Job.Application.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyResponse {
    private Long id;
    private String name;
    private String description;
}
