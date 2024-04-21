package org.pacs.pacs_mobile_application.data;

import org.pacs.pacs_mobile_application.pojo.requestmodel.LoginModel;
import org.pacs.pacs_mobile_application.pojo.requestmodel.RegistrationModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.UserInfoModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class BackEndClient {

    private static final String BASE_URL = "http://192.168.1.7:8084/pacs/";
    private final BackEndApi backEndApi;
    private static BackEndClient INSTANCE;

    public BackEndClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        backEndApi = retrofit.create(BackEndApi.class);
    }

    public static BackEndClient getINSTANCE() {
        if (null == INSTANCE){
            INSTANCE = new BackEndClient();
        }
        return INSTANCE;
    }

    public Call<EmployeeAttributesModel> registerEmployee(@Body RegistrationModel registrationModel) {
        return backEndApi.registerEmployee(registrationModel);
    }
    public Call<VisitorAttributesModel> registerVisitor(@Body RegistrationModel registrationModel) {
        return backEndApi.registerVisitor(registrationModel);
    }
    public Call<EmployeeAttributesModel> validateEmployee(@Body LoginModel loginModel) {
        return backEndApi.validateEmployee(loginModel);
    }
    public Call<VisitorAttributesModel> validateVisitor(@Body LoginModel loginModel) {
        return backEndApi.validateVisitor(loginModel);
    }
    public Call<UserInfoModel> findEmployeeInfo(@Path("email") String email) {
        return backEndApi.findEmployeeInfo(email);
    }
    public Call<UserInfoModel> findVisitorInfo(@Path("email") String email) {
        return backEndApi.findVisitorInfo(email);
    }


}
