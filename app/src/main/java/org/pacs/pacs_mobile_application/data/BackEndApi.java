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

    @POST("/login-registration/employee/register")
     Call<EmployeeAttributesModel> registerEmployee(@Body RegistrationModel registrationModel);
    @POST("/login-registration/visitor/register")
     Call<VisitorAttributesModel> registerVisitor(@Body RegistrationModel registrationModel);
    @POST("/login-registration/employee/login")
     Call<EmployeeAttributesModel> validateEmployee(@Body LoginModel loginModel);
    @POST("/login-registration/visitor/login")
     Call<VisitorAttributesModel> validateVisitor(@Body LoginModel loginModel);
    @GET("/login-registration/employee/find/info/email/{email}")
    Call<UserInfoModel> findEmployeeInfo(@Path("email") String email);
    @GET("/login-registration/visitor/find/info/email/{email}")
     Call<UserInfoModel> findVisitorInfo(@Path("email") String email);
    @GET("/access-control/employee/list/history/id/{id}")
    Call<List<AccessAttemptModel>>  findEmployeeHistory(@Path("id") String id);
    @GET("/access-control/visitor/list/history/id/{id}")
    Call<List<AccessAttemptModel>> findVisitorHistory(@Path("id") String id);
}
