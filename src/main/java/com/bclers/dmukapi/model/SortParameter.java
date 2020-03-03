package com.bclers.dmukapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SortParameter
{
    private final String[] columnNames = {"id", "author", "body", "score", "articleTitle", "commentSource", "date"};

    private int column;

    @JsonProperty("dir")
    private String direction;

    public String getColumnName()
    {
        return column < columnNames.length - 1 ? columnNames[column] : columnNames[0];
    }
}
