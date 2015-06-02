package contextproviders.core;

import android.hardware.Sensor;

/**
 * TODO
 *
 * Created by Jose Torres on 3/10/15.
 */
public class ContextProvidersConstants {

    /**
     * TODO
     */
    public class Providers {

        /**
         * TODO
         */
        public static final int BAROMETER = Sensor.TYPE_PRESSURE;

        /**
         * TODO
         */
        public static final int ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;

        /**
         * TODO
         */
        public static final int THERMOMETER = Sensor.TYPE_AMBIENT_TEMPERATURE;

        /**
         * TODO
         */
        public static final int PHOTOMETER = Sensor.TYPE_LIGHT;

        /**
         * TODO
         */
        public static final int PEDOMETER = Sensor.TYPE_STEP_COUNTER;
    }

    /**
     *
     */
    public class Cache {

        /**
         * Constant for cache expiry time of 1 second (in milliseconds)
         */
        public static final long EXPIRY_TIME_ONE_SECOND  = 1  * 1000;

        /**
         * Constant for cache expiry time of 2 seconds (in milliseconds)
         */
        public static final long EXPIRY_TIME_TWO_SECONDS  = 2 * 1000;

        /**
         * Constant for cache expiry time of 3 seconds (in milliseconds)
         */
        public static final long EXPIRY_TIME_THREE_SECONDS  = 3 * 1000;

        /**
         * Constant for cache expiry time of 4 seconds (in milliseconds)
         */
        public static final long EXPIRY_TIME_FOUR_SECONDS  = 4 * 1000;

        /**
         * Constant for cache expiry time of 5 seconds (in milliseconds)
         */
        public static final long EXPIRY_TIME_FIVE_SECONDS  = 5 * 1000;

        /**
         * Constant for disable the cache service capability
         */
        public static final boolean DISABLE_CACHE_SERVICE = true;
    }
}
