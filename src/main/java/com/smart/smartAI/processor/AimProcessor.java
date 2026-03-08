package com.smart.smartAI.processor;

import com.smart.smartAI.data.TickData;
import com.smart.smartAI.util.GcdMath;

/**
 * Обработчик ротаций игрока - вычисляет дельты, ускорения, рывки и GCD ошибки
 */
public class AimProcessor {
    private static final float MAX_DELTA_FOR_GCD = 5.0f;

    private float lastYaw;
    private float lastPitch;
    private float lastDeltaYaw;
    private float lastDeltaPitch;
    private float lastYawAccel;
    private float lastPitchAccel;
    private float currentYawAccel;
    private float currentPitchAccel;
    
    private float lastXRot;
    private float lastYRot;
    private double modeX;
    private double modeY;
    
    private boolean hasLastRotation;

    public void reset() {
        lastYaw = 0;
        lastPitch = 0;
        lastDeltaYaw = 0;
        lastDeltaPitch = 0;
        lastYawAccel = 0;
        lastPitchAccel = 0;
        currentYawAccel = 0;
        currentPitchAccel = 0;
        lastXRot = 0;
        lastYRot = 0;
        modeX = 0;
        modeY = 0;
        hasLastRotation = false;
    }

    public TickData process(float yaw, float pitch) {
        float deltaYaw = hasLastRotation ? normalizeAngle(yaw - lastYaw) : 0;
        float deltaPitch = hasLastRotation ? pitch - lastPitch : 0;
        
        float deltaYawAbs = Math.abs(deltaYaw);
        float deltaPitchAbs = Math.abs(deltaPitch);

        lastYawAccel = currentYawAccel;
        lastPitchAccel = currentPitchAccel;
        
        currentYawAccel = deltaYawAbs - Math.abs(lastDeltaYaw);
        currentPitchAccel = deltaPitchAbs - Math.abs(lastDeltaPitch);

        float jerkYaw = currentYawAccel - lastYawAccel;
        float jerkPitch = currentPitchAccel - lastPitchAccel;

        // Обновление GCD
        if (hasLastRotation) {
            double divisorX = GcdMath.gcd(deltaYawAbs, lastXRot);
            if (deltaYawAbs > 0 && deltaYawAbs < MAX_DELTA_FOR_GCD && divisorX > GcdMath.MINIMUM_DIVISOR) {
                modeX = divisorX;
                lastXRot = deltaYawAbs;
            }

            double divisorY = GcdMath.gcd(deltaPitchAbs, lastYRot);
            if (deltaPitchAbs > 0 && deltaPitchAbs < MAX_DELTA_FOR_GCD && divisorY > GcdMath.MINIMUM_DIVISOR) {
                modeY = divisorY;
                lastYRot = deltaPitchAbs;
            }
        }

        float gcdErrorYaw = calculateGcdError(deltaYaw, modeX);
        float gcdErrorPitch = calculateGcdError(deltaPitch, modeY);

        lastYaw = yaw;
        lastPitch = pitch;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
        hasLastRotation = true;

        return new TickData(deltaYaw, deltaPitch, currentYawAccel, currentPitchAccel,
                jerkYaw, jerkPitch, gcdErrorYaw, gcdErrorPitch);
    }

    private float normalizeAngle(float angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    private float calculateGcdError(float delta, double mode) {
        if (mode == 0) return 0;
        double absDelta = Math.abs(delta);
        double remainder = absDelta % mode;
        double error = Math.min(remainder, mode - remainder);
        return (float) error;
    }
}
