package org.pacs.pacs_mobile_application.data;


import org.pacs.pacs_mobile_application.pojo.requestmodel.LoginModel;
import org.pacs.pacs_mobile_application.pojo.requestmodel.RegistrationModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.AccessAttemptModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.UserInfoModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BackEndApi {

    @POST("employee/register")
     Call<EmployeeAttributesModel> registerEmployee(@Body RegistrationModel registrationModel);
    @POST("visitor/register")
     Call<VisitorAttributesModel> registerVisitor(@Body RegistrationModel registrationModel);
    @POST("employee/login")
     Call<EmployeeAttributesModel> validateEmployee(@Body LoginModel loginModel);
    @POST("visitor/login")
     Call<VisitorAttributesModel> validateVisitor(@Body LoginModel loginModel);
    @GET("employee/find/info/email/{email}")
    Call<UserInfoModel> findEmployeeInfo(@Path("email") String email);
    @GET("visitor/find/info/email/{email}")
     Call<UserInfoModel> findVisitorInfo(@Path("email") String email);
    @GET("visitor/find/history/id/{id}")
    Call<List<AccessAttemptModel>> findVisitorHistory(@Path("id") String id);
    @GET("employee/find/history/id/{id}")
    Call<List<AccessAttemptModel>>  findEmployeeHistory(@Path("id") String id);

}
