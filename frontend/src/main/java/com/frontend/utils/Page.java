package com.frontend.utils;

import java.util.List;

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
public class Page<T> {

    private List<T> content;
    private Pageable pageable;
    private long totalPages;
    private long totalElements;
    private boolean last;
    private long numberOfElements;
    private long size;
    private long number;
    private PageSort sort;
    private boolean first;
    private boolean empty;
}
