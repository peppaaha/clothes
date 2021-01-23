package com.example.clothes.stSonActivity;


import java.io.Serializable;
import java.util.List;

    @lombok.Data
    public class WeatherBean implements Serializable {

        /**
         * status : ok
         * query : 北京
         * places : [{"id":"ChIJuSwU55ZS8DURiqkPryBWYrk","location":{"lat":39.9041999,"lng":116.4073963},"place_id":"g-ChIJuSwU55ZS8DURiqkPryBWYrk","name":"北京市","formatted_address":"中国北京市"},{"id":"B000A83M61","name":"北京西站","formatted_address":"中国 北京市 丰台区 莲花池东路118号","location":{"lat":39.89491,"lng":116.322056},"place_id":"a-B000A83M61"},{"id":"B000A833V8","name":"北京北站","formatted_address":"中国 北京市 西城区 北滨河路1号","location":{"lat":39.944876,"lng":116.353063},"place_id":"a-B000A833V8"},{"id":"B000A350CB","name":"北京东站","formatted_address":"中国 北京市 朝阳区 百子湾路7号","location":{"lat":39.90242,"lng":116.485079},"place_id":"a-B000A350CB"},{"id":"BV10006813","name":"北京站(地铁站)","formatted_address":"中国 北京市 东城区 2号线","location":{"lat":39.904983,"lng":116.427287},"place_id":"a-BV10006813"}]
         */

        private String status;
        private String query;
        private List<PlacesBean> places;

        public List<PlacesBean> getPlaces() {
            return places;
        }

        public void setPlaces(List<PlacesBean> places) {
            this.places = places;
        }

        @lombok.Data
        public static class PlacesBean implements Serializable {
            /**
             * id : ChIJuSwU55ZS8DURiqkPryBWYrk
             * location : {"lat":39.9041999,"lng":116.4073963}
             * place_id : g-ChIJuSwU55ZS8DURiqkPryBWYrk
             * name : 北京市
             * formatted_address : 中国北京市
             */

            private String id;
            private LocationBean location;
            private String place_id;
            private String name;
            private String formatted_address;

            public String getName(){
                return name ;
            }
            public LocationBean getLocation() { return location; }

            @lombok.Data
            public static class LocationBean implements Serializable {
                /**
                 * lat : 39.9041999
                 * lng : 116.4073963
                 */

                private double lat;
                private double lng;

                public double getLng() {
                    return lng;
                }

                public double getLat() {
                    return lat;
                }
            }
        }
    }
