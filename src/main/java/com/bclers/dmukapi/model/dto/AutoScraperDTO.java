package com.bclers.dmukapi.model.dto;

import lombok.Getter;
import lombok.Setter;

public class AutoScraperDTO
{
    @Getter
    @Setter
    private boolean useBBC;

    @Getter
    @Setter
    private boolean useDM;

    @Getter
    @Setter
    private boolean useUKP;
}
