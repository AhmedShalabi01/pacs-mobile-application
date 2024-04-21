package org.pacs.pacs_mobile_application.data;


import org.pacs.pacs_mobile_application.pojo.requestmodel.LoginModel;
import org.pacs.pacs_mobile_application.pojo.requestmodel.RegistrationModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.UserInfoModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BackEndApi {

    @POST("employee/register")
    public Call<EmployeeAttributesModel> registerEmployee(@Body RegistrationModel registrationModel);
    @POST("visitor/register")
    public Call<VisitorAttributesModel> registerVisitor(@Body RegistrationModel registrationModel);
    @POST("employee/login")
    public Call<EmployeeAttributesModel> validateEmployee(@Body LoginModel loginModel);
    @POST("visitor/login")
    public Call<VisitorAttributesModel> validateVisitor(@Body LoginModel loginModel);
    @GET("employee/find/email/{email}")
    public Call<UserInfoModel> findEmployeeInfo(@Path("email") String email);
    @GET("visitor/find/email/{email}")
    public Call<UserInfoModel> findVisitorInfo(@Path("email") String email);

}
