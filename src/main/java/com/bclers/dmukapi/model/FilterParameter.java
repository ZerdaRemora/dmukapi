package com.bclers.dmukapi.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FilterParameter
{
    private int start;
    private int length;
    private List<SortParameter> order;
    private Map<String, String> search;
}
