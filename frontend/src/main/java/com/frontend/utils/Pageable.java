package com.frontend.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Pageable{

    private long pageNumber;
    private long pageSize;
    private PageSort sort;
    private long offset;
    private boolean unpaged;
    private boolean paged;
}
