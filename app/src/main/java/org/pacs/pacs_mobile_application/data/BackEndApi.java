package org.pacs.pacs_mobile_application.data;


import org.pacs.pacs_mobile_application.pojo.requestmodel.LoginModel;
import org.pacs.pacs_mobile_application.pojo.requestmodel.RegistrationModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BackEndApi {

    @POST("employee/register")
    public Call<EmployeeAttributesModel> registerEmployee(@Body RegistrationModel registrationModel);
    @POST("visitor/register")
    public Call<VisitorAttributesModel> registerVisitor(@Body RegistrationModel registrationModel);

    @POST("employee/login")
    public Call<EmployeeAttributesModel> validateEmployee(@Body LoginModel loginModel);
    @POST("visitor/login")
    public Call<VisitorAttributesModel> validateVisitor(@Body LoginModel loginModel);

}
