package com.ignacio.gradecalc.strategy;

import com.ignacio.gradecalc.enums.UniversityPreset;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GradingStrategyFactory {

    private final Map<String, GradingStrategy> strategies;
    private final GradingStrategy defaultStrategy;

    public GradingStrategyFactory(List<GradingStrategy> strategyList) {
        this.strategies = new HashMap<>();
        GradingStrategy standard = null;

        for (GradingStrategy strategy : strategyList) {
            strategies.put(strategy.getUniversityCode(), strategy);
            if ("STANDARD".equals(strategy.getUniversityCode())) {
                standard = strategy;
            }
        }

        this.defaultStrategy = standard;
    }

    /**
     * Get the appropriate strategy for a university preset.
     * Falls back to STANDARD if no specific strategy exists.
     */
    public GradingStrategy getStrategy(UniversityPreset universityPreset) {
        if (universityPreset == null) {
            return defaultStrategy;
        }

        String code = universityPreset.name();
        return strategies.getOrDefault(code, defaultStrategy);
    }

    /**
     * Get a strategy by university code string.
     */
    public GradingStrategy getStrategy(String universityCode) {
        if (universityCode == null || universityCode.isBlank()) {
            return defaultStrategy;
        }

        return strategies.getOrDefault(universityCode.toUpperCase(), defaultStrategy);
    }
}