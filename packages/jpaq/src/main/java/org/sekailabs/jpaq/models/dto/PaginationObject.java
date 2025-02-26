package org.sekailabs.jpaq.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationObject {
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;
}
