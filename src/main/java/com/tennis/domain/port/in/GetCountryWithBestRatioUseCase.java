package com.tennis.domain.port.in;

import com.tennis.domain.model.Statistic;

public interface GetCountryWithBestRatioUseCase {
    Statistic getCountryWithBestWinRatio();
}
