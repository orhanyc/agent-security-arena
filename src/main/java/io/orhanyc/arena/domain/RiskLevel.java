package io.orhanyc.arena.domain;

public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static RiskLevel fromScore(double score) {
        if (score >= 0.85) return CRITICAL;
        if (score >= 0.60) return HIGH;
        if (score >= 0.30) return MEDIUM;
        return LOW;
    }
}
