package com.smart.smartAI.data;

import java.util.Locale;

/**
 * Данные одного тика ротации игрока
 */
public class TickData {
    public final float deltaYaw;
    public final float deltaPitch;
    public final float accelYaw;
    public final float accelPitch;
    public final float jerkYaw;
    public final float jerkPitch;
    public final float gcdErrorYaw;
    public final float gcdErrorPitch;

    public TickData(float deltaYaw, float deltaPitch, float accelYaw, float accelPitch,
                    float jerkYaw, float jerkPitch, float gcdErrorYaw, float gcdErrorPitch) {
        this.deltaYaw = deltaYaw;
        this.deltaPitch = deltaPitch;
        this.accelYaw = accelYaw;
        this.accelPitch = accelPitch;
        this.jerkYaw = jerkYaw;
        this.jerkPitch = jerkPitch;
        this.gcdErrorYaw = gcdErrorYaw;
        this.gcdErrorPitch = gcdErrorPitch;
    }

    public static String getHeader() {
        return "is_cheating,delta_yaw,delta_pitch,accel_yaw,accel_pitch,jerk_yaw,jerk_pitch,gcd_error_yaw,gcd_error_pitch";
    }

    public String toCsv(boolean isCheating) {
        return String.format(Locale.US, "%d,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f",
                isCheating ? 1 : 0, deltaYaw, deltaPitch, accelYaw, accelPitch,
                jerkYaw, jerkPitch, gcdErrorYaw, gcdErrorPitch);
    }
}
