package pt.ua.opendoors;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DataFromPersistenceClient {

    @POST("temperature")
    Call<String> getTemperatureStats(@Body String dfp);

    @POST("current/temperature")
    Call<String> getCurrentTemperature(@Body String dfp);

    @POST("light")
    Call<String> getLightStats(@Body String dfp);

    @POST("current/light")
    Call<String> getCurrentLight(@Body String dfp);

    @POST("employees/store")
    Call<String> getAllEmployees(@Body String dfp);

    @POST("newconsumer2")
    @Headers({"Content-Type:application/vnd.kafka.v2+json","Accept:application/vnd.kafka.v2+json"})
    Call<String> getKafkaCreate(@Body String dfp);

    @POST("subscription")
    @Headers({"Content-Type:application/vnd.kafka.v2+json"})
    Call<String> getKafkaSubscribe(@Body String dfp);


    @POST("clients")
    Call<String> getClients(@Body String dfp);

}
