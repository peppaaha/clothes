package com.example.clothes;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class TrueWeatherBean implements Serializable {

    /**
     * status : ok
     * api_version : v2.5
     * api_status : active
     * lang : zh_CN
     * unit : metric
     * tzshift : 28800
     * timezone : Asia/Taipei
     * server_time : 1611152320
     * location : [25.1552,121.6544]
     * result : {"realtime":{"status":"ok","temperature":14.16,"humidity":0.84,"cloudrate":0,"skycon":"CLEAR_NIGHT","visibility":9,"dswrf":0,"wind":{"speed":24.12,"direction":45},"pressure":100186.9,"apparent_temperature":10.4,"precipitation":{"local":{"status":"ok","datasource":"radar","intensity":0},"nearest":{"status":"ok","distance":5,"intensity":2}},"air_quality":{"pm25":14,"pm10":0,"o3":0,"so2":0,"no2":0,"co":0,"aqi":{"chn":22,"usa":0},"description":{"usa":"","chn":"优"}},"life_index":{"ultraviolet":{"index":0,"desc":"无"},"comfort":{"index":7,"desc":"冷"}}},"primary":0}
     */

    private String status;
    private String api_version;
    private String api_status;
    private String lang;
    private String unit;
    private int tzshift;
    private String timezone;
    private int server_time;
    private ResultBean result;
    private List<Double> location;

    @Data
    public static class ResultBean implements Serializable {
        /**
         * realtime : {"status":"ok","temperature":14.16,"humidity":0.84,"cloudrate":0,"skycon":"CLEAR_NIGHT","visibility":9,"dswrf":0,"wind":{"speed":24.12,"direction":45},"pressure":100186.9,"apparent_temperature":10.4,"precipitation":{"local":{"status":"ok","datasource":"radar","intensity":0},"nearest":{"status":"ok","distance":5,"intensity":2}},"air_quality":{"pm25":14,"pm10":0,"o3":0,"so2":0,"no2":0,"co":0,"aqi":{"chn":22,"usa":0},"description":{"usa":"","chn":"优"}},"life_index":{"ultraviolet":{"index":0,"desc":"无"},"comfort":{"index":7,"desc":"冷"}}}
         * primary : 0
         */

        private RealtimeBean realtime;
        private int primary;

        @Data
        public static class RealtimeBean implements Serializable {
            /**
             * status : ok
             * temperature : 14.16
             * humidity : 0.84
             * cloudrate : 0.0
             * skycon : CLEAR_NIGHT
             * visibility : 9.0
             * dswrf : 0.0
             * wind : {"speed":24.12,"direction":45}
             * pressure : 100186.9
             * apparent_temperature : 10.4
             * precipitation : {"local":{"status":"ok","datasource":"radar","intensity":0},"nearest":{"status":"ok","distance":5,"intensity":2}}
             * air_quality : {"pm25":14,"pm10":0,"o3":0,"so2":0,"no2":0,"co":0,"aqi":{"chn":22,"usa":0},"description":{"usa":"","chn":"优"}}
             * life_index : {"ultraviolet":{"index":0,"desc":"无"},"comfort":{"index":7,"desc":"冷"}}
             */

            private String status;
            private double temperature;
            private double humidity;
            private double cloudrate;
            private String skycon;
            private double visibility;
            private double dswrf;
            private WindBean wind;
            private double pressure;
            private double apparent_temperature;
            private PrecipitationBean precipitation;
            private AirQualityBean air_quality;
            private LifeIndexBean life_index;

            public double getTemperature(){
                return temperature ;
            }

            @Data
            public static class WindBean implements Serializable {
                /**
                 * speed : 24.12
                 * direction : 45.0
                 */

                private double speed;
                private double direction;
            }

            @Data
            public static class PrecipitationBean implements Serializable {
                /**
                 * local : {"status":"ok","datasource":"radar","intensity":0}
                 * nearest : {"status":"ok","distance":5,"intensity":2}
                 */

                private LocalBean local;
                private NearestBean nearest;

                @Data
                public static class LocalBean implements Serializable {
                    /**
                     * status : ok
                     * datasource : radar
                     * intensity : 0.0
                     */

                    private String status;
                    private String datasource;
                    private double intensity;
                }

                @Data
                public static class NearestBean implements Serializable {
                    /**
                     * status : ok
                     * distance : 5.0
                     * intensity : 2.0
                     */

                    private String status;
                    private double distance;
                    private double intensity;
                }
            }

            @Data
            public static class AirQualityBean implements Serializable {
                /**
                 * pm25 : 14
                 * pm10 : 0
                 * o3 : 0
                 * so2 : 0
                 * no2 : 0
                 * co : 0
                 * aqi : {"chn":22,"usa":0}
                 * description : {"usa":"","chn":"优"}
                 */

                private int pm25;
                private int pm10;
                private int o3;
                private int so2;
                private int no2;
                private int co;
                private AqiBean aqi;
                private DescriptionBean description;

                @Data
                public static class AqiBean implements Serializable {
                    /**
                     * chn : 22
                     * usa : 0
                     */

                    private int chn;
                    private int usa;
                }

                @Data
                public static class DescriptionBean implements Serializable {
                    /**
                     * usa :
                     * chn : 优
                     */

                    private String usa;
                    private String chn;
                }
            }

            @Data
            public static class LifeIndexBean implements Serializable {
                /**
                 * ultraviolet : {"index":0,"desc":"无"}
                 * comfort : {"index":7,"desc":"冷"}
                 */

                private UltravioletBean ultraviolet;
                private ComfortBean comfort;

                @Data
                public static class UltravioletBean implements Serializable {
                    /**
                     * index : 0.0
                     * desc : 无
                     */

                    private double index;
                    private String desc;
                }

                @Data
                public static class ComfortBean implements Serializable {
                    /**
                     * index : 7
                     * desc : 冷
                     */

                    private int index;
                    private String desc;
                }
            }
        }
    }
}
