package pt.ua.opendoors;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DataFromPersistenceClient {

    @POST("temperature")
    Call<String> getTemperatureStats(@Body String dfp);

    @POST("current/temperature")
    Call<String> getCurrentTemperature(@Body String dfp);
}
